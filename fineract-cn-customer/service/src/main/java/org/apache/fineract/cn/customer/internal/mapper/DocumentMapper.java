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
import org.apache.fineract.cn.customer.internal.service.DocumentService;
import org.apache.fineract.cn.lang.DateConverter;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Myrle Krantz
 */
public class DocumentMapper {
    private DocumentMapper() {
        super();
    }


    public static List<DocumentsType> map(Map<String, List<DocumentEntryEntity>> documentEntryEntity) {
        final List<DocumentsType> ret = new ArrayList<>();

        documentEntryEntity.forEach((key, val) -> {
            final List<DocumentsSubType> documentsSubTypeList = new ArrayList<>();
            final DocumentsType d = new DocumentsType();
            d.setKYCVerified(false);
            val.forEach(doc -> {
                final DocumentsSubType documentsSubType = new DocumentsSubType();
                documentsSubType.setId(doc.getId());
                documentsSubType.setCreated_by(doc.getCreatedBy());
                documentsSubType.setStatus(doc.getStatus());
                documentsSubType.setType(doc.getType());
                documentsSubType.setSubType(doc.getSubType());
                DocumentService.setKycDocumentMapper(documentsSubTypeList, d, doc, documentsSubType);
            });

            d.setType(key);
            d.setDocumentsSubType(documentsSubTypeList);
            ret.add(d);
        });
        return ret;
    }


    public static DocumentsMaster map(DocumentTypeEntity documentTypeEntity, List<DocumentsMasterSubtype> documentsMasterSubtypes) {
        DocumentsMaster documentsMaster = new DocumentsMaster();
        documentsMaster.setUuid(documentTypeEntity.getUuid());
        documentsMaster.setTitle(documentTypeEntity.getTitle());
        documentsMaster.setUserType(documentTypeEntity.getUserType());
        documentsMaster.setDocumentsMasterSubtypes(documentsMasterSubtypes);
        documentsMaster.setId(documentTypeEntity.getId());
        return documentsMaster;
    }

    public static List<DocumentsMasterSubtype> map(List<DocumentSubTypeEntity> documentSubTypeEntities) {
        List<DocumentsMasterSubtype> documentsMasterSubtypes = new ArrayList<>();
        documentSubTypeEntities.forEach(documentSubTypeEntity -> {
            DocumentsMasterSubtype documentsMasterSubtype = new DocumentsMasterSubtype();
            documentsMasterSubtype.setTitle(documentSubTypeEntity.getTitle());
            documentsMasterSubtype.setUuid(documentSubTypeEntity.getUuid());
            documentsMasterSubtype.setId(documentSubTypeEntity.getId());
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
        ret.setDocument(documentEntity);
        return ret;
    }

    public static CustomerDocument map(final DocumentEntity documentEntity) {
        final CustomerDocument ret = new CustomerDocument();
        ret.setCompleted(documentEntity.getCompleted());
        ret.setCreatedBy(documentEntity.getCreatedBy());
        ret.setCreatedOn(DateConverter.toIsoString(documentEntity.getCreatedOn()));
        ret.setIdentifier(documentEntity.getIdentifier());
        ret.setDescription(documentEntity.getDescription());
        return ret;
    }

    public static DocumentEntity map(final CustomerDocument customerDocument, final CustomerEntity customerEntity) {
        final DocumentEntity ret = new DocumentEntity();
        ret.setCustomer(customerEntity);
        ret.setIdentifier(customerEntity.getIdentifier());
        ret.setCompleted(false);
        ret.setStatus("PENDING");
        ret.setCreatedBy(UserContextHolder.checkedGetUser());
        ret.setCreatedOn(LocalDateTime.now(Clock.systemUTC()));
        ret.setDescription(customerDocument.getDescription());
        return ret;
    }


    public static DocumentEntity map(final CustomerDocumentsBody customerDocumentsBody, final CustomerEntity customerEntity) {
        final DocumentEntity ret = new DocumentEntity();
        ret.setCustomer(customerEntity);
        ret.setCompleted(false);
        ret.setStatus("PENDING");
        ret.setCreatedBy(UserContextHolder.checkedGetUser());
        ret.setCreatedOn(LocalDateTime.now(Clock.systemUTC()));
        ret.setIdentifier(customerEntity.getIdentifier());
        ret.setDescription(customerDocumentsBody.getDescription());
        return ret;
    }

    public static DocumentEntryEntity map(final CustomerDocumentsBody customerDocumentsBody, final DocumentEntity documentEntity) {
        final DocumentEntryEntity ret = new DocumentEntryEntity();
        ret.setDescription(customerDocumentsBody.getDescription());
        ret.setType(customerDocumentsBody.getType());
        ret.setSubType(customerDocumentsBody.getSubType());
        ret.setCreatedBy(documentEntity.getCreatedBy());
        ret.setDocument(documentEntity);
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

    public static DocumentStorageEntity map(final MultipartFile multipartFile, final String createdBy, final String docType) throws IOException {
        DocumentStorageEntity storageEntity = new DocumentStorageEntity();
        storageEntity.setContentType(multipartFile.getContentType());
        storageEntity.setDocumentName(multipartFile.getOriginalFilename());
        storageEntity.setImage(multipartFile.getBytes());
        storageEntity.setSize(multipartFile.getSize());
        storageEntity.setCreatedBy(createdBy);
        storageEntity.setDocType(docType);
        return storageEntity;
    }

    public static List<DocumentEntryEntity> map(List<KycDocuments> kycDocuments, DocumentEntity documentEntity) {
        List<DocumentEntryEntity> documentEntryEntityList = new ArrayList<>();
        kycDocuments.forEach(doc -> {
            DocumentEntryEntity documents = new DocumentEntryEntity();
            documents.setDescription(doc.getDescription());
            documents.setDocumentName(doc.getDocName());
            documents.setSubType(doc.getSubType());
            documents.setType(doc.getType());
            documents.setDocRef(doc.getUuid());
            documents.setCreatedBy(UserContextHolder.checkedGetUser());
            documents.setStatus(CustomerDocument.Status.PENDING.name());
            documents.setCreatedOn(LocalDateTime.now(Clock.systemUTC()));
            documents.setDocument(documentEntity);
            documentEntryEntityList.add(documents);
        });
        return documentEntryEntityList;
    }

}
