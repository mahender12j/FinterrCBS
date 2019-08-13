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
import org.apache.fineract.cn.customer.api.v1.domain.*;
import org.apache.fineract.cn.customer.api.v1.events.DocumentEvent;
import org.apache.fineract.cn.customer.api.v1.events.DocumentPageEvent;
import org.apache.fineract.cn.customer.internal.command.*;
import org.apache.fineract.cn.customer.internal.mapper.DocumentMapper;
import org.apache.fineract.cn.customer.internal.repository.*;

import java.io.IOException;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import org.apache.fineract.cn.api.util.UserContextHolder;
import org.apache.fineract.cn.command.annotation.Aggregate;
import org.apache.fineract.cn.command.annotation.CommandHandler;
import org.apache.fineract.cn.command.annotation.EventEmitter;
import org.apache.fineract.cn.lang.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

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

        final DocumentEntity documentEntity = customerRepository.findByIdentifier(command.getCustomeridentifier())
                .map(entity -> this.documentRepository.findByCustomer(entity)
                        .orElseGet(() -> documentRepository.save(DocumentMapper
                                .map(command.getCustomerDocument(), entity))))
                .orElseThrow(() -> ServiceException.notFound("Customer {0} not found", command.getCustomeridentifier()));

        List<KycDocuments> kycDocuments = command.getCustomerDocument().getKycDocuments();

        List<DocumentEntryEntity> documentEntryEntityList = kycDocuments
                .stream()
                .map(documents -> DocumentMapper.map(documents, documentEntity))
                .collect(Collectors.toList());

        this.documentEntryRepository.save(documentEntryEntityList);
        return new DocumentPageEvent(command.getCustomeridentifier(), command.getCustomeridentifier(), 1);
    }


    //    todo update customer documents for approval and reject
    @Transactional
    @CommandHandler
    @EventEmitter(selectorName = CustomerEventConstants.SELECTOR_NAME, selectorValue = CustomerEventConstants.PUT_DOCUMENT)
    public CustomerDocument process(final UpdateDocumentStatusCommand command) {

        List<CustomerDocumentApproval> customerDocumentApprovals = command.getDocumentApproval();
        CustomerEntity customerEntity = this.customerRepository.findByIdentifier(command.getCustomerIdentifier())
                .orElseThrow(() -> ServiceException.notFound("Customer not found {0}", command.getCustomerIdentifier()));

        DocumentEntity documentEntity = customerEntity.getDocumentEntity();

        List<DocumentEntryEntity> documentEntryEntityList = documentEntity
                .getDocumentEntryEntities()
                .stream()
                .filter(documentEntryEntity -> documentEntryEntity.getStatus().equals(CustomerDocument.Status.PENDING.name()))
                .peek(documentEntryEntity -> customerDocumentApprovals
                        .stream()
                        .filter(documentApproval -> documentApproval.getId()
                                .equals(documentEntryEntity.getId())).findFirst().ifPresent(documentApproval -> {
                            System.out.println("Current document: " + documentApproval);
                            if (documentApproval.getStatus().equals(CustomerDocumentApproval.Status.REJECTED.name())) {
                                documentEntryEntity.setReasonForReject(documentApproval.getRejectedReason());
                                documentEntryEntity.setStatus(CustomerDocumentApproval.Status.REJECTED.name());
                                documentEntryEntity.setRejectedOn(LocalDateTime.now(Clock.systemUTC()));
                                documentEntryEntity.setRejectedBy(UserContextHolder.checkedGetUser());
                            } else if (documentApproval.getStatus().equals(CustomerDocumentApproval.Status.APPROVED.name())) {
                                documentEntryEntity.setStatus(CustomerDocumentApproval.Status.APPROVED.name());
                                documentEntryEntity.setApprovedBy(UserContextHolder.checkedGetUser());
                                documentEntryEntity.setApprovedOn(LocalDateTime.now(Clock.systemUTC()));
                            }

                        })).collect(Collectors.toList());

        this.documentEntryRepository.save(documentEntryEntityList);


        if (!documentEntity.getStatus().equals(CustomerDocument.Status.APPROVED.name())) {
            documentEntity.setStatus(findCustomerDocumentsStatus(customerEntity));
            this.documentRepository.save(documentEntity);
        }

        return new CustomerDocument(command.getCustomerIdentifier());
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
        existingDocument.setApprovedBy(UserContextHolder.checkedGetUser());
        existingDocument.setApprovedOn(LocalDateTime.now());
        documentEntryRepository.save(existingDocument);
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

//        updating the status for document type
        this.putDocumentTypeActivate(new UpdateDocumentTypeActivationCommand(documentSubTypeEntity.getDocumentType().getId()));
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

        //        updating the status for document type
        this.putDocumentTypeActivate(new UpdateDocumentTypeActivationCommand(documentSubTypeEntity.getDocumentType().getId()));
        return new DocumentEvent(subTypeCommand.getuuid(), subTypeCommand.getDocumentsType().toString());
    }


    @Transactional
    @CommandHandler
    @EventEmitter(selectorName = CustomerEventConstants.SELECTOR_NAME, selectorValue = CustomerEventConstants.PUT_DOCUMENT_TYPE_ACTIVATE)
    public void putDocumentTypeActivate(final UpdateDocumentTypeActivationCommand activationCommand) {
        DocumentTypeEntity documentTypeEntity = this.documentTypeRepository.findOne(activationCommand.getid());
        final List<DocumentSubTypeEntity> documentSubTypeEntity = documentSubTypeRepository.findByDocumentType(documentTypeEntity);
        boolean anyActivatedSubTypeExist = documentSubTypeEntity.stream().anyMatch(DocumentSubTypeEntity::isActive);
        if (anyActivatedSubTypeExist) {
            documentTypeEntity.setActive(true);
        } else {
            documentTypeEntity.setActive(false);
        }
        this.documentTypeRepository.save(documentTypeEntity);
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
        documentRepository.save(documentEntity);


        return new DocumentEvent(command.getCustomerIdentifier(), command.getDocumentIdentifier());
    }


    String findCustomerDocumentsStatus(CustomerEntity customerEntity) {
        List<DocumentTypeEntity> allTypeEntities = this.documentTypeRepository.findAll();
        return this.documentRepository.findByCustomerId(customerEntity.getIdentifier()).map(documentEntity -> {

            final Map<String, List<DocumentEntryEntity>> documentEntryEntity =
                    this.documentEntryRepository.findByDocumentAndStatusNot(documentEntity, CustomerDocument.Status.DELETED.name())
                            .stream()
                            .collect(groupingBy(DocumentEntryEntity::getType, toList()));

            List<DocumentsType> documentsType = new ArrayList<>();

            documentEntryEntity.forEach((key, documentEntryEntities) -> {
                final DocumentsType type = new DocumentsType();
                DocumentMapper.setDocumentTypeStatus(documentEntryEntities, type);
                documentsType.add(type);

            });

            //receive the documents master, all the types of documents per type in list
            Set<String> docMaster = allTypeEntities
                    .stream()
                    .filter((DocumentTypeEntity::isActive))
                    .filter(documentTypeEntity -> documentTypeEntity.getUserType().equals(customerEntity.getType()))
                    .map(DocumentTypeEntity::getUuid)
                    .collect(Collectors.toSet());

            Set<String> docAvailable = documentEntryEntity.keySet();

            System.out.println("Documents master Type: " + docMaster);
            System.out.println("Available documents Type: " + docAvailable);

            boolean isAllDocApproved = documentsType.stream().allMatch(type -> type.getStatus().equals(CustomerDocument.Status.APPROVED.name()));
            boolean isAnyDocumentRejected = documentsType.stream().anyMatch(type -> type.getStatus().equals(CustomerDocument.Status.REJECTED.name()));

            Set<String> docDifferent = new HashSet<>(docMaster);
            docDifferent.removeAll(docAvailable);


            System.out.println("same doc");
            if (isAllDocApproved) {
                System.out.println("APPROVED doc");
                return CustomerDocument.Status.APPROVED.name();
            } else if (isAnyDocumentRejected) {
                System.out.println("REJECTED doc");
                return CustomerDocument.Status.REJECTED.name();
            } else if (docDifferent.size() == 0) {
                System.out.println("PENDING doc");
                return CustomerDocument.Status.PENDING.name();
            } else {
                System.out.println("NOTUPLOADED doc");
                return CustomerDocument.Status.NOTUPLOADED.name();
            }

        }).orElse(CustomerDocument.Status.NOTUPLOADED.name());
    }

}
