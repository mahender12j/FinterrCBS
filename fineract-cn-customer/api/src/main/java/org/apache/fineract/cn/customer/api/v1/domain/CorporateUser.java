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
package org.apache.fineract.cn.customer.api.v1.domain;

import org.hibernate.validator.constraints.NotBlank;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

public final class CorporateUser {


    private Long id;
    @NotBlank
    private String identifier;
    private Customer.UserType type;

    @NotNull
    private String companyName;
    @NotBlank
    private String givenName;
    @NotBlank
    private String surname;
    private String middleName;

    @Valid
    private List<ContactDetail> contactDetails;

    @NotNull
    private String designation;

    @Valid
    private Address address;

    @NotNull
    private String ercTokenAddress;
    private String typeOfCompany;
    private int numberOfEmployees;

    @NotNull
    private String refferalCodeIdentifier;
    private String refferalUserIdentifier;

    private SocialMatrix socialMatrix;
    private CustomerDocument corporateDocument;
    @NotNull
    private String refAccountNumber;
    private Boolean deposited;
    private Customer.KycStatus kycStatus;
    @NotNull
    private String accountNumbers;
    private String principalBusinessActivities;


    @NotNull
    private Customer.RegistrationType registrationType;


    public CorporateUser() {
        super();
    }

    public CorporateUser(Long id, String identifier, Customer.UserType type, String givenName, String surname, String designation) {
        this.id = id;
        this.identifier = identifier;
        this.type = type;
        this.givenName = givenName;
        this.surname = surname;
        this.companyName = companyName;
        this.designation = designation;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getType() {
        return type.name();
    }

    public String getRegistrationType() {
        return registrationType.name();
    }

    public void setRegistrationType(String registrationType) {
        this.registrationType = Customer.RegistrationType.valueOf(registrationType);
    }

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public List<ContactDetail> getContactDetails() {
        return contactDetails;
    }

    public void setContactDetails(List<ContactDetail> contactDetails) {
        this.contactDetails = contactDetails;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getErcTokenAddress() {
        return ercTokenAddress;
    }

    public void setErcTokenAddress(String ercTokenAddress) {
        this.ercTokenAddress = ercTokenAddress;
    }

    public String getTypeOfCompany() {
        return typeOfCompany;
    }

    public void setTypeOfCompany(String typeOfCompany) {
        this.typeOfCompany = typeOfCompany;
    }

    public int getNumberOfEmployees() {
        return numberOfEmployees;
    }

    public void setNumberOfEmployees(int numberOfEmployees) {
        this.numberOfEmployees = numberOfEmployees;
    }

    public String getRefferalCodeIdentifier() {
        return refferalCodeIdentifier;
    }

    public void setRefferalCodeIdentifier(String refferalCodeIdentifier) {
        this.refferalCodeIdentifier = refferalCodeIdentifier;
    }

    public String getRefferalUserIdentifier() {
        return refferalUserIdentifier;
    }

    public void setRefferalUserIdentifier(String refferalUserIdentifier) {
        this.refferalUserIdentifier = refferalUserIdentifier;
    }

    public SocialMatrix getSocialMatrix() {
        return socialMatrix;
    }

    public void setSocialMatrix(SocialMatrix socialMatrix) {
        this.socialMatrix = socialMatrix;
    }

    public CustomerDocument getCorporateDocument() {
        return corporateDocument;
    }

    public void setCorporateDocument(CustomerDocument corporateDocument) {
        this.corporateDocument = corporateDocument;
    }

    public Boolean getDeposited() {
        return deposited;
    }

    public void setDeposited(Boolean deposited) {
        this.deposited = deposited;
    }

    public String getKycStatus() {
        return kycStatus.name();
    }

    public void setKycStatus(String kycStatus) {
        this.kycStatus = Customer.KycStatus.valueOf(kycStatus);
    }

    public String getAccountNumbers() {
        return accountNumbers;
    }

    public void setAccountNumbers(String accountNumbers) {
        this.accountNumbers = accountNumbers;
    }

    public String getPrincipalBusinessActivities() {
        return principalBusinessActivities;
    }

    public void setPrincipalBusinessActivities(String principalBusinessActivities) {
        this.principalBusinessActivities = principalBusinessActivities;
    }

    public String getRefAccountNumber() {
        return refAccountNumber;
    }

    public void setRefAccountNumber(String refAccountNumber) {
        this.refAccountNumber = refAccountNumber;
    }

    @Override
    public String toString() {
        return "CorporateUser{" +
                "id=" + id +
                ", identifier='" + identifier + '\'' +
                ", type=" + type +
                ", registrationType=" + registrationType +
                ", givenName='" + givenName + '\'' +
                ", surname='" + surname + '\'' +
                ", middleName='" + middleName + '\'' +
                ", companyName='" + companyName + '\'' +
                ", address=" + address +
                ", contactDetails=" + contactDetails +
                ", designation='" + designation + '\'' +
                '}';
    }
}

