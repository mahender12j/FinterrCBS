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
import org.apache.fineract.cn.customer.api.v1.events.DocumentEvent;
import org.apache.fineract.cn.customer.api.v1.events.DocumentPageEvent;
import org.apache.fineract.cn.customer.internal.command.*;
import org.apache.fineract.cn.customer.internal.mapper.DocumentMapper;
import org.apache.fineract.cn.customer.internal.repository.*;

import java.io.IOException;
import java.time.Clock;
import java.time.LocalDateTime;
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
    private final DocumentPageRepository documentPageRepository;
    private final CustomerRepository customerRepository;
    private final DocumentEntryRepository documentEntryRepository;

    @Autowired
    public DocumentCommandHandler(
            final DocumentRepository documentRepository,
            final DocumentPageRepository documentPageRepository,
            final DocumentEntryRepository documentEntryRepository,
            final CustomerRepository customerRepository) {
        this.documentRepository = documentRepository;
        this.documentPageRepository = documentPageRepository;
        this.customerRepository = customerRepository;
        this.documentEntryRepository = documentEntryRepository;
    }

//    @Transactional
//    @CommandHandler
//    @EventEmitter(selectorName = CustomerEventConstants.SELECTOR_NAME, selectorValue = CustomerEventConstants.POST_DOCUMENT_PAGE)
//    public DocumentPageEvent process(final CreateDocumentEntryCommand command) throws IOException {
//        final DocumentEntity documentEntity = documentRepository.findByCustomerIdAndDocumentIdentifier(
//                command.getCustomerIdentifier(),
//                command.getDocumentIdentifier())
//                .orElseThrow(() -> ServiceException.badRequest("Document not found"));
//
//        final DocumentEntryEntity documentEntryEntity = DocumentMapper.map(command.getDocumentEntry(), documentEntity);
//        documentEntryEntity.setStatus("PENDING");
//        final DocumentPageEntity documentPageEntity = DocumentMapper.map(command.getFile(), 1, documentEntity, documentEntryEntity);
//        documentEntryRepository.save(documentEntryEntity);
//        documentPageRepository.save(documentPageEntity);
//        return new DocumentPageEvent(command.getCustomerIdentifier(), command.getDocumentIdentifier(), 1);
//    }

//    @Transactional
//    @CommandHandler
//    @EventEmitter(selectorName = CustomerEventConstants.SELECTOR_NAME, selectorValue = CustomerEventConstants.POST_DOCUMENT)
//    public DocumentEvent process(final CreateDocumentCommand command) throws IOException {
//        customerRepository.findByIdentifier(command.getCustomerIdentifier())
//                .map(customerEntity -> DocumentMapper.map(command.getCustomerDocument(), customerEntity))
//                .ifPresent(documentRepository::save);
//
//        return new DocumentEvent(command.getCustomerIdentifier(), command.getCustomerDocument().getIdentifier());
//    }


    @Transactional
    @CommandHandler
    @EventEmitter(selectorName = CustomerEventConstants.SELECTOR_NAME, selectorValue = CustomerEventConstants.POST_DOCUMENT)
    public DocumentEvent process(final CreateKYCDocumentCommand command) throws IOException {

        Optional<CustomerEntity> customerEntity = customerRepository.findByIdentifier(command.getCustomerIdentifier());

        Boolean findDocument = documentRepository.findByIdentifierAndCustomer(command.getCustomerIdentifier());
        if (!findDocument) {
            documentRepository.save(DocumentMapper.map(command.getCustomerDocumentsBody(), customerEntity.get()));
        }
        final DocumentEntity documentEntity = documentRepository.findByCustomerIdAndDocumentIdentifier(
                command.getCustomerIdentifier(), command.getCustomerIdentifier())
                .orElseThrow(() -> ServiceException.badRequest("Document not found"));

        final DocumentEntryEntity documentEntryEntity = DocumentMapper.map(command.getCustomerDocumentsBody(), documentEntity);
        // documentEntryEntity.setStatus("PENDING");
        documentEntryEntity.setStatus("UPLOADED");
        DocumentEntryEntity doc = documentEntryRepository.save(documentEntryEntity);
        final DocumentPageEntity documentPageEntity = DocumentMapper.map(command.getFile(), 1, documentEntity, doc);
        documentPageRepository.save(documentPageEntity);
        return new DocumentEvent(command.getCustomerIdentifier(), command.getCustomerIdentifier());
    }

    @Transactional
    @CommandHandler
    @EventEmitter(selectorName = CustomerEventConstants.SELECTOR_NAME, selectorValue = CustomerEventConstants.PUT_DOCUMENT)
    public DocumentEvent process(final ChangeDocumentStatusCommand command) throws IOException {

        final DocumentEntryEntity existingDocument = documentEntryRepository.findByCustomerIdAndDocumentId(
                command.getCustomerIdentifier(), command.getCustomerDocumentId())
                .orElseThrow(() ->
                        ServiceException.notFound("Document ''{0}'' for customer ''{1}'' not found",
                                command.getCustomerDocumentId(), command.getCustomerIdentifier()));

        existingDocument.setStatus("APPROVED");
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
    @EventEmitter(selectorName = CustomerEventConstants.SELECTOR_NAME, selectorValue = CustomerEventConstants.DELETE_DOCUMENT)
    public DocumentEvent process(final DeleteDocumentCommand command) throws IOException {
        final DocumentEntryEntity existingDocument = documentEntryRepository.findByCustomerIdAndDocumentId(command.getCustomerIdentifier(), command.getDocumentIdentifier())
                .orElseThrow(() ->
                        ServiceException.notFound("Document ''{0}'' for customer ''{1}'' not found",
                                command.getDocumentIdentifier(), command.getCustomerIdentifier()));
        existingDocument.setStatus("DELETED");

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

    @Transactional
    @CommandHandler
    @EventEmitter(selectorName = CustomerEventConstants.SELECTOR_NAME, selectorValue = CustomerEventConstants.DELETE_DOCUMENT_PAGE)
    public DocumentPageEvent process(final DeleteDocumentPageCommand command) throws IOException {
        documentPageRepository.findByCustomerIdAndDocumentIdentifierAndPageNumber(
                command.getCustomerIdentifier(),
                command.getDocumentIdentifier(),
                command.getPageNumber())
                .ifPresent(documentPageRepository::delete);

        //No exception if it's not present, because why bother.  It's not present.  That was the goal.

        return new DocumentPageEvent(command.getCustomerIdentifier(), command.getDocumentIdentifier(), command.getPageNumber());
    }
}
