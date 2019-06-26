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
import org.apache.fineract.cn.customer.api.v1.domain.DocumentStorage;
import org.apache.fineract.cn.customer.api.v1.domain.DocumentsMasterSubtype;
import org.apache.fineract.cn.customer.api.v1.domain.DocumentsType;
import org.apache.fineract.cn.customer.api.v1.events.DocumentEvent;
import org.apache.fineract.cn.customer.api.v1.events.DocumentPageEvent;
import org.apache.fineract.cn.customer.internal.command.*;
import org.apache.fineract.cn.customer.internal.mapper.DocumentMapper;
import org.apache.fineract.cn.customer.internal.repository.*;

import java.io.IOException;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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
    private final DocumentTypeRepository documentTypeRepository;
    private final DocumentSubTypeRepository documentSubTypeRepository;
    private final DocumentStorageRepository documentStorageRepository;

    @Autowired
    public DocumentCommandHandler(
            final DocumentRepository documentRepository,
            final DocumentEntryRepository documentEntryRepository,
            final CustomerRepository customerRepository,
            final DocumentTypeRepository documentTypeRepository,
            final DocumentSubTypeRepository documentSubTypeRepository,
            final DocumentStorageRepository documentStorageRepository) {
        this.documentRepository = documentRepository;
        this.customerRepository = customerRepository;
        this.documentEntryRepository = documentEntryRepository;
        this.documentTypeRepository = documentTypeRepository;
        this.documentSubTypeRepository = documentSubTypeRepository;
        this.documentStorageRepository = documentStorageRepository;
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
        List<DocumentEntryEntity> documentEntryEntityList = command.getCustomerDocument().getKycDocuments()
                .stream()
                .map(kycDocuments -> DocumentMapper.map(kycDocuments, documentEntity))
                .collect(Collectors.toList());

//                DocumentMapper.map(command.getCustomerDocument().getKycDocuments(), documentEntity);
        this.documentEntryRepository.save(documentEntryEntityList);
        return new DocumentPageEvent(command.getCustomeridentifier(), command.getCustomeridentifier(), 1);
    }


    @Transactional
    @CommandHandler
    @EventEmitter(selectorName = CustomerEventConstants.SELECTOR_NAME, selectorValue = CustomerEventConstants.PUT_DOCUMENT)
    public DocumentEvent process(final ChangeDocumentStatusCommand command) {

        final DocumentEntryEntity existingDocument = documentEntryRepository.findByCustomerIdAndDocumentId(
                command.getCustomerIdentifier(), command.getCustomerDocumentId())
                .orElseThrow(() -> ServiceException.notFound("Document ''{0}'' for customer ''{1}'' not found", command.getCustomerDocumentId(), command.getCustomerIdentifier()));

        existingDocument.setStatus(CustomerDocument.Status.APPROVED.name());
        existingDocument.setUpdatedOn(LocalDateTime.now(Clock.systemUTC()));
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
    @EventEmitter(selectorName = CustomerEventConstants.SELECTOR_NAME, selectorValue = CustomerEventConstants.UPLOAD_DOCUMENT)
    public DocumentStorage process(final UploadDocumentCommand command) throws IOException {
        DocumentStorageEntity storageEntity = DocumentMapper.map(command.getFile(), command.getDocType());
        DocumentStorageEntity entity = this.documentStorageRepository.save(storageEntity);
        return new DocumentStorage(entity.getId(), entity.getUuid(), entity.getCreatedBy(), entity.getDocumentName(), entity.getContentType(), entity.getSize(), entity.getDocType());
    }


    @Transactional
    @CommandHandler
    @EventEmitter(selectorName = CustomerEventConstants.SELECTOR_NAME, selectorValue = CustomerEventConstants.UNDO_DOCUMENT)
    public DocumentEvent process(final UndoDocumentStatusCommand command) {

        final DocumentEntryEntity existingDocument = documentEntryRepository.findByCustomerIdAndDocumentId(
                command.getCustomerIdentifier(), command.getCustomerDocumentId())
                .orElseThrow(() -> ServiceException.notFound("Document ''{0}'' for customer ''{1}'' not found", command.getCustomerDocumentId(), command.getCustomerIdentifier()));

        existingDocument.setStatus(CustomerDocument.Status.PENDING.name());
        existingDocument.setUpdatedOn(LocalDateTime.now(Clock.systemUTC()));
        documentEntryRepository.save(existingDocument);
        return new DocumentEvent(command.getCustomerIdentifier(), command.getCustomerDocumentId().toString());
    }


    @Transactional
    @CommandHandler
    @EventEmitter(selectorName = CustomerEventConstants.SELECTOR_NAME, selectorValue = CustomerEventConstants.POST_DOCUMENT_TYPE)
    public CreateDocumentTypeCommandResponse process(final CreateDocumentTypeCommand command) {

        final DocumentTypeEntity documentTypeEntity = DocumentMapper.map(command.getDocumentsType());
        DocumentTypeEntity typeEntity = this.documentTypeRepository.save(documentTypeEntity);
        return new CreateDocumentTypeCommandResponse(typeEntity.getTitle(), typeEntity.getUserType(), typeEntity.isActive(), typeEntity.getUuid());
    }


    @Transactional
    @CommandHandler
    @EventEmitter(selectorName = CustomerEventConstants.SELECTOR_NAME, selectorValue = CustomerEventConstants.PUT_DOCUMENT_TYPE)
    public DocumentEvent process(final UpdateDocumentTypeCommand command) {
        DocumentsType documentsType = command.getDocumentsType();

        final DocumentTypeEntity documentTypeEntity = documentTypeRepository.findByUuid(command.getuuid()).orElseThrow(() -> ServiceException.notFound("NOT FOUND"));
        documentTypeEntity.setTitle(documentsType.getTitle());
        documentTypeEntity.setActive(documentsType.isActive());
        documentTypeEntity.setUserType(documentsType.getUserType());
        documentTypeEntity.setMaxUpload(documentsType.getMaxUpload());
        this.documentTypeRepository.save(documentTypeEntity);
        return new DocumentEvent(command.getuuid(), command.getDocumentsType().toString());
    }


    @Transactional
    @CommandHandler
    @EventEmitter(selectorName = CustomerEventConstants.SELECTOR_NAME, selectorValue = CustomerEventConstants.POST_DOCUMENT_SUB_TYPE)
    public DocumentEvent process(final CreateDocumentSubTypeCommand command) {
        DocumentTypeEntity documentTypeEntity = documentTypeRepository.findByUuid(command.getUuid()).orElseThrow(() -> ServiceException.notFound("Document not found"));
        final DocumentSubTypeEntity documentSubTypeEntity = DocumentMapper.map(command.getDocumentsSubType(), documentTypeEntity);
        this.documentSubTypeRepository.save(documentSubTypeEntity);
        return new DocumentEvent(command.getUuid(), command.getDocumentsSubType().toString());
    }


    @Transactional
    @CommandHandler
    @EventEmitter(selectorName = CustomerEventConstants.SELECTOR_NAME, selectorValue = CustomerEventConstants.PUT_DOCUMENT_SUB_TYPE)
    public DocumentEvent process(final UpdateDocumentSubTypeCommand subTypeCommand) {
        DocumentsMasterSubtype documentsType = subTypeCommand.getDocumentsType();

        final DocumentSubTypeEntity documentSubTypeEntity = documentSubTypeRepository.findByUuid(subTypeCommand.getuuid()).orElseThrow(() -> ServiceException.notFound("NOT FOUND"));
        documentSubTypeEntity.setTitle(documentsType.getTitle());
        documentSubTypeEntity.setActive(documentsType.isActive());
        this.documentSubTypeRepository.save(documentSubTypeEntity);
        return new DocumentEvent(subTypeCommand.getuuid(), subTypeCommand.getDocumentsType().toString());
    }


    @Transactional
    @CommandHandler
    @EventEmitter(selectorName = CustomerEventConstants.SELECTOR_NAME, selectorValue = CustomerEventConstants.PUT_DOCUMENT)
    public DocumentEvent process(final RejectDocumentCommand command) {

        final DocumentEntryEntity existingDocument = documentEntryRepository.findByCustomerIdAndDocumentId(
                command.getCustomerIdentifier(), command.getCustomerDocumentId())
                .orElseThrow(() -> ServiceException.notFound("Document ''{0}'' for customer ''{1}'' not found", command.getCustomerDocumentId(), command.getCustomerIdentifier()));

        existingDocument.setStatus(CustomerDocument.Status.REJECTED.name());
        existingDocument.setReasonForReject(command.getReason());
        existingDocument.setRejectedBy(UserContextHolder.checkedGetUser());
        existingDocument.setRejectedOn(LocalDateTime.now(Clock.systemUTC()));
        existingDocument.setUpdatedOn(LocalDateTime.now(Clock.systemUTC()));
        documentEntryRepository.save(existingDocument);
        return new DocumentEvent(command.getCustomerIdentifier(), command.getCustomerDocumentId().toString());
    }


    @Transactional
    @CommandHandler
    @EventEmitter(selectorName = CustomerEventConstants.SELECTOR_NAME, selectorValue = CustomerEventConstants.DELETE_DOCUMENT)
    public DocumentEvent process(final DeleteDocumentCommand command) {
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
    public DocumentEvent process(final CompleteDocumentCommand command) {
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
