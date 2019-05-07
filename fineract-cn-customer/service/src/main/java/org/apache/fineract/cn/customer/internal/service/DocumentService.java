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
import org.apache.fineract.cn.customer.internal.mapper.CustomerMapper;
import org.apache.fineract.cn.customer.internal.mapper.DocumentMapper;
import org.apache.fineract.cn.customer.internal.repository.*;
import org.apache.fineract.cn.lang.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Clock;
import java.time.LocalDateTime;
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


    public DocumentStorage addNewDocument(final MultipartFile multipartFile, final String customeridentifier, final String docType) throws IOException {
        DocumentStorageEntity storageEntity = DocumentMapper.map(multipartFile, customeridentifier, docType);
        DocumentStorageEntity entity = this.documentStorageRepository.save(storageEntity);
        return DocumentMapper.map(entity);
    }

    public Optional<DocumentStorageEntity> findDocumentStorageByUUID(final String uuid) {
        return this.documentStorageRepository.findByUuid(uuid);
    }


    public CustomerDocument findCustomerDocuments(final String customerIdentifier) {
        final CustomerEntity customerEntity = this.customerRepository.findByIdentifier(customerIdentifier).orElseThrow(() -> ServiceException.notFound("Customer not found"));
        final Optional<DocumentEntity> documentEntity = this.documentRepository.findByCustomerId(customerIdentifier);

        CustomerDocument customerDocument = new CustomerDocument();
        if (documentEntity.isPresent()) {
            customerDocument = DocumentMapper.map(documentEntity.get());
            final Map<String, List<DocumentEntryEntity>> documentEntryEntity =
                    this.documentEntryRepository
                            .findByDocumentAndStatusNot(documentEntity.get(), CustomerDocument.Status.DELETED.name()).stream()
                            .collect(groupingBy(DocumentEntryEntity::getType, toList()));

            List<DocumentsType> documentsType = new ArrayList<>();
            documentEntryEntity.forEach((key, documentEntryEntities) -> {
                final List<DocumentsSubType> documentsSubTypeList = new ArrayList<>();
                final DocumentsType type = new DocumentsType();
                documentEntryEntities.forEach(entity -> {
                    final DocumentsSubType documentsSubType = new DocumentsSubType();
                    documentsSubType.setId(entity.getId());
                    documentsSubType.setCreated_by(entity.getCreatedBy());
                    documentsSubType.setStatus(entity.getStatus());
                    documentsSubType.setType(this.getDocumentTypeTitle(entity.getType()));
                    documentsSubType.setSubType(this.getDocumentSubTypeTitle(entity.getSubType()));
                    setKycDocumentMapper(documentsSubTypeList, entity, documentsSubType);
                });

                DocumentMapper.setDocumentTypeStatus(documentEntryEntities, type);
                type.setType(this.getDocumentTypeTitle(key));
                type.setDocumentsSubType(documentsSubTypeList);
                documentsType.add(type);
            });

            customerDocument.setDocumentsTypes(documentsType);

            Set<String> doc_master =
                    this.documentTypeRepository.findByUserType(customerEntity.getType())
                            .stream()
                            .map(DocumentTypeEntity::getUuid)
                            .collect(Collectors.toSet());

            final boolean isDocAvailable = documentEntryEntity.keySet().equals(doc_master);

            if (documentsType.stream().allMatch(type -> type.getStatus().equals(CustomerDocument.Status.APPROVED.name())) && isDocAvailable) {
                customerDocument.setKycStatusText(CustomerDocument.Status.APPROVED.name());
                customerDocument.setKycStatus(true);
            } else if ((documentsType.stream().anyMatch(type -> type.getStatus().equals(CustomerDocument.Status.APPROVED.name()))
                    || documentsType.stream().noneMatch(type -> type.getStatus().equals(CustomerDocument.Status.APPROVED.name())))
                    && isDocAvailable
                    && documentsType.stream().anyMatch(type -> type.getStatus().equals(CustomerDocument.Status.REJECTED.name()))) {
                customerDocument.setKycStatusText(CustomerDocument.Status.REJECTED.name());
                customerDocument.setKycStatus(false);
            } else {
                customerDocument.setKycStatusText(CustomerDocument.Status.PENDING.name());
                customerDocument.setKycStatus(false);
            }

        }


        return customerDocument;
    }

    public static void setKycDocumentMapper(List<DocumentsSubType> documentsSubTypeList, DocumentEntryEntity doc, DocumentsSubType documentsSubType) {
        documentsSubType.setApprovedBy(doc.getApprovedBy());
        documentsSubType.setRejectedBy(doc.getRejectedBy());
        documentsSubType.setReasonForReject(doc.getReasonForReject());
        documentsSubType.setDescription(doc.getDescription());
        documentsSubType.setCreatedOn(doc.getCreatedOn().toString());
        documentsSubType.setDocRef(doc.getDocRef());
        documentsSubTypeList.add(documentsSubType);
    }

    public CustomerPage findCustomersByKYCStatus(final String status, final Pageable pageable) {

//        this.documentRepository.findAll().stream().filter(documentEntity -> {
//            CustomerDocument customerDocument = this.findCustomerDocumentsForCA(documentEntity.getIdentifier());
//            return customerDocument.isKycStatus() ==
//        })

//        Page<CustomerEntity> customerEntities = this.customerRepository.findAllByType(Customer.Type.PERSON.name(), pageable);
//        CustomerPage customerPage = new CustomerPage();
//        customerPage.setTotalPages(customerEntities.getTotalPages());
//        customerPage.setTotalElements(customerEntities.getTotalElements());
//        if (customerEntities.getSize() > 0) {
//            final ArrayList<Customer> customers = new ArrayList<>(customerEntities.getSize());
//            customerPage.setCustomers(customers);
//            for (CustomerEntity customerEntity : customerEntities) {
//                Customer customer = CustomerMapper.map(customerEntity);
//                customer.setCustomerDocument(this.documentService.findCustomerDocumentsForCA(customer.getIdentifier()));
//                customers.add(customer);
//            }
//        }
//        return customerPage;

        return new CustomerPage();
    }
