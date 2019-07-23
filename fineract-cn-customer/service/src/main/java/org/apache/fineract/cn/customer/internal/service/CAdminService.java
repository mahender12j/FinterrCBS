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

import org.apache.fineract.cn.cause.api.v1.domain.CaAdminCauseData;
import org.apache.fineract.cn.customer.api.v1.domain.*;
import org.apache.fineract.cn.customer.internal.mapper.CadminMapper;
import org.apache.fineract.cn.customer.internal.mapper.CustomerMapper;
import org.apache.fineract.cn.customer.internal.mapper.DocumentMapper;
import org.apache.fineract.cn.customer.internal.repository.*;
import org.apache.fineract.cn.customer.internal.service.helperService.CauseAdaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@Service
public class CAdminService {

    private final CustomerRepository customerRepository;
    private final DocumentRepository documentRepository;
    private final DocumentEntryRepository documentEntryRepository;
    private final DocumentTypeRepository documentTypeRepository;
    private final DocumentSubTypeRepository documentSubTypeRepository;
    private final CauseAdaptor causeAdaptor;

    @Autowired
    public CAdminService(final CustomerRepository customerRepository,
                         final DocumentRepository documentRepository,
                         final DocumentEntryRepository documentEntryRepository,
                         final DocumentTypeRepository documentTypeRepository,
                         final DocumentSubTypeRepository documentSubTypeRepository,
                         final CauseAdaptor causeAdaptor) {
        super();
        this.customerRepository = customerRepository;
        this.documentRepository = documentRepository;
        this.documentEntryRepository = documentEntryRepository;
        this.documentTypeRepository = documentTypeRepository;
        this.documentSubTypeRepository = documentSubTypeRepository;
        this.causeAdaptor = causeAdaptor;
    }


