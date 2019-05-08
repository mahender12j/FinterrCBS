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
import org.apache.fineract.cn.customer.catalog.api.v1.domain.Field;
import org.apache.fineract.cn.customer.internal.mapper.ContactDetailMapper;
import org.apache.fineract.cn.customer.internal.mapper.CustomerMapper;
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
    private final ContactDetailRepository contactDetailRepository;

    @Autowired
    public DocumentService(
            final DocumentRepository documentRepository,
            final DocumentEntryRepository documentEntryRepository,
            final DocumentStorageRepository documentStorageRepository,
            final DocumentTypeRepository documentTypeRepository,
            final CustomerRepository customerRepository,
            final DocumentSubTypeRepository documentSubTypeRepository, ContactDetailRepository contactDetailRepository) {
        this.documentRepository = documentRepository;
        this.documentEntryRepository = documentEntryRepository;
        this.documentStorageRepository = documentStorageRepository;
        this.documentTypeRepository = documentTypeRepository;
        this.customerRepository = customerRepository;
        this.documentSubTypeRepository = documentSubTypeRepository;
        this.contactDetailRepository = contactDetailRepository;
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


    public List<Customer> findCustomersByKYCStatus(final String status) {
        List<CustomerEntity> customerEntities = this.customerRepository.findAllByTypeIn(new HashSet<>(Arrays.asList(Customer.Type.PERSON.name(), Customer.Type.BUSINESS.name())));

        return customerEntities.stream().map(entity -> {
            Customer customer = CustomerMapper.map(entity);
            final Optional<DocumentEntity> documentEntity = this.documentRepository.findByCustomerId(entity.getIdentifier());

//            System.out.println(documentEntity.toString() + "\n\n");

            CustomerDocument customerDocument = this.findCustomerDocuments(entity.getIdentifier());
            customer.setCustomerDocument(customerDocument);
            return customer;

        })
                .filter(customer -> customer.getCustomerDocument().getKycStatusText().equals(status.toUpperCase()))
                .collect(Collectors.toList());
    }


    public UserContactVerificationStatus findCustomersContactDetails(final String identifier) {
        final ContactDetailEntity contactDetailEntity = this.contactDetailRepository.findAllByValue(identifier).stream().findFirst().orElseThrow(() -> ServiceException.notFound("NOT FOUND"));
        UserContactVerificationStatus userContactVerificationStatus = new UserContactVerificationStatus();

        switch (contactDetailEntity.getType()) {
            case "EMAIL":
                userContactVerificationStatus.setEmail(contactDetailEntity.getValue());
                userContactVerificationStatus.setEmailVerified(this.contactDetailRepository.existsByIdentifierAndTypeAndValid(contactDetailEntity.getValue(), "EMAIL"));
                CustomerEntity customerEntity = contactDetailEntity.getCustomer();
                userContactVerificationStatus.setUsername(customerEntity.getIdentifier());
                Optional<ContactDetailEntity> mobileContactDetail = contactDetailRepository.findByCustomerAndType(customerEntity, "MOBILE").stream().findFirst();

                if (mobileContactDetail.isPresent()) {
                    Optional<ContactDetailEntity> contactDetailData = contactDetailRepository.findAllByValue(mobileContactDetail.get().getValue()).stream().findFirst();
                    if (contactDetailData.isPresent()) {
                        userContactVerificationStatus.setMobile(contactDetailData.get().getValue());
                        userContactVerificationStatus.setMobileVerified(this.contactDetailRepository.existsByIdentifierAndTypeAndValid(contactDetailData.get().getValue(), "MOBILE"));
                    } else {
                        userContactVerificationStatus.setMobile("");
                        userContactVerificationStatus.setMobileVerified(false);
                    }


                } else {
                    userContactVerificationStatus.setMobile("");
                    userContactVerificationStatus.setMobileVerified(false);
                }

                break;
            case "MOBILE":
                userContactVerificationStatus.setMobile(contactDetailEntity.getValue());
                userContactVerificationStatus.setMobileVerified(this.contactDetailRepository.existsByIdentifierAndTypeAndValid(contactDetailEntity.getValue(), "MOBILE"));

                CustomerEntity customerEntity1 = contactDetailEntity.getCustomer();
                userContactVerificationStatus.setUsername(customerEntity1.getIdentifier());
                Optional<ContactDetailEntity> detailEntity = contactDetailRepository.findByCustomerAndType(customerEntity1, "EMAIL").stream().findFirst();


                if (detailEntity.isPresent()) {
                    detailEntity = contactDetailRepository.findAllByValue(detailEntity.get().getValue()).stream().findFirst();

                    if (detailEntity.isPresent()) {
                        userContactVerificationStatus.setEmail(detailEntity.get().getValue());
                        userContactVerificationStatus.setEmailVerified(this.contactDetailRepository.existsByIdentifierAndTypeAndValid(detailEntity.get().getValue(), "EMAIL"));

                    } else {
                        userContactVerificationStatus.setEmail("");
                        userContactVerificationStatus.setEmailVerified(false);

                    }

                } else {
                    userContactVerificationStatus.setEmail("");
                    userContactVerificationStatus.setEmailVerified(false);
                }
                break;
            default:
                userContactVerificationStatus.setEmail("");
                userContactVerificationStatus.setMobile("");
                userContactVerificationStatus.setMobileVerified(false);
                userContactVerificationStatus.setEmailVerified(false);
        }

        return userContactVerificationStatus;
    }

    private List<CustomerDocument> findAllCustomerKYCDetails(CustomerEntity customerEntity) {
        //        call the master types so later can be filtered
        List<DocumentTypeEntity> doc_master_ent = this.documentTypeRepository.findAll();
        List<DocumentSubTypeEntity> doc_sub_master_ent = this.documentSubTypeRepository.findAll();

        return this.documentRepository.findAll()
                .stream()
                .map(documentEntity -> {
                    CustomerDocument customerDocument = DocumentMapper.map(documentEntity);
                    final Map<String, List<DocumentEntryEntity>> documentEntryEntity =
                            this.documentEntryRepository
                                    .findByDocumentAndStatusNot(documentEntity, CustomerDocument.Status.DELETED.name())
                                    .stream()
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
                            documentsSubType.setType(doc_master_ent.stream().filter(d -> d.getUuid().equals(entity.getType())).findAny().orElseThrow(null).getTitle());
                            documentsSubType.setSubType(this.getDocumentSubTypeTitle(entity.getSubType()));
                            setKycDocumentMapper(documentsSubTypeList, entity, documentsSubType);
                        });

                        DocumentMapper.setDocumentTypeStatus(documentEntryEntities, type);
                        type.setType(this.getDocumentTypeTitle(key));
                        type.setDocumentsSubType(documentsSubTypeList);
                        documentsType.add(type);
                    });

                    customerDocument.setDocumentsTypes(documentsType);

                    Set<String> doc_master = doc_master_ent
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
                    return customerDocument;
                }).collect(Collectors.toList());
    }


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
