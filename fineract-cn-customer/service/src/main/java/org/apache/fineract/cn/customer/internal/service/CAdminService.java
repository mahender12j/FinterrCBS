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
import org.apache.fineract.cn.customer.internal.mapper.CadminMapper;
import org.apache.fineract.cn.customer.internal.mapper.DocumentMapper;
import org.apache.fineract.cn.customer.internal.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
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

    @Autowired
    public CAdminService(final CustomerRepository customerRepository,
                         final DocumentRepository documentRepository,
                         final DocumentEntryRepository documentEntryRepository,
                         final DocumentTypeRepository documentTypeRepository,
                         final DocumentSubTypeRepository documentSubTypeRepository) {
        super();
        this.customerRepository = customerRepository;
        this.documentRepository = documentRepository;
        this.documentEntryRepository = documentEntryRepository;
        this.documentTypeRepository = documentTypeRepository;
        this.documentSubTypeRepository = documentSubTypeRepository;
    }


    public CAdminPage getCaAdminStatistics() {
        CAdminPage cAdminPage = new CAdminPage();
        List<CustomerEntity> customerEntities = this.customerRepository.findAll();
        List<DocumentTypeEntity> documentTypeEntities = this.documentTypeRepository.findAll();
        List<DocumentSubTypeEntity> documentSubTypeEntities = this.documentSubTypeRepository.findAll();

        final DayOfWeek firstDayOfWeek = WeekFields.ISO.getFirstDayOfWeek();
        final LocalDateTime startDateOfThisWeek = LocalDateTime.now(Clock.systemUTC()).with(TemporalAdjusters.previousOrSame(firstDayOfWeek));


        cAdminPage.setActiveMember(customerEntities.stream().filter(customerEntity -> customerEntity.getIsDeposited() && customerEntity.getType().equals(Customer.Type.PERSON.name())).count());
        cAdminPage.setNoOfMember(customerEntities.stream().filter(customerEntity -> customerEntity.getType().equals(Customer.Type.PERSON.name())).count());
        cAdminPage.setNoOfMemberThisWeek(customerEntities.stream().filter(customerEntity -> customerEntity.getType().equals(Customer.Type.PERSON.name()) && customerEntity.getCreatedOn().isAfter(startDateOfThisWeek)).count());
        cAdminPage.setMemberPerMonth(CadminMapper.map(customerEntities, Customer.Type.PERSON));
        cAdminPage.setActiveMemberPerMonth(CadminMapper.mapByStatus(customerEntities, Customer.Type.PERSON, true));
        cAdminPage.setInactiveMemberPerMonth(CadminMapper.mapByStatus(customerEntities, Customer.Type.PERSON, false));


        cAdminPage.setActiveNGO(customerEntities.stream().filter(customerEntity -> customerEntity.getIsDeposited() && customerEntity.getType().equals(Customer.Type.BUSINESS.name())).count());
        cAdminPage.setNoOfNGO(customerEntities.stream().filter(customerEntity -> customerEntity.getType().equals(Customer.Type.BUSINESS.name())).count());
        cAdminPage.setNoOfNGOThisWeek(customerEntities.stream().filter(customerEntity -> customerEntity.getType().equals(Customer.Type.BUSINESS.name()) && customerEntity.getCreatedOn().isAfter(startDateOfThisWeek)).count());
        cAdminPage.setNgoPerMonth(CadminMapper.map(customerEntities, Customer.Type.BUSINESS));
        cAdminPage.setActiveNgoPerMonth(CadminMapper.mapByStatus(customerEntities, Customer.Type.BUSINESS, true));
        cAdminPage.setInactiveNgoPerMonth(CadminMapper.mapByStatus(customerEntities, Customer.Type.BUSINESS, false));

        List<CustomerDocument> customerDocuments = customerEntities.stream().map(customerEntity -> this.findCustomerDocuments(customerEntity, documentTypeEntities, documentSubTypeEntities)).collect(Collectors.toList());

        cAdminPage.setKycPending(customerDocuments.stream().filter(customerDocument -> customerDocument.getKycStatusText().equals(CustomerDocument.Status.PENDING.name())).count());
        cAdminPage.setKycRejected(customerDocuments.stream().filter(customerDocument -> customerDocument.getKycStatusText().equals(CustomerDocument.Status.REJECTED.name())).count());
        cAdminPage.setKycApproved(customerDocuments.stream().filter(customerDocument -> customerDocument.getKycStatusText().equals(CustomerDocument.Status.APPROVED.name())).count());
        cAdminPage.setKycNotUploaded(customerDocuments.stream().filter(customerDocument -> customerDocument.getKycStatusText().equals(CustomerDocument.Status.NOTUPLOADED.name())).count());


        return cAdminPage;
    }


    public CustomerDocument findCustomerDocuments(CustomerEntity customerEntity, List<DocumentTypeEntity> documentTypeEntities, List<DocumentSubTypeEntity> documentSubTypeEntities) {

        List<CustomerDocument> customerDocuments = new ArrayList<>();
        final Optional<DocumentEntity> documentEntity = this.documentRepository.findByCustomerId(customerEntity.getIdentifier());
        CustomerDocument customerDocument = new CustomerDocument();
        List<DocumentsType> documentsType = new ArrayList<>();

        if (documentEntity.isPresent()) {
//                start document entity
            customerDocument = DocumentMapper.map(documentEntity.get());
            final Map<String, List<DocumentEntryEntity>> documentEntryEntity =
                    this.documentEntryRepository.findByDocumentAndStatusNot(documentEntity.get(), CustomerDocument.Status.DELETED.name())
                            .stream()
                            .collect(groupingBy(DocumentEntryEntity::getType, toList()));

            documentEntryEntity.forEach((key, documentEntryEntities) -> {
                final List<DocumentsSubType> documentsSubTypeList = documentEntryEntities.stream().map(entity -> {
                    final DocumentsSubType documentsSubType = new DocumentsSubType();
                    documentsSubType.setId(entity.getId());
                    documentsSubType.setCreated_by(entity.getCreatedBy());
                    documentsSubType.setStatus(entity.getStatus());
                    documentsSubType.setType(this.getDocumentTypeTitle(documentTypeEntities, entity.getType()));
                    documentsSubType.setSubType(this.getDocumentSubTypeTitle(documentSubTypeEntities, entity.getSubType()));
                    documentsSubType.setUpdatedOn(entity.getUpdatedOn().toString());
                    documentsSubType.setApprovedBy(entity.getApprovedBy());
                    documentsSubType.setRejectedBy(entity.getRejectedBy());
                    documentsSubType.setReasonForReject(entity.getReasonForReject());
                    documentsSubType.setDescription(entity.getDescription());
                    documentsSubType.setCreatedOn(entity.getCreatedOn().toString());
                    documentsSubType.setDocRef(entity.getDocRef());
                    return documentsSubType;
                }).collect(Collectors.toList());

                final DocumentsType type = new DocumentsType();
                DocumentMapper.setDocumentTypeStatus(documentEntryEntities, type);
                type.setType(this.getDocumentTypeTitle(documentTypeEntities, key));
                type.setDocumentsSubType(documentsSubTypeList);
                documentsType.add(type);
            });
            customerDocument.setDocumentsTypes(documentsType);

            Set<String> doc_master = documentTypeEntities.stream().filter(documentTypeEntity -> documentTypeEntity.getUserType().equals(customerEntity.getType()))
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


        } else {
            customerDocument.setKycStatus(false);
            customerDocument.setKycStatusText(CustomerDocument.Status.NOTUPLOADED.name());

        }
        return customerDocument;
    }


    private String getDocumentTypeTitle(List<DocumentTypeEntity> documentTypeEntities, final String uuid) {

        final Optional<DocumentTypeEntity> documentTypeEntity = documentTypeEntities.stream().filter(typeEntity -> typeEntity.getUuid().equals(uuid)).findFirst();

        if (documentTypeEntity.isPresent()) {
            return documentTypeEntity.get().getTitle();
        } else {
            return "NOT-FOUND";
        }
    }

    private String getDocumentSubTypeTitle(List<DocumentSubTypeEntity> documentSubTypeEntities, final String uuid) {
        final Optional<DocumentSubTypeEntity> documentSubTypeEntity = documentSubTypeEntities.stream().filter(subTypeEntity -> subTypeEntity.getUuid().equals(uuid)).findFirst();
        if (documentSubTypeEntity.isPresent()) {
            return documentSubTypeEntity.get().getTitle();
        } else {
            return "NOT-FOUND";
        }
    }

}
