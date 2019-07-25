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
package org.apache.fineract.cn.customer.internal.mapper;

import org.apache.fineract.cn.api.util.UserContextHolder;
import org.apache.fineract.cn.customer.api.v1.domain.*;
import org.apache.fineract.cn.customer.internal.repository.*;
import org.apache.fineract.cn.lang.DateConverter;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Md  Robiul Hassan
 */
public class DocumentMapper {
    private DocumentMapper() {
        super();
    }


    public static void setDocumentTypeStatus(List<DocumentEntryEntity> documentEntryEntities, DocumentsType documentsType) {
        if (documentEntryEntities.stream().anyMatch(e -> e.getStatus().equals(CustomerDocument.Status.APPROVED.name()))) {
            documentsType.setStatus(CustomerDocument.Status.APPROVED.name());
        } else if (documentEntryEntities.stream().noneMatch(e -> e.getStatus().equals(CustomerDocument.Status.APPROVED.name()))
                && documentEntryEntities.stream().anyMatch(e -> e.getStatus().equals(CustomerDocument.Status.REJECTED.name()))) {
//            new requirements on the pending and rejected documents status on timestamp
            documentEntryEntities.stream().filter(documentEntryEntity -> documentEntryEntity.getStatus()
                    .equals(CustomerDocument.Status.REJECTED.name())).findFirst().ifPresent(documentEntryEntity -> {
                boolean isAnyPendingDocumentPresentAfterRejected = documentEntryEntities
                        .stream()
                        .anyMatch(entity -> entity.getCreatedOn()
                                .isAfter(documentEntryEntity.getCreatedOn())
                                && entity.getStatus().equals(CustomerDocument.Status.PENDING.name()));
                if (isAnyPendingDocumentPresentAfterRejected) {
                    documentsType.setStatus(CustomerDocument.Status.PENDING.name());
                } else {
                    documentsType.setStatus(CustomerDocument.Status.REJECTED.name());
                }
            });

        } else {
            documentsType.setStatus(CustomerDocument.Status.PENDING.name());
        }
    }


    public static DocumentsMaster map(DocumentTypeEntity documentTypeEntity, List<DocumentsMasterSubtype> documentsMasterSubtypes) {
        DocumentsMaster documentsMaster = new DocumentsMaster();
        documentsMaster.setUuid(documentTypeEntity.getUuid());
        documentsMaster.setTitle(documentTypeEntity.getTitle());
        documentsMaster.setUserType(documentTypeEntity.getUserType());
        documentsMaster.setDocumentsMasterSubtypes(documentsMasterSubtypes);
        documentsMaster.setId(documentTypeEntity.getId());
        documentsMaster.setActive(documentTypeEntity.isActive());
        documentsMaster.setMaxUpload(documentTypeEntity.getMaxUpload());
        return documentsMaster;
    }

    public static List<DocumentsMasterSubtype> map(List<DocumentSubTypeEntity> documentSubTypeEntities, final DocumentTypeEntity documentTypeEntity) {
        List<DocumentsMasterSubtype> documentsMasterSubtypes = new ArrayList<>();
        documentSubTypeEntities.forEach(documentSubTypeEntity -> {
            DocumentsMasterSubtype documentsMasterSubtype = new DocumentsMasterSubtype();
            documentsMasterSubtype.setTitle(documentSubTypeEntity.getTitle());
            documentsMasterSubtype.setUuid(documentSubTypeEntity.getUuid());
            documentsMasterSubtype.setId(documentSubTypeEntity.getId());
            documentsMasterSubtype.setActive(documentSubTypeEntity.isActive());
            documentsMasterSubtype.setDocTypeUUID(documentTypeEntity.getUuid());
            documentsMasterSubtype.setDocTypeId(documentSubTypeEntity.getDocumentType().getId());
            documentsMasterSubtypes.add(documentsMasterSubtype);
        });
        return documentsMasterSubtypes;
    }

    public static CustomerDocumentEntry map(DocumentEntryEntity documentEntryEntity) {
        final CustomerDocumentEntry documentEntry = new CustomerDocumentEntry();
        documentEntry.setId(documentEntryEntity.getId());
        documentEntry.setDescription(documentEntryEntity.getDescription());
        documentEntry.setStatus(documentEntryEntity.getStatus());
        documentEntry.setSubType(documentEntryEntity.getSubType());
        documentEntry.setType(documentEntryEntity.getType());
        return documentEntry;
    }


    public static DocumentEntryEntity map(final CustomerDocumentEntry customerDocumentEntry, final DocumentEntity documentEntity) {
        final DocumentEntryEntity ret = new DocumentEntryEntity();
        ret.setDescription(customerDocumentEntry.getDescription());
        ret.setType(customerDocumentEntry.getType());
        ret.setSubType(customerDocumentEntry.getSubType());
        ret.setCreatedBy(documentEntity.getCreatedBy());
        ret.setUpdatedOn(LocalDateTime.now(Clock.systemUTC()));
        ret.setDocument(documentEntity);
        return ret;
    }

    public static CustomerDocument map(final DocumentEntity documentEntity) {
        final CustomerDocument ret = new CustomerDocument();
        ret.setCreatedBy(documentEntity.getCreatedBy());
        ret.setCreatedOn(DateConverter.toIsoString(documentEntity.getCreatedOn()));
        ret.setIdentifier(documentEntity.getIdentifier());
        ret.setKycStatusText(documentEntity.getStatus());
        ret.setKycStatus(documentEntity.getStatus().equals(CustomerDocument.Status.APPROVED.name()));
        return ret;
    }


