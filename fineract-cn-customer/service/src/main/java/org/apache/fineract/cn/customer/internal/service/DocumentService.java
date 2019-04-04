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

import org.apache.fineract.cn.customer.api.v1.domain.CustomerDocument;
import org.apache.fineract.cn.customer.api.v1.domain.CustomerDocumentEntry;
import org.apache.fineract.cn.customer.api.v1.domain.DocumentStorage;
import org.apache.fineract.cn.customer.api.v1.domain.DocumentsType;
import org.apache.fineract.cn.customer.internal.mapper.DocumentMapper;
import org.apache.fineract.cn.customer.internal.repository.*;
import org.apache.fineract.cn.lang.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

/**
 * @author Myrle Krantz
 */
@Service
public class DocumentService {
    private final DocumentRepository documentRepository;
    private final DocumentPageRepository documentPageRepository;
    private final DocumentEntryRepository documentEntryRepository;
    private final DocumentStorageRepository documentStorageRepository;

    @Autowired
    public DocumentService(
            final DocumentRepository documentRepository,
            final DocumentEntryRepository documentEntryRepository,
            final DocumentStorageRepository documentStorageRepository,
            final DocumentPageRepository documentPageRepository) {
        this.documentRepository = documentRepository;
        this.documentEntryRepository = documentEntryRepository;
        this.documentPageRepository = documentPageRepository;
        this.documentStorageRepository = documentStorageRepository;
    }

    public Optional<DocumentPageEntity> findPage(final String customerIdentifier,
                                                 final String documentIdentifier,
                                                 final Integer pageNumber) {
        return this.documentPageRepository.findByCustomerIdAndDocumentIdentifierAndPageNumber(
                customerIdentifier,
                documentIdentifier,
                pageNumber);
    }


    public DocumentStorage addNewDocument(final MultipartFile multipartFile, final String customeridentifier) throws IOException {
        DocumentStorageEntity storageEntity = DocumentMapper.map(multipartFile, customeridentifier);
        DocumentStorageEntity entity = this.documentStorageRepository.save(storageEntity);
        return DocumentMapper.map(entity);
    }


    public DocumentStorage findDocumentStorage(final String uuid) {

        Optional<DocumentStorageEntity> documentEntity = this.documentStorageRepository.findByUuid(uuid);
        if (documentEntity.isPresent()) {
            return DocumentMapper.map(documentEntity.get());
        } else {
            throw ServiceException.notFound("Document {0} not found.", uuid);
        }
    }


    public Optional<DocumentStorageEntity> findDocumentStorageByUUID(final String uuid) {
        return this.documentStorageRepository.findByUuid(uuid);
    }


    public Optional<DocumentPageEntity> findPagebyDocumentID(final Long documentIdentifier) {
        return this.documentPageRepository.findByDocumentEntryId(documentIdentifier);
    }

    public Stream<CustomerDocument> find(final String customerIdentifier) {
        final Stream<DocumentEntity> preMappedRet = this.documentRepository.findByCustomerId(customerIdentifier);
        return preMappedRet.map(DocumentMapper::map);
    }


    public CustomerDocument findCustomerDocuments(final String customerIdentifier) {
        final Optional<DocumentEntity> documentEntity = this.documentRepository.findByCustomerId(customerIdentifier).findFirst();
        CustomerDocument customerDocument = new CustomerDocument();
        if (documentEntity.isPresent()) {
            customerDocument = DocumentMapper.map(documentEntity.get());
            final Map<String, List<DocumentEntryEntity>> documentEntryEntity = this.documentEntryRepository.findByDocumentAndStatusNotAndStatusNot(documentEntity.get(), "DELETED", "UPLOADED").stream()
                    .collect(groupingBy(DocumentEntryEntity::getType, toList()));
            List<DocumentsType> documentsType = DocumentMapper.map(documentEntryEntity);
            customerDocument.setDocumentsTypes(documentsType);
            customerDocument.setKycStatus(documentsType.stream().allMatch(DocumentsType::isKYCVerified));
        }
        return customerDocument;
    }

    public CustomerDocument findCustomerUploadedDocuments(final String customerIdentifier) {
        final Optional<DocumentEntity> documentEntity = this.documentRepository.findByCustomerId(customerIdentifier).findFirst();
        CustomerDocument customerDocument = new CustomerDocument();
        if (documentEntity.isPresent()) {
            customerDocument = DocumentMapper.map(documentEntity.get());
            final Map<String, List<DocumentEntryEntity>> documentEntryEntity = this.documentEntryRepository.findByDocumentAndStatus(documentEntity.get(), "UPLOADED").stream()
                    .collect(groupingBy(DocumentEntryEntity::getType, toList()));
            List<DocumentsType> documentsType = DocumentMapper.map(documentEntryEntity);
            customerDocument.setDocumentsTypes(documentsType);
            customerDocument.setKycStatus(documentsType.stream().allMatch(d -> d.isKYCVerified()));
        }
        return customerDocument;
    }

    public List<DocumentEntryEntity> findUploadedDocumentEntries(final String customerIdentifier) {
        final Optional<DocumentEntity> documentEntity = this.documentRepository.findByCustomerId(customerIdentifier).findFirst();
        List<DocumentEntryEntity> entries = this.documentEntryRepository.findByDocumentAndStatus(documentEntity.get(), "UPLOADED");
        return entries;
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

    public Stream<Integer> findPageNumbers(final String customerIdentifier,
                                           final String documentIdentifier) {
        return documentPageRepository.findByCustomerIdAndDocumentIdentifier(customerIdentifier, documentIdentifier)
                .map(DocumentPageEntity::getPageNumber);
    }

    public void submitDocuments(final String customerIdentifier) {
        List<DocumentEntryEntity> entries = this.findUploadedDocumentEntries(customerIdentifier);
        entries.forEach(e -> {
            e.setStatus("PENDING");
        });
        documentEntryRepository.save(entries);
    }

    public boolean isDocumentCompleted(final String customerIdentifier,
                                       final Long documentIdentifier) {
        final Optional<DocumentEntryEntity> documentEntityOptional = documentEntryRepository.findByCustomerIdAndDocumentId(customerIdentifier, documentIdentifier);
        return documentEntityOptional.map(DocumentEntryEntity::getStatus).get().equals("APPROVED");
    }

    public boolean isDocumentMissingPages(final String customerIdentifier,
                                          final String documentIdentifier) {
        final List<Integer> pageNumbers = findPageNumbers(customerIdentifier, documentIdentifier)
                .sorted(Integer::compareTo)
                .collect(Collectors.toList());
        for (int i = 0; i < pageNumbers.size(); i++) {
            if (i != pageNumbers.get(i))
                return true;
        }

        return false;
    }
}
