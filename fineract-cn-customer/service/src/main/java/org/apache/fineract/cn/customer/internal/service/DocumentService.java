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

import org.apache.fineract.cn.api.util.UserContextHolder;
import org.apache.fineract.cn.customer.api.v1.domain.*;
import org.apache.fineract.cn.customer.internal.mapper.ContactDetailMapper;
import org.apache.fineract.cn.customer.internal.mapper.CustomerMapper;
import org.apache.fineract.cn.customer.internal.mapper.DocumentMapper;
import org.apache.fineract.cn.customer.internal.repository.*;
import org.apache.fineract.cn.lang.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    private final CAdminService cAdminService;

    @Autowired
    public DocumentService(
            final DocumentRepository documentRepository,
            final DocumentEntryRepository documentEntryRepository,
            final DocumentStorageRepository documentStorageRepository,
            final DocumentTypeRepository documentTypeRepository,
            final CustomerRepository customerRepository,
            final DocumentSubTypeRepository documentSubTypeRepository,
            final ContactDetailRepository contactDetailRepository,
            final CAdminService cAdminService) {
        this.documentRepository = documentRepository;
        this.documentEntryRepository = documentEntryRepository;
        this.documentStorageRepository = documentStorageRepository;
        this.documentTypeRepository = documentTypeRepository;
        this.customerRepository = customerRepository;
        this.documentSubTypeRepository = documentSubTypeRepository;
        this.contactDetailRepository = contactDetailRepository;
        this.cAdminService = cAdminService;
    }

    public Optional<DocumentStorageEntity> findDocumentStorageByUUID(final String uuid) {
        return this.documentStorageRepository.findByUuid(uuid);
    }


    public CustomerDocument findCustomerDocuments(final String customerIdentifier) {
        final CustomerEntity customerEntity = this.customerRepository
                .findByIdentifier(customerIdentifier).orElseThrow(() -> ServiceException.notFound("Customer not found"));
        return cAdminService.findCustomerDocumentsByCustomerEntity(customerEntity);
    }

    public Optional<DocumentEntity> documentEntity(final CustomerEntity customerEntity) {
        return this.documentRepository.findByCustomer(customerEntity);
    }


    public List<Customer> findCustomersByKYCStatus(final String status) {
        List<CustomerEntity> customerEntities = this.customerRepository
                .findAllByTypeNotIn(new HashSet<>(Arrays.asList(Customer.UserType.SADMIN.name(), Customer.UserType.CADMIN.name())));
        return customerEntities.stream().map(entity -> {
            Customer customer = CustomerMapper.map(entity);
            final List<ContactDetailEntity> contactDetailEntities = contactDetailRepository.findByCustomer(entity);
            if (contactDetailEntities != null) {
                customer.setContactDetails(contactDetailEntities.stream()
                        .map(ContactDetailMapper::map).collect(Collectors.toList()));
            }

//            set kyc documents for the customers, fetched by customers identifier
            CustomerDocument customerDocument = findCustomerDocuments(customer.getIdentifier());
            customer.setCustomerDocument(customerDocument);
            return customer;

        }).filter(customer -> customer.getCustomerDocument().getKycStatusText() != null && customer.getCustomerDocument().getKycStatusText().equals(status.toUpperCase()))
                .collect(Collectors.toList());
    }


    public UserContactVerificationStatus findCustomersContactDetails(final String identifier) {
//        final ContactDetailEntity contactDetailEntity = this.contactDetailRepository.findAllByValue(identifier).stream().findFirst().orElseThrow(() -> ServiceException.notFound("NOT FOUND"));
        return this.contactDetailRepository.findAllByValue(identifier).stream().findFirst().map(contactDetailEntity -> {
            UserContactVerificationStatus userContactVerificationStatus = new UserContactVerificationStatus();
            switch (contactDetailEntity.getType()) {
                case "EMAIL":
                    userContactVerificationStatus.setEmail(contactDetailEntity.getValue());
                    boolean isEmailValid = this.contactDetailRepository.existsByIdentifierAndTypeAndValid(contactDetailEntity.getValue(), "EMAIL");
                    userContactVerificationStatus.setEmailVerified(isEmailValid);
                    CustomerEntity customerEntity = contactDetailEntity.getCustomer();
                    if (isEmailValid) {
                        ContactDetailEntity e = contactDetailRepository.findAllByValueAndTypeAndValid(contactDetailEntity.getValue(), "EMAIL", true).stream().findFirst().get();
                        userContactVerificationStatus.setUsername(e.getCustomer().getIdentifier());
                    }
                    Optional<ContactDetailEntity> mobileContactDetail = contactDetailRepository.findByCustomerAndType(customerEntity, "MOBILE").stream().findFirst();

                    if (mobileContactDetail.isPresent()) {
                        Optional<ContactDetailEntity> contactDetailData = contactDetailRepository.findAllByValue(mobileContactDetail.get().getValue()).stream().findFirst();
                        if (contactDetailData.isPresent()) {
                            userContactVerificationStatus.setMobile(contactDetailData.get().getValue());
                            userContactVerificationStatus.setMobileVerified(this.contactDetailRepository.existsByIdentifierAndTypeAndValid(contactDetailData.get().getValue(), "MOBILE"));
                        } else {
                            userContactVerificationStatus.setMobile(null);
                            userContactVerificationStatus.setMobileVerified(false);
                        }


                    } else {
                        userContactVerificationStatus.setMobile(null);
                        userContactVerificationStatus.setMobileVerified(false);
                    }

                    break;
                case "MOBILE":
                    userContactVerificationStatus.setMobile(contactDetailEntity.getValue());
                    boolean isMobileValid = this.contactDetailRepository.existsByIdentifierAndTypeAndValid(contactDetailEntity.getValue(), "MOBILE");
                    userContactVerificationStatus.setMobileVerified(isMobileValid);
                    CustomerEntity customerEntity1 = contactDetailEntity.getCustomer();
                    if (isMobileValid) {
                        ContactDetailEntity d = contactDetailRepository.findAllByValueAndTypeAndValid(contactDetailEntity.getValue(), "MOBILE", true).stream().findFirst().get();
                        userContactVerificationStatus.setUsername(d.getCustomer().getIdentifier());
                    }
                    Optional<ContactDetailEntity> detailEntity = contactDetailRepository.findByCustomerAndType(customerEntity1, "EMAIL").stream().findFirst();

                    if (detailEntity.isPresent()) {
                        detailEntity = contactDetailRepository.findAllByValue(detailEntity.get().getValue()).stream().findFirst();

                        if (detailEntity.isPresent()) {
                            userContactVerificationStatus.setEmail(detailEntity.get().getValue());
                            userContactVerificationStatus.setEmailVerified(this.contactDetailRepository.existsByIdentifierAndTypeAndValid(detailEntity.get().getValue(), "EMAIL"));

                        } else {
                            userContactVerificationStatus.setEmail(null);
                            userContactVerificationStatus.setEmailVerified(false);

                        }

                    } else {
                        userContactVerificationStatus.setEmail(null);
                        userContactVerificationStatus.setEmailVerified(false);
                    }
                    break;
                default:
                    userContactVerificationStatus.setEmail(null);
                    userContactVerificationStatus.setMobile(null);
                    userContactVerificationStatus.setMobileVerified(false);
                    userContactVerificationStatus.setEmailVerified(false);
            }
            return userContactVerificationStatus;

        }).orElseGet(() -> {
            UserContactVerificationStatus userContactVerificationStatus = new UserContactVerificationStatus();
            userContactVerificationStatus.setEmail(null);
            userContactVerificationStatus.setMobile(null);
            userContactVerificationStatus.setUsername(null);
            userContactVerificationStatus.setMobileVerified(false);
            userContactVerificationStatus.setEmailVerified(false);
            return userContactVerificationStatus;

        });
    }


    public String getDocumentTypeTitle(final String uuid) {
        return this.documentTypeRepository.findByUuid(uuid).orElseThrow(() -> ServiceException.notFound("Document UserType Not Found")).getTitle();
    }

    public List<DocumentsMaster> findDocumentsTypesMaster(final String customerIdentifier) {
        final CustomerEntity customerEntity = this.customerRepository.findByIdentifier(customerIdentifier).orElseThrow(() -> ServiceException.notFound("Customer not found"));
        List<DocumentTypeEntity> documentTypeEntities = this.documentTypeRepository.findByUserType(customerEntity.getType());

        return documentTypeEntities.stream().map(documentTypeEntity -> {
            List<DocumentSubTypeEntity> documentSubTypeEntities = this.documentSubTypeRepository.findByDocumentType(documentTypeEntity);
            List<DocumentsMasterSubtype> documentsMasterSubtypes = DocumentMapper.map(documentSubTypeEntities, documentTypeEntity);
            return DocumentMapper.map(documentTypeEntity, documentsMasterSubtypes);
        }).collect(toList());
    }

    public List<DocumentsMaster> findDocumentsTypes() {
        List<DocumentTypeEntity> documentTypeEntities = this.documentTypeRepository.findAll();
        return documentTypeEntities.stream().map(entity -> {
            List<DocumentsMasterSubtype> masterSubtypes = this.findDocumentsSubTypes().stream().filter(stypes -> stypes.getDocTypeId().equals(entity.getId())).collect(toList());
            DocumentsMaster master = DocumentMapper.map(entity);
            master.setDocumentsMasterSubtypes(masterSubtypes);
            return master;
        }).collect(toList());
    }


    public List<DocumentsMasterSubtype> findDocumentsSubTypes() {
        List<DocumentTypeEntity> documentTypeEntities = this.documentTypeRepository.findAll();
        List<DocumentSubTypeEntity> documentSubTypeEntities = this.documentSubTypeRepository.findAll();
        return documentSubTypeEntities.stream().map(entity -> {
            DocumentsMasterSubtype subtype = DocumentMapper.map(entity);
            documentTypeEntities.stream()
                    .filter(documentTypeEntity -> documentTypeEntity.getId().equals(subtype.getDocTypeId()))
                    .findFirst()
                    .ifPresent(d -> {
                        subtype.setDocTypeUUID(d.getUuid());
                    });
            return subtype;
        }).collect(toList());
    }

    public Optional<DocumentSubTypeEntity> findDocumentSubTypeEntityByUuid(final DocumentTypeEntity documentType, final String uuid) {
        return this.documentSubTypeRepository.findByDocumentTypeAndUuid(documentType, uuid);
    }

    public Optional<DocumentTypeEntity> findByUserTypeAndUuidAndActiveTrue(final String userType, final String uuid) {
        return this.documentTypeRepository.findByUserTypeAndUuidAndActiveIsTrue(userType, uuid);
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

    public List<DocumentEntryEntity> findAllByTypeAndStatusNot(String docType) {
        return this.documentEntryRepository.findAllByTypeAndCreatedByAndStatusNot(docType, UserContextHolder.checkedGetUser(), CustomerDocument.Status.DELETED.name());
    }

}