    public CAdminPage getCaAdminStatistics() {
        CAdminPage cAdminPage = new CAdminPage();
        List<CustomerEntity> customerEntities = this.customerRepository.findAll();
        List<DocumentTypeEntity> documentTypeEntities = this.documentTypeRepository.findAll();

        CaAdminCauseData caAdminCauseData = this.causeAdaptor.fetchCauseData();
        final LocalDateTime startDateOfThisWeek = LocalDateTime.now(Clock.systemUTC()).minusDays(7);


//        todo next release we need to complete for release of the fund
//        final DayOfWeek firstDayOfWeek = WeekFields.ISO.getFirstDayOfWeek();
//        final LocalDateTime startDateOfThisWeek = LocalDateTime.now(Clock.systemUTC()).with(TemporalAdjusters.previousOrSame(firstDayOfWeek));
//        System.out.println("Start Date Of This Week" + startDateOfThisWeek);

        cAdminPage.setActiveMember(customerEntities.stream().filter(customerEntity -> customerEntity.getIsDeposited() && customerEntity.getType().equals(Customer.UserType.PERSON.name())).count());
        cAdminPage.setNoOfMember(customerEntities.stream().filter(customerEntity -> customerEntity.getType().equals(Customer.UserType.PERSON.name())).count());
        cAdminPage.setNoOfMemberThisWeek(customerEntities.stream().filter(customerEntity -> customerEntity.getType().equals(Customer.UserType.PERSON.name()) && customerEntity.getCreatedOn().isAfter(startDateOfThisWeek)).count());
        cAdminPage.setMemberPerMonth(CadminMapper.map(customerEntities, Customer.UserType.PERSON));
        cAdminPage.setActiveMemberPerMonth(CadminMapper.mapByStatus(customerEntities, Customer.UserType.PERSON, true));
        cAdminPage.setInactiveMemberPerMonth(CadminMapper.mapByStatus(customerEntities, Customer.UserType.PERSON, false));


        cAdminPage.setActiveNGO(customerEntities.stream().filter(customerEntity -> customerEntity.getIsDeposited() && customerEntity.getType().equals(Customer.UserType.BUSINESS.name())).count());
        cAdminPage.setNoOfNGO(customerEntities.stream().filter(customerEntity -> customerEntity.getType().equals(Customer.UserType.BUSINESS.name())).count());
        cAdminPage.setNoOfNGOThisWeek(customerEntities.stream().filter(customerEntity -> customerEntity.getType().equals(Customer.UserType.BUSINESS.name()) && customerEntity.getCreatedOn().isAfter(startDateOfThisWeek)).count());
        cAdminPage.setNgoPerMonth(CadminMapper.map(customerEntities, Customer.UserType.BUSINESS));
        cAdminPage.setActiveNgoPerMonth(CadminMapper.mapByStatus(customerEntities, Customer.UserType.BUSINESS, true));
        cAdminPage.setInactiveNgoPerMonth(CadminMapper.mapByStatus(customerEntities, Customer.UserType.BUSINESS, false));

        List<CustomerDocument> customerDocuments = customerEntities
                .stream()
                .map(customerEntity -> this.findCustomerDocuments(customerEntity, documentTypeEntities))
                .filter(doc -> doc.getKycStatusText() != null)
                .collect(Collectors.toList());


        cAdminPage.setKycPending(customerDocuments.stream().filter(customerDocument -> customerDocument.getKycStatusText().equals(CustomerDocument.Status.PENDING.name())).count());
        cAdminPage.setKycRejected(customerDocuments.stream().filter(customerDocument -> customerDocument.getKycStatusText().equals(CustomerDocument.Status.REJECTED.name())).count());
        cAdminPage.setKycApproved(customerDocuments.stream().filter(customerDocument -> customerDocument.getKycStatusText().equals(CustomerDocument.Status.APPROVED.name())).count());
        cAdminPage.setKycNotUploaded(customerDocuments.stream().filter(customerDocument -> customerDocument.getKycStatusText().equals(CustomerDocument.Status.NOTUPLOADED.name())).count());

        cAdminPage.setNoOfCause(caAdminCauseData.getNoOfCause());
        cAdminPage.setNoOfCauseThisWeek(caAdminCauseData.getNoOfCauseThisWeek());
        cAdminPage.setActiveCause(caAdminCauseData.getActiveCause());

        cAdminPage.setCausePending(caAdminCauseData.getCausePending());
        cAdminPage.setCauseCompleted(caAdminCauseData.getCauseCompleted());

        cAdminPage.setCausePerMonth(caAdminCauseData.getCausePerMonth().stream().map(perMonthRecord -> new PerMonthRecord(perMonthRecord.getMonth(), perMonthRecord.getMonthNumber(), perMonthRecord.getNumberOfRecord())).collect(toList()));
        cAdminPage.setActiveCausePerMonth(caAdminCauseData.getActiveCausePerMonth().stream().map(perMonthRecord -> new PerMonthRecord(perMonthRecord.getMonth(), perMonthRecord.getMonthNumber(), perMonthRecord.getNumberOfRecord())).collect(toList()));
        cAdminPage.setInactiveCausePerMonth(caAdminCauseData.getInactiveCausePerMonth().stream().map(perMonthRecord -> new PerMonthRecord(perMonthRecord.getMonth(), perMonthRecord.getMonthNumber(), perMonthRecord.getNumberOfRecord())).collect(toList()));


        return cAdminPage;
    }


    public List<Customer> findCustomerByType(final String type) {
        return customerRepository
                .findAllByType(type.toUpperCase())
                .stream()
                .map(CustomerMapper::map)
                .collect(Collectors.toList());

    }

