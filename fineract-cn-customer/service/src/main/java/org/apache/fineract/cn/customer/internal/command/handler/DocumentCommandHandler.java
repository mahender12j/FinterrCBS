/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.fineract.cn.customer.internal.command.handler;

import org.apache.fineract.cn.customer.api.v1.CustomerEventConstants;
import org.apache.fineract.cn.customer.api.v1.domain.CustomerDocument;
import org.apache.fineract.cn.customer.api.v1.events.DocumentEvent;
import org.apache.fineract.cn.customer.api.v1.events.DocumentPageEvent;
import org.apache.fineract.cn.customer.internal.command.*;
import org.apache.fineract.cn.customer.internal.mapper.DocumentMapper;
import org.apache.fineract.cn.customer.internal.repository.*;

import java.io.IOException;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.apache.fineract.cn.api.util.UserContextHolder;
import org.apache.fineract.cn.command.annotation.Aggregate;
import org.apache.fineract.cn.command.annotation.CommandHandler;
import org.apache.fineract.cn.command.annotation.EventEmitter;
import org.apache.fineract.cn.lang.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Myrle Krantz
 */
@Aggregate
public class DocumentCommandHandler {
    private final DocumentRepository documentRepository;
    private final CustomerRepository customerRepository;
    private final DocumentEntryRepository documentEntryRepository;

    @Autowired
    public DocumentCommandHandler(
            final DocumentRepository documentRepository,
            final DocumentEntryRepository documentEntryRepository,
            final CustomerRepository customerRepository) {
        this.documentRepository = documentRepository;
        this.customerRepository = customerRepository;
        this.documentEntryRepository = documentEntryRepository;
    }

    @Transactional
    @CommandHandler
    @EventEmitter(selectorName = CustomerEventConstants.SELECTOR_NAME, selectorValue = CustomerEventConstants.POST_DOCUMENT_PAGE)
    public DocumentPageEvent process(final CreateDocumentEntryCommand command) {
        CustomerEntity customerEntity = customerRepository.findByIdentifier(command.getCustomeridentifier())
                .orElseThrow(() -> ServiceException.notFound("Customer {0} not found", command.getCustomeridentifier()));

        Boolean findDocument = documentRepository.findByIdentifierAndCustomer(command.getCustomeridentifier());
        if (!findDocument) {
            documentRepository.save(DocumentMapper.map(command.getCustomerDocument(), customerEntity));
        }

        final DocumentEntity documentEntity = documentRepository.findByCustomer(customerEntity);
        List<DocumentEntryEntity> documentEntryEntityList = DocumentMapper.map(command.getCustomerDocument().getKycDocuments(), documentEntity);
        this.documentEntryRepository.save(documentEntryEntityList);
        return new DocumentPageEvent(command.getCustomeridentifier(), command.getCustomeridentifier(), 1);
    }


    @Transactional
    @CommandHandler
    @EventEmitter(selectorName = CustomerEventConstants.SELECTOR_NAME, selectorValue = CustomerEventConstants.PUT_DOCUMENT)
    public DocumentEvent process(final ChangeDocumentStatusCommand command) throws IOException {

        final DocumentEntryEntity existingDocument = documentEntryRepository.findByCustomerIdAndDocumentId(
                command.getCustomerIdentifier(), command.getCustomerDocumentId())
                .orElseThrow(() -> ServiceException.notFound("Document ''{0}'' for customer ''{1}'' not found", command.getCustomerDocumentId(), command.getCustomerIdentifier()));

        existingDocument.setStatus(CustomerDocument.Status.APPROVED.name());
        documentEntryRepository.save(existingDocument);

//        customerRepository.findByIdentifier(command.getCustomerIdentifier())
//                .map(customerEntity -> DocumentMapper.map(command.getCustomerDocumentId(), customerEntity))
//                .ifPresent(documentEntity -> {
//                    documentEntity.setId(existingDocument.getId());
//                    documentRepository.save(documentEntity);
//                });

        return new DocumentEvent(command.getCustomerIdentifier(), command.getCustomerDocumentId().toString());
    }


    @Transactional
    @CommandHandler
    @EventEmitter(selectorName = CustomerEventConstants.SELECTOR_NAME, selectorValue = CustomerEventConstants.PUT_DOCUMENT)
    public DocumentEvent process(final RejectDocumentCommand command) throws IOException {

        final DocumentEntryEntity existingDocument = documentEntryRepository.findByCustomerIdAndDocumentId(
                command.getCustomerIdentifier(), command.getCustomerDocumentId())
                .orElseThrow(() -> ServiceException.notFound("Document ''{0}'' for customer ''{1}'' not found", command.getCustomerDocumentId(), command.getCustomerIdentifier()));

        existingDocument.setStatus(CustomerDocument.Status.REJECTED.name());
        existingDocument.setReasonForReject(command.getReason());
        existingDocument.setRejectedBy(UserContextHolder.checkedGetUser());
        existingDocument.setRejectedOn(LocalDateTime.now(Clock.systemUTC()));
        documentEntryRepository.save(existingDocument);
        return new DocumentEvent(command.getCustomerIdentifier(), command.getCustomerDocumentId().toString());
    }


    @Transactional
    @CommandHandler
    @EventEmitter(selectorName = CustomerEventConstants.SELECTOR_NAME, selectorValue = CustomerEventConstants.DELETE_DOCUMENT)
    public DocumentEvent process(final DeleteDocumentCommand command) throws IOException {
        final DocumentEntryEntity existingDocument = documentEntryRepository.findByCustomerIdAndDocumentId(command.getCustomerIdentifier(), command.getDocumentIdentifier())
                .orElseThrow(() ->
                        ServiceException.notFound("Document ''{0}'' for customer ''{1}'' not found",
                                command.getDocumentIdentifier(), command.getCustomerIdentifier()));
        existingDocument.setStatus(CustomerDocument.Status.DELETED.name());

        documentEntryRepository.save(existingDocument);

        return new DocumentEvent(command.getCustomerIdentifier(), command.getDocumentIdentifier().toString());
    }

    @Transactional
    @CommandHandler
    @EventEmitter(selectorName = CustomerEventConstants.SELECTOR_NAME, selectorValue = CustomerEventConstants.POST_DOCUMENT_COMPLETE)
    public DocumentEvent process(final CompleteDocumentCommand command) throws IOException {
        final DocumentEntity documentEntity = documentRepository.findByCustomerIdAndDocumentIdentifier(
                command.getCustomerIdentifier(),
                command.getDocumentIdentifier())
                .orElseThrow(() -> ServiceException.badRequest("Document not found"));

        documentEntity.setCreatedOn(LocalDateTime.now(Clock.systemUTC()));
        documentEntity.setCreatedBy(UserContextHolder.checkedGetUser());
        documentEntity.setCompleted(true);
        documentRepository.save(documentEntity);


        return new DocumentEvent(command.getCustomerIdentifier(), command.getDocumentIdentifier());
    }
}