    public static DocumentsMaster map(final DocumentTypeEntity documentTypeEntity) {
        final DocumentsMaster documentsMaster = new DocumentsMaster();
        documentsMaster.setUuid(documentTypeEntity.getUuid());
        documentsMaster.setUserType(documentTypeEntity.getUserType());
        documentsMaster.setTitle(documentTypeEntity.getTitle());
        documentsMaster.setActive(documentTypeEntity.isActive());
        documentsMaster.setMaxUpload(documentTypeEntity.getMaxUpload());
        return documentsMaster;
    }


    public static DocumentEntity map(final CustomerDocument customerDocument, final CustomerEntity customerEntity) {
        final DocumentEntity ret = new DocumentEntity();
        ret.setCustomer(customerEntity);
        ret.setIdentifier(customerEntity.getIdentifier());
        ret.setStatus(CustomerDocument.Status.NOTUPLOADED.name());
        ret.setCreatedBy(UserContextHolder.checkedGetUser());
        ret.setCreatedOn(LocalDateTime.now(Clock.systemUTC()));
        return ret;
    }


    public static DocumentEntity map(final CustomerDocumentsBody customerDocumentsBody, final CustomerEntity customerEntity) {
        final DocumentEntity ret = new DocumentEntity();
        ret.setCustomer(customerEntity);
        ret.setStatus(CustomerDocument.Status.PENDING.name());
        ret.setCreatedBy(UserContextHolder.checkedGetUser());
        ret.setCreatedOn(LocalDateTime.now(Clock.systemUTC()));
        ret.setIdentifier(customerEntity.getIdentifier());
        return ret;
    }

    public static DocumentEntryEntity map(final CustomerDocumentsBody customerDocumentsBody, final DocumentEntity documentEntity) {
        final DocumentEntryEntity ret = new DocumentEntryEntity();
        ret.setType(customerDocumentsBody.getType());
        ret.setSubType(customerDocumentsBody.getSubType());
        ret.setCreatedBy(documentEntity.getCreatedBy());
        ret.setDocument(documentEntity);
        ret.setUpdatedOn(LocalDateTime.now(Clock.systemUTC()));
        return ret;
    }


    public static DocumentStorage map(final DocumentStorageEntity documentStorageEntity) {
        DocumentStorage documentStorage = new DocumentStorage();
        documentStorage.setId(documentStorageEntity.getId());
        documentStorage.setContentType(documentStorageEntity.getContentType());
        documentStorage.setDocumentName(documentStorageEntity.getDocumentName());
        documentStorage.setUuid(documentStorageEntity.getUuid());
        documentStorage.setSize(documentStorageEntity.getSize());
        documentStorage.setCreatedBy(documentStorageEntity.getCreatedBy());
        documentStorage.setDocType(documentStorageEntity.getDocType());

        return documentStorage;
    }

    public static DocumentStorageEntity map(final MultipartFile multipartFile, final String docType) throws IOException {
        DocumentStorageEntity storageEntity = new DocumentStorageEntity();
        storageEntity.setContentType(multipartFile.getContentType());
        storageEntity.setDocumentName(multipartFile.getOriginalFilename());
        storageEntity.setImage(multipartFile.getBytes());
        storageEntity.setSize(multipartFile.getSize());
        storageEntity.setCreatedBy(UserContextHolder.checkedGetUser());
        storageEntity.setDocType(docType);
        return storageEntity;
    }

    public static DocumentEntryEntity map(KycDocuments kycDocuments, DocumentEntity documentEntity) {
        DocumentEntryEntity documents = new DocumentEntryEntity();
        documents.setDescription(kycDocuments.getDescription());
        documents.setDocumentName(kycDocuments.getDocName());
        documents.setSubType(kycDocuments.getSubType());
        documents.setType(kycDocuments.getType());
        documents.setDocRef(kycDocuments.getUuid());
        documents.setCreatedBy(UserContextHolder.checkedGetUser());
        documents.setStatus(CustomerDocument.Status.PENDING.name());
        documents.setCreatedOn(LocalDateTime.now(Clock.systemUTC()));
        documents.setUpdatedOn(LocalDateTime.now(Clock.systemUTC()));
        documents.setDocument(documentEntity);
        return documents;
    }

    public static DocumentTypeEntity map(DocumentsType documentsType) {
        DocumentTypeEntity documentTypeEntity = new DocumentTypeEntity();
        documentTypeEntity.setTitle(documentsType.getTitle());
        documentTypeEntity.setUserType(documentsType.getUserType());
        documentTypeEntity.setActive(documentsType.isActive());
        documentTypeEntity.setMaxUpload(documentsType.getMaxUpload());
        return documentTypeEntity;
    }

    public static DocumentSubTypeEntity map(DocumentsMasterSubtype documentsMasterSubtype, DocumentTypeEntity documentTypeEntity) {
        DocumentSubTypeEntity documentSubTypeEntity = new DocumentSubTypeEntity();
        documentSubTypeEntity.setTitle(documentsMasterSubtype.getTitle());
        documentSubTypeEntity.setDocumentType(documentTypeEntity);
        documentSubTypeEntity.setActive(documentsMasterSubtype.isActive());
        return documentSubTypeEntity;
    }

    public static DocumentsMasterSubtype map(DocumentSubTypeEntity documentSubTypeEntity) {
        DocumentsMasterSubtype documentsSubType = new DocumentsMasterSubtype();
        documentsSubType.setTitle(documentSubTypeEntity.getTitle());
        documentsSubType.setUuid(documentSubTypeEntity.getUuid());
        documentsSubType.setDocTypeId(documentSubTypeEntity.getDocumentType().getId());
        documentsSubType.setActive(documentSubTypeEntity.isActive());
        documentsSubType.setId(documentSubTypeEntity.getId());
        return documentsSubType;
    }

}
