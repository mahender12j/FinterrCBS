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
package org.apache.fineract.cn.customer.internal.repository;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;

import org.apache.fineract.cn.customer.catalog.internal.repository.FieldValueEntity;
import org.apache.fineract.cn.mariadb.util.LocalDateConverter;
import org.apache.fineract.cn.mariadb.util.LocalDateTimeConverter;

@Entity
@Table(name = "maat_customers")
public class CustomerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "a_type")
    private String type;
    @Column(name = "a_registration_type")
    private String registrationType;
    @Column(name = "identifier")
    private String identifier;
    @Column(name = "given_name")
    private String givenName;
    @Column(name = "middle_name")
    private String middleName;
    @Column(name = "surname")
    private String surname;
    @Column(name = "gender")
    private String gender;
    @Column(name = "date_of_birth")
    private Date dateOfBirth;
    @Column(name = "reference_customer")
    private String referenceCustomer;
    @Column(name = "current_state")
    private String currentState;
    @Column(name = "application_date")
    @Convert(converter = LocalDateConverter.class)
    private LocalDate applicationDate;

    @OneToOne(mappedBy = "customer", cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = false)
    private AddressEntity address;

    @OneToOne(mappedBy = "customer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private DocumentEntity documentEntity;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ContactDetailEntity> contactDetail = new ArrayList<>();

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<FieldValueEntity> fieldValueEntities = new ArrayList<>();


    @Column(name = "created_by")
    private String createdBy;
    @Column(name = "created_on")
    @Convert(converter = LocalDateTimeConverter.class)
    private LocalDateTime createdOn;
    @Column(name = "last_modified_by")
    private String lastModifiedBy;
    @Column(name = "last_modified_on")
    @Convert(converter = LocalDateTimeConverter.class)
    private LocalDateTime lastModifiedOn;
    @Column(name = "refferal_code_identifier")
    private String refferalCodeIdentifier;
    @Column(name = "eth_address")
    private String ethAddress;
    @Column(name = "is_deposited")
    private Boolean isDeposited;
    @Column(name = "kyc_status")
    private String kycStatus;
    @Column(name = "account_numbers")
    private String accountNumbers;
    @Column(name = "avg_monthly_income")
    private Double avgMonthlyIncome;
    @Column(name = "ngo_name")
    private String ngoName;
    @Column(name = "designation")
    private String designation;
    @Column(name = "ngo_registration_number")
    private String ngoRegistrationNumber;
    @Column(name = "date_of_registration")
    @Convert(converter = LocalDateTimeConverter.class)
    private LocalDateTime dateOfRegistration;
    @Column(name = "ref_account_number")
    private String refAccountNumber;
    @Column(name = "portrait_url")
    private String portraitUrl;

    public CustomerEntity() {
        super();
    }

    public Long getId() {
        return this.id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getIdentifier() {
        return this.identifier;
    }

    public void setIdentifier(final String identifier) {
        this.identifier = identifier;
    }

    public String getType() {
        return this.type;
    }

    public void setType(final String type) {
        this.type = type;
    }


    public String getRegistrationType() {
        return this.registrationType;
    }

    public void setRegistrationType(final String registrationType) {
        this.registrationType = registrationType;
    }

    public String getGivenName() {
        return this.givenName;
    }

    public void setGivenName(final String givenName) {
        this.givenName = givenName;
    }

    public String getMiddleName() {
        return this.middleName;
    }

    public void setMiddleName(final String middleName) {
        this.middleName = middleName;
    }

    public String getSurname() {
        return this.surname;
    }

    public void setSurname(final String surname) {
        this.surname = surname;
    }

    public Date getDateOfBirth() {
        return this.dateOfBirth;
    }

    public void setDateOfBirth(final Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getReferenceCustomer() {
        return this.referenceCustomer;
    }

    public void setReferenceCustomer(final String referenceCustomer) {
        this.referenceCustomer = referenceCustomer;
    }

    public String getCurrentState() {
        return this.currentState;
    }

    public void setCurrentState(final String currentState) {
        this.currentState = currentState;
    }

    public LocalDate getApplicationDate() {
        return this.applicationDate;
    }

    public void setApplicationDate(final LocalDate applicationDate) {
        this.applicationDate = applicationDate;
    }

    public AddressEntity getAddress() {
        return this.address;
    }

    public void setAddress(final AddressEntity address) {
        this.address = address;
    }

    public String getCreatedBy() {
        return this.createdBy;
    }

    public void setCreatedBy(final String createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedOn() {
        return this.createdOn;
    }

    public void setCreatedOn(final LocalDateTime createdOn) {
        this.createdOn = createdOn;
    }

    public String getLastModifiedBy() {
        return this.lastModifiedBy;
    }

    public void setLastModifiedBy(final String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public LocalDateTime getLastModifiedOn() {
        return this.lastModifiedOn;
    }

    public void setLastModifiedOn(final LocalDateTime lastModifiedOn) {
        this.lastModifiedOn = lastModifiedOn;
    }

    public String getRefferalCodeIdentifier() {
        return this.refferalCodeIdentifier;
    }

    public void setRefferalCodeIdentifier(final String refferalCodeIdentifier) {
        this.refferalCodeIdentifier = refferalCodeIdentifier;
    }

    public String getEthAddress() {
        return this.ethAddress;
    }

    public void setEthAddress(final String ethAddress) {
        this.ethAddress = ethAddress;
    }

    public Boolean getIsDeposited() {
        return this.isDeposited;
    }

    public void setIsDeposited(final Boolean isDeposited) {
        this.isDeposited = isDeposited;
    }

    public String getKycStatus() {
        return this.kycStatus;
    }

    public void setKycStatus(final String kycStatus) {
        this.kycStatus = kycStatus;
    }

    public String getAccountNumbers() {
        return this.accountNumbers;
    }

    public void setAccountNumbers(final String accountNumbers) {
        this.accountNumbers = accountNumbers;
    }

    public Double getAvgMonthlyIncome() {
        return this.avgMonthlyIncome;
    }

    public void setAvgMonthlyIncome(final Double avgMonthlyIncome) {
        this.avgMonthlyIncome = avgMonthlyIncome;
    }

    public String getNgoName() {
        return ngoName;
    }

    public void setNgoName(String ngoName) {
        this.ngoName = ngoName;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getNgoRegistrationNumber() {
        return ngoRegistrationNumber;
    }

    public void setNgoRegistrationNumber(String ngoRegistrationNumber) {
        this.ngoRegistrationNumber = ngoRegistrationNumber;
    }

    public LocalDateTime getDateOfRegistration() {
        return dateOfRegistration;
    }

    public void setDateOfRegistration(LocalDateTime dateOfRegistration) {
        this.dateOfRegistration = dateOfRegistration;
    }

    public List<ContactDetailEntity> getContactDetail() {
        return contactDetail;
    }

    public void setContactDetail(List<ContactDetailEntity> contactDetail) {
        this.contactDetail = contactDetail;
    }

    public Boolean getDeposited() {
        return isDeposited;
    }

    public void setDeposited(Boolean deposited) {
        isDeposited = deposited;
    }

    public String getRefAccountNumber() {
        return refAccountNumber;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setRefAccountNumber(String refAccountNumber) {
        this.refAccountNumber = refAccountNumber;
    }

    public List<FieldValueEntity> getFieldValueEntities() {
        return fieldValueEntities;
    }

    public void setFieldValueEntities(List<FieldValueEntity> fieldValueEntities) {
        this.fieldValueEntities = fieldValueEntities;
    }

    public DocumentEntity getDocumentEntity() {
        return documentEntity;
    }

    public void setDocumentEntity(DocumentEntity documentEntity) {
        this.documentEntity = documentEntity;
    }

    public String getPortraitUrl() {
        return portraitUrl;
    }

    public void setPortraitUrl(String portraitUrl) {
        this.portraitUrl = portraitUrl;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CustomerEntity that = (CustomerEntity) o;

        return identifier.equals(that.identifier);

    }

    @Override
    public int hashCode() {
        return identifier.hashCode();
    }
}

