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
package org.apache.fineract.cn.customer.internal.service;

import org.apache.fineract.cn.customer.api.v1.domain.*;
import org.apache.fineract.cn.customer.internal.mapper.DocumentMapper;
import org.apache.fineract.cn.customer.internal.repository.*;
import org.apache.fineract.cn.lang.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

/**
 * @author Myrle Krantz
 */
@Service
public class DocumentService {
    private final DocumentRepository documentRepository;
    private final DocumentEntryRepository documentEntryRepository;
    private final DocumentStorageRepository documentStorageRepository;
    private final DocumentTypeRepository documentTypeRepository;
    private final CustomerRepository customerRepository;
    private final DocumentSubTypeRepository documentSubTypeRepository;

    @Autowired
    public DocumentService(
            final DocumentRepository documentRepository,
            final DocumentEntryRepository documentEntryRepository,
            final DocumentStorageRepository documentStorageRepository,
            final DocumentTypeRepository documentTypeRepository,
            final CustomerRepository customerRepository,
            final DocumentSubTypeRepository documentSubTypeRepository) {
        this.documentRepository = documentRepository;
        this.documentEntryRepository = documentEntryRepository;
        this.documentStorageRepository = documentStorageRepository;
        this.documentTypeRepository = documentTypeRepository;
        this.customerRepository = customerRepository;
        this.documentSubTypeRepository = documentSubTypeRepository;
    }


    public DocumentStorage addNewDocument(final MultipartFile multipartFile, final String customeridentifier) throws IOException {
        DocumentStorageEntity storageEntity = DocumentMapper.map(multipartFile, customeridentifier);
        DocumentStorageEntity entity = this.documentStorageRepository.save(storageEntity);
        return DocumentMapper.map(entity);
    }

    public Optional<DocumentStorageEntity> findDocumentStorageByUUID(final String uuid) {
        return this.documentStorageRepository.findByUuid(uuid);
    }

    public CustomerDocument findCustomerDocuments(final String customerIdentifier) {
        final CustomerEntity customerEntity = this.customerRepository.findByIdentifier(customerIdentifier).orElseThrow(() -> ServiceException.notFound("Customer not found"));
        final DocumentEntity documentEntity = this.documentRepository.findByCustomerId(customerIdentifier).orElseThrow(() -> ServiceException.notFound("Document not found"));
        CustomerDocument customerDocument = DocumentMapper.map(documentEntity);
        final Map<String, List<DocumentEntryEntity>> documentEntryEntity =
                this.documentEntryRepository
                        .findByDocumentAndStatusNot(documentEntity, CustomerDocument.Status.DELETED.name()).stream()
                        .collect(groupingBy(DocumentEntryEntity::getType, toList()));

        List<DocumentsType> documentsType = DocumentMapper.map(documentEntryEntity);
        customerDocument.setDocumentsTypes(documentsType);

        Set<String> doc_master = this.documentTypeRepository.findByUserType(customerEntity.getType())
                .stream()
                .map(DocumentTypeEntity::getTitle)
                .collect(Collectors.toSet());
        final boolean isDocAvailable = documentEntryEntity
                .keySet()
                .equals(doc_master);
        customerDocument.setKycStatus(documentsType
                .stream()
                .allMatch(DocumentsType::isKYCVerified) && isDocAvailable);
        return customerDocument;
    }

    public List<DocumentsMaster> findDocumentsTypesMaster(final String customerIdentifier) {
        final CustomerEntity customerEntity = this.customerRepository.findByIdentifier(customerIdentifier).orElseThrow(() -> ServiceException.notFound("Customer not found"));
        List<DocumentTypeEntity> documentTypeEntities = this.documentTypeRepository.findByUserType(customerEntity.getType());

        return documentTypeEntities.stream().map(documentTypeEntity -> {
            List<DocumentSubTypeEntity> documentSubTypeEntities = this.documentSubTypeRepository.findByDocumentType(documentTypeEntity);
            List<DocumentsMasterSubtype> documentsMasterSubtypes = DocumentMapper.map(documentSubTypeEntities);
            return DocumentMapper.map(documentTypeEntity, documentsMasterSubtypes);
        }).collect(toList());


    }

    public CustomerDocument findCustomerUploadedDocuments(final String customerIdentifier) {
        final Optional<DocumentEntity> documentEntity = this.documentRepository.findByCustomerId(customerIdentifier);
        CustomerDocument customerDocument = new CustomerDocument();
        if (documentEntity.isPresent()) {
            customerDocument = DocumentMapper.map(documentEntity.get());
            final Map<String, List<DocumentEntryEntity>> documentEntryEntity =
                    this.documentEntryRepository.findByDocumentAndStatus(documentEntity.get(), CustomerDocument.Status.UPLOADED.name()).stream()
                            .collect(groupingBy(DocumentEntryEntity::getType, toList()));
            List<DocumentsType> documentsType = DocumentMapper.map(documentEntryEntity);
            customerDocument.setDocumentsTypes(documentsType);
            customerDocument.setKycStatus(documentsType.stream().allMatch(DocumentsType::isKYCVerified));
        }
        return customerDocument;
    }

    public Optional<CustomerDocumentEntry> findDocument(final String customerIdentifier,
                                                        final Long documentIdentifier) {
        return this.documentEntryRepository.findByCustomerIdAndDocumentId(customerIdentifier, documentIdentifier)
                .map(DocumentMapper::map);
    }

    public boolean documentExists(final String customerIdentifier, final Long documentIdentifier) {
        return findDocument(customerIdentifier, documentIdentifier).isPresent();
    }

    public boolean isDocumentExistByCustomerIdentifier(final String identifier) {
        return this.documentRepository.findByIdentifierAndCustomer(identifier);
    }


    public boolean isDocumentCompleted(final String customerIdentifier,
                                       final Long documentIdentifier) {
        final Optional<DocumentEntryEntity> documentEntityOptional = documentEntryRepository.findByCustomerIdAndDocumentId(customerIdentifier, documentIdentifier);
        return documentEntityOptional.map(DocumentEntryEntity::getStatus).get().equals(CustomerDocument.Status.APPROVED.name());
    }

}