//    CustomerDocument findCustomerDocumentsForCA(final String customerIdentifier) {
//        final Optional<CustomerEntity> customerEntity = this.customerRepository.findByIdentifier(customerIdentifier);
//        final Optional<DocumentEntity> documentEntity = this.documentRepository.findByCustomerId(customerIdentifier);
//        CustomerDocument customerDocument = new CustomerDocument();
//        if (documentEntity.isPresent()) {
//
//            customerDocument = DocumentMapper.map(documentEntity.get());
//            final Map<String, List<DocumentEntryEntity>> documentEntryEntity =
//                    this.documentEntryRepository
//                            .findByDocumentAndStatusNot(documentEntity.get(), CustomerDocument.Status.DELETED.name()).stream()
//                            .collect(groupingBy(DocumentEntryEntity::getType, toList()));
//
//            List<DocumentsType> documentsType = new ArrayList<>();
//            documentEntryEntity.forEach((key, documentEntryEntities) -> {
//                final List<DocumentsSubType> documentsSubTypeList = new ArrayList<>();
//                final DocumentsType type = new DocumentsType();
//                type.setKYCVerified(false);
//                documentEntryEntities.forEach(entity -> {
//                    final DocumentsSubType documentsSubType = new DocumentsSubType();
//                    documentsSubType.setId(entity.getId());
//                    documentsSubType.setCreated_by(entity.getCreatedBy());
//                    documentsSubType.setStatus(entity.getStatus());
//                    documentsSubType.setType(this.getDocumentTypeTitle(entity.getType()));
//                    documentsSubType.setSubType(this.getDocumentSubTypeTitle(entity.getSubType()));
//                    setKycDocumentMapper(documentsSubTypeList, type, entity, documentsSubType);
//                });
//
//                type.setType(this.getDocumentTypeTitle(key));
//                type.setDocumentsSubType(documentsSubTypeList);
//                documentsType.add(type);
//            });
//            customerDocument.setDocumentsTypes(documentsType);
//
//            Set<String> doc_master =
//                    this.documentTypeRepository.findByUserType(customerEntity.get().getType())
//                            .stream()
//                            .map(DocumentTypeEntity::getUuid)
//                            .collect(Collectors.toSet());
//
//            final boolean isDocAvailable = documentEntryEntity.keySet().equals(doc_master);
//
//            if (documentsType.stream().allMatch(type -> type.getType().equals(CustomerDocument.Status.APPROVED.name())) && isDocAvailable) {
//                customerDocument.setKycStatus(CustomerDocument.Status.APPROVED.name());
//            } else if (!documentsType.stream().allMatch(type -> type.getType().equals(CustomerDocument.Status.APPROVED.name()))
//                    && documentsType.stream().anyMatch(type -> type.getType().equals(CustomerDocument.Status.REJECTED.name()))
//                    && isDocAvailable) {
//                customerDocument.setKycStatus(CustomerDocument.Status.APPROVED.name());
//            } else {
//                customerDocument.setKycStatus(CustomerDocument.Status.PENDING.name());
//            }
//        }
//
//
//        return customerDocument;

//    }


    private String getDocumentTypeTitle(final String uuid) {
        Optional<DocumentTypeEntity> documentTypeEntity = this.documentTypeRepository.findByUuid(uuid);
        if (documentTypeEntity.isPresent()) {
            return documentTypeEntity.get().getTitle();
        } else {
            return "NOT-FOUND";
        }
    }

    private String getDocumentSubTypeTitle(final String uuid) {
        Optional<DocumentSubTypeEntity> documentSubTypeEntity = this.documentSubTypeRepository.findByUuid(uuid);
        if (documentSubTypeEntity.isPresent()) {
            return documentSubTypeEntity.get().getTitle();
        } else {
            return "NOT-FOUND";
        }
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

    public Optional<DocumentSubTypeEntity> findDocumentSubTypeEntityByUuid(final DocumentTypeEntity documentType, final String uuid) {
        return this.documentSubTypeRepository.findByDocumentTypeAndUuid(documentType, uuid);
    }


    public Optional<DocumentTypeEntity> findDocumentTypeEntityByUserTypeAndUuid(final String userType, final String uuid) {
        return this.documentTypeRepository.findByUserTypeAndUuid(userType, uuid);
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