    CustomerDocument findCustomerDocuments(CustomerEntity customerEntity, List<DocumentTypeEntity> documentTypeEntities) {

        List<DocumentTypeEntity> allTypeEntities = this.documentTypeRepository.findAll();
        List<DocumentSubTypeEntity> allSubTypeRepository = this.documentSubTypeRepository.findAll();

        return this.documentRepository.findByCustomerId(customerEntity.getIdentifier()).map(documentEntity -> {
            CustomerDocument customerDocument = DocumentMapper.map(documentEntity);

            List<DocumentEntryEntity> documentEntryEntityList = this.documentEntryRepository.findByDocumentAndStatusNot(documentEntity, CustomerDocument.Status.DELETED.name());

            final Map<String, List<DocumentEntryEntity>> documentEntryEntity =
                    documentEntryEntityList
                            .stream()
                            .collect(groupingBy(DocumentEntryEntity::getType, toList()));

            List<DocumentsType> documentsType = new ArrayList<>();

            documentEntryEntity.forEach((key, documentEntryEntities) -> {
                final List<DocumentsSubType> documentsSubTypeList = documentEntryEntities.stream().map(entity -> {
                    final DocumentsSubType documentsSubType = new DocumentsSubType();
                    documentsSubType.setId(entity.getId());
                    documentsSubType.setCreated_by(entity.getCreatedBy());
                    documentsSubType.setStatus(entity.getStatus());
                    documentsSubType.setType(this.getDocumentTypeTitle(allTypeEntities, entity.getType()));
                    documentsSubType.setTypeUUID(entity.getType());
                    documentsSubType.setSubTypeUUID(entity.getSubType());
                    documentsSubType.setSubType(this.getDocumentSubTypeTitle(allSubTypeRepository, entity.getSubType()));
                    documentsSubType.setUpdatedOn(entity.getUpdatedOn().toString());
                    documentsSubType.setApprovedBy(entity.getApprovedBy());
                    documentsSubType.setRejectedBy(entity.getRejectedBy());
                    documentsSubType.setReasonForReject(entity.getReasonForReject());
                    documentsSubType.setDescription(entity.getDescription());
                    documentsSubType.setCreatedOn(entity.getCreatedOn().toString());
                    documentsSubType.setDocRef(entity.getDocRef());
                    return documentsSubType;
                }).collect(toList());

                final DocumentsType type = new DocumentsType();
                DocumentMapper.setDocumentTypeStatus(documentEntryEntities, type);
                type.setTitle(this.getDocumentTypeTitle(allTypeEntities, key));
                type.setDocumentsSubType(documentsSubTypeList);
                type.setUserType(customerEntity.getType());
                type.setUuid(key);
                type.setActive(documentsSubTypeList.stream().anyMatch(dcoSubType -> dcoSubType.getStatus().equals(CustomerDocument.Status.APPROVED.name())));
                documentsType.add(type);
            });
            customerDocument.setDocumentsTypes(documentsType);

            //receive the documents master, all the types of documents per type in list
            Set<String> doc_master = documentTypeEntities
                    .stream()
                    .filter(documentTypeEntity -> documentTypeEntity.getUserType().equals(customerEntity.getType()))
                    .map(DocumentTypeEntity::getUuid)
                    .collect(Collectors.toSet());

            final boolean isDocAvailable = documentEntryEntity.keySet().equals(doc_master);


//            check if each doc type has at least a approved document
            if (documentsType.stream().allMatch(type -> type.getStatus().equals(CustomerDocument.Status.APPROVED.name()))) {
//                get approved doc list sort by approval data, the recent approved doc at first
//                then get the latest approval doc date
                Optional<DocumentEntryEntity> entryEntity = documentEntryEntityList.stream().filter(doc -> doc.getStatus().equals(CustomerDocument.Status.APPROVED.name()))
                        .max(Comparator.comparing(DocumentEntryEntity::getApprovedOn));


                if (entryEntity.isPresent()) {
                    entryEntity.ifPresent(ent -> {
                        if (isDocAvailable) {
                            customerDocument.setKycStatusText(CustomerDocument.Status.APPROVED.name());
                            customerDocument.setKycStatus(true);
                        } else {
                            Set<String> doc_available = documentEntryEntityList
                                    .stream()
                                    .map(DocumentEntryEntity::getType)
                                    .collect(Collectors.toSet());

                            Set<String> doc_different = new HashSet<>(doc_master);
                            doc_different.removeAll(doc_available);
                            if (doc_different.size() == 0) {
                                customerDocument.setKycStatusText(CustomerDocument.Status.APPROVED.name());
                                customerDocument.setKycStatus(true);

                            } else {

                                this.documentTypeRepository.findByUuidIn(doc_different)
                                        .stream()
                                        .max(Comparator.comparing(DocumentTypeEntity::getCreatedOn))
                                        .ifPresent(documentTEntity -> {
                                            if (documentTEntity.getCreatedOn().isAfter(ent.getApprovedOn())) {
                                                customerDocument.setKycStatusText(CustomerDocument.Status.APPROVED.name());
                                                customerDocument.setKycStatus(true);
                                            } else {
                                                customerDocument.setKycStatusText(CustomerDocument.Status.PENDING.name());
                                                customerDocument.setKycStatus(false);
                                            }
                                        });
                            }
                        }
                    });
                } else {
                    customerDocument.setKycStatusText(CustomerDocument.Status.NOTUPLOADED.name());
                    customerDocument.setKycStatus(false);
                }

            } else {
                if (isDocAvailable) {
                    if (documentsType.stream().noneMatch(type -> type.getStatus().equals(CustomerDocument.Status.APPROVED.name()))
                            && documentsType.stream().anyMatch(type -> type.getStatus().equals(CustomerDocument.Status.REJECTED.name()))) {
                        customerDocument.setKycStatusText(CustomerDocument.Status.REJECTED.name());
                        customerDocument.setKycStatus(false);
                    } else {
                        customerDocument.setKycStatusText(CustomerDocument.Status.PENDING.name());
                        customerDocument.setKycStatus(false);
                    }
                } else {
                    customerDocument.setKycStatus(false);
                    customerDocument.setKycStatusText(CustomerDocument.Status.NOTUPLOADED.name());
                }
            }

//            if (isDocAvailable) {
//                if (documentsType.stream().allMatch(type -> type.getStatus().equals(CustomerDocument.Status.APPROVED.name()))) {
//                    customerDocument.setKycStatusText(CustomerDocument.Status.APPROVED.name());
//                    customerDocument.setKycStatus(true);
//
//                } else if (documentsType.stream().noneMatch(type -> type.getStatus().equals(CustomerDocument.Status.APPROVED.name()))
//                        && documentsType.stream().anyMatch(type -> type.getStatus().equals(CustomerDocument.Status.REJECTED.name()))) {
//                    customerDocument.setKycStatusText(CustomerDocument.Status.REJECTED.name());
//                    customerDocument.setKycStatus(false);
//                } else {
//                    customerDocument.setKycStatusText(CustomerDocument.Status.PENDING.name());
//                    customerDocument.setKycStatus(false);
//                }
//            } else {
//                customerDocument.setKycStatus(false);
//                customerDocument.setKycStatusText(CustomerDocument.Status.NOTUPLOADED.name());
//            }

            return customerDocument;

        }).orElseGet(() -> {
            CustomerDocument customerDocument = new CustomerDocument();
            customerDocument.setKycStatus(false);
            customerDocument.setKycStatusText(CustomerDocument.Status.NOTUPLOADED.name());
            return customerDocument;

        });

//        if (documentEntity.isPresent()) {
////                start document entity
//
//
//        } else {
//
//
//        }
//        return customerDocument;
    }


    private String getDocumentTypeTitle(List<DocumentTypeEntity> documentTypeEntities, final String uuid) {
        return documentTypeEntities
                .stream()
                .filter(typeEntity -> typeEntity.getUuid().equals(uuid))
                .findFirst()
                .map(DocumentTypeEntity::getTitle)
                .orElse(null);

    }

    private String getDocumentSubTypeTitle(List<DocumentSubTypeEntity> documentSubTypeEntities, final String uuid) {
        return documentSubTypeEntities
                .stream()
                .filter(subTypeEntity -> subTypeEntity.getUuid().equals(uuid)).findFirst()
                .map(DocumentSubTypeEntity::getTitle)
                .orElse(null);
    }

}
