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

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.fineract.cn.customer.catalog.api.v1.domain.Value;
import org.apache.fineract.cn.lang.DateOfBirth;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

public final class Customer {

    public enum UserType {
        PERSON,
        BUSINESS,
        CADMIN,
        SADMIN,
        CORPORATE,
        TRUST,
        FUNDMANAGER
    }

    public enum RegistrationType {
        FACEBOOK,
        GOOGLEPLUS,
        LINKEDIN,
        TWITTER,
        EMAIL
    }

    public enum UserState {
        PENDING,
        ACTIVE,
        LOCKED,
        REJECTED,
        CLOSED,
        NOT_REQUIRED
    }

    public enum KycStatus {
        NOTUPLOADED,
        PENDING,
        APPROVED,
        REJECTED,
        PROCESSING,
        NOT_REQUIRED
    }

    private Long id;
    @NotBlank
    private String identifier;
    @NotNull
    private UserType type;
    @NotNull
    private RegistrationType registrationType;
    @NotBlank
    private String givenName;
    @NotBlank
    private String surname;
    private String middleName;
    private String gender;
    private DateOfBirth dateOfBirth;
    private Boolean ngoProfileExist;
    private String referenceCustomer;
    private Address address;
    @Valid
    private List<ContactDetail> contactDetails;
    private UserState currentState;
    private String applicationDate;
    private List<Value> customValues;
    private String createdBy;
    private String createdOn;
    private String lastModifiedBy;
    private String lastModifiedOn;
    @NotBlank
    private String refferalCodeIdentifier;
    private String refferalUserIdentifier;
    private String ethAddress;
    private Boolean deposited;
    private String activationDate;

    private KycStatus kycStatus;
    private boolean kycVerified;
    private String accountNumbers;
    private Double avgMonthlyIncome;
    private String ngoName;
    private String designation;
    private String ngoRegistrationNumber;
    private String dateOfRegistration;
    private String refAccountNumber;
    private Double refferalBalance;
    private SocialMatrix socialMatrix;
    private CustomerDocument customerDocument;


    //    used for front-end support
    @JsonProperty(value = "isProfileComplete")
    private boolean profileComplete;
    @JsonProperty(value = "isEmailVerified")
    private boolean emailVerified;
    @JsonProperty(value = "isMobileVerified")
    private boolean mobileVerified;
    private String verifiedMobile;
    private String verifiedEmail;
    private String portraitUrl;

    public Customer() {
        super();
    }

    public String getIdentifier() {
        return this.identifier;
    }

    public void setIdentifier(final String identifier) {
        this.identifier = identifier;
    }

    public String getType() {
        return this.type.name();
    }

    public void setType(final String type) {
        this.type = UserType.valueOf(type);
    }

    public String getRegistrationType() {
        return this.registrationType.name();
    }

    public void setRegistrationType(final String registrationType) {
        this.registrationType = RegistrationType.valueOf(registrationType);
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

    public DateOfBirth getDateOfBirth() {
        return this.dateOfBirth;
    }

    public void setDateOfBirth(final DateOfBirth dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getReferenceCustomer() {
        return this.referenceCustomer;
    }

    public void setReferenceCustomer(final String referenceCustomer) {
        this.referenceCustomer = referenceCustomer;
    }

    public Address getAddress() {
        return this.address;
    }

    public void setAddress(final Address address) {
        this.address = address;
    }

    public List<ContactDetail> getContactDetails() {
        return this.contactDetails;
    }

    public void setContactDetails(final List<ContactDetail> contactDetails) {
        this.contactDetails = contactDetails;
    }

    public String getCurrentState() {
        return this.currentState != null ? this.currentState.name() : null;
    }

    public void setCurrentState(final String currentState) {
        this.currentState = UserState.valueOf(currentState);
    }

    public String getCreatedBy() {
        return this.createdBy;
    }

    public void setCreatedBy(final String createdBy) {
        this.createdBy = createdBy;
    }

    public String getCreatedOn() {
        return this.createdOn;
    }

    public void setCreatedOn(final String createdOn) {
        this.createdOn = createdOn;
    }

    public String getLastModifiedBy() {
        return this.lastModifiedBy;
    }

    public void setLastModifiedBy(final String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public String getLastModifiedOn() {
        return this.lastModifiedOn;
    }

    public void setLastModifiedOn(final String lastModifiedOn) {
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

    public String getKycStatus() {
        return this.kycStatus != null ? this.kycStatus.name() : null;
    }

    public void setKycStatus(final String kycStatus) {
        this.kycStatus = KycStatus.valueOf(kycStatus);
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

    public String getRefferalUserIdentifier() {
        return refferalUserIdentifier;
    }

    public void setRefferalUserIdentifier(String refferalUserIdentifier) {
        this.refferalUserIdentifier = refferalUserIdentifier;
    }

    public String getDateOfRegistration() {
        return dateOfRegistration;
    }

    public void setDateOfRegistration(String dateOfRegistration) {
        this.dateOfRegistration = dateOfRegistration;
    }

    public String getRefAccountNumber() {
        return refAccountNumber;
    }

    public void setRefAccountNumber(String refAccountNumber) {
        this.refAccountNumber = refAccountNumber;
    }

    public Double getRefferalBalance() {
        return this.refferalBalance;
    }

    public void setRefferalBalance(final Double refferalBalance) {
        this.refferalBalance = refferalBalance;
    }

    public SocialMatrix getSocialMatrix() {
        return socialMatrix;
    }

    public void setSocialMatrix(SocialMatrix socialMatrix) {
        this.socialMatrix = socialMatrix;
    }

    public CustomerDocument getCustomerDocument() {
        return customerDocument;
    }

    public void setCustomerDocument(CustomerDocument customerDocument) {
        this.customerDocument = customerDocument;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setType(UserType userType) {
        this.type = userType;
    }

    public void setRegistrationType(RegistrationType registrationType) {
        this.registrationType = registrationType;
    }

    public void setCurrentState(UserState currentUserState) {
        this.currentState = currentUserState;
    }

    public Boolean getDeposited() {
        return deposited;
    }

    public void setDeposited(Boolean deposited) {
        this.deposited = deposited;
    }

    public void setKycStatus(KycStatus kycStatus) {
        this.kycStatus = kycStatus;
    }

    public boolean isKycVerified() {
        return kycVerified;
    }

    public void setKycVerified(boolean kycVerified) {
        this.kycVerified = kycVerified;
    }

    public Boolean getNgoProfileExist() {
        return ngoProfileExist;
    }

    public void setNgoProfileExist(Boolean ngoProfileExist) {
        this.ngoProfileExist = ngoProfileExist;
    }

    public String getApplicationDate() {
        return applicationDate;
    }

    public void setApplicationDate(String applicationDate) {
        this.applicationDate = applicationDate;
    }

    public List<Value> getCustomValues() {
        return customValues;
    }

    public void setCustomValues(List<Value> customValues) {
        this.customValues = customValues;
    }

    public String getActivationDate() {
        return activationDate;
    }

    public void setActivationDate(String activationDate) {
        this.activationDate = activationDate;
    }

    //    for the user status


    public boolean isProfileComplete() {
        return profileComplete;
    }

    public void setProfileComplete(boolean profileComplete) {
        this.profileComplete = profileComplete;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public boolean isMobileVerified() {
        return mobileVerified;
    }

    public void setMobileVerified(boolean mobileVerified) {
        this.mobileVerified = mobileVerified;
    }

    public String getVerifiedMobile() {
        return verifiedMobile;
    }

    public void setVerifiedMobile(String verifiedMobile) {
        this.verifiedMobile = verifiedMobile;
    }

    public String getVerifiedEmail() {
        return verifiedEmail;
    }

    public void setVerifiedEmail(String verifiedEmail) {
        this.verifiedEmail = verifiedEmail;
    }

    public String getPortraitUrl() {
        return portraitUrl;
    }

    public void setPortraitUrl(String portraitUrl) {
        this.portraitUrl = portraitUrl;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id=" + id +
                ", identifier='" + identifier + '\'' +
                ", type=" + type +
                ", registrationType=" + registrationType +
                ", givenName='" + givenName + '\'' +
                ", surname='" + surname + '\'' +
                ", middleName='" + middleName + '\'' +
                ", gender='" + gender + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                ", ngoProfileExist=" + ngoProfileExist +
                ", referenceCustomer='" + referenceCustomer + '\'' +
                ", address=" + address +
                ", contactDetails=" + contactDetails +
                ", currentState=" + currentState +
                ", createdBy='" + createdBy + '\'' +
                ", createdOn='" + createdOn + '\'' +
                ", lastModifiedBy='" + lastModifiedBy + '\'' +
                ", lastModifiedOn='" + lastModifiedOn + '\'' +
                ", refferalCodeIdentifier='" + refferalCodeIdentifier + '\'' +
                ", refferalUserIdentifier='" + refferalUserIdentifier + '\'' +
                ", ethAddress='" + ethAddress + '\'' +
                ", deposited=" + deposited +
                ", kycStatus=" + kycStatus +
                ", accountNumbers='" + accountNumbers + '\'' +
                ", avgMonthlyIncome=" + avgMonthlyIncome +
                ", ngoName='" + ngoName + '\'' +
                ", designation='" + designation + '\'' +
                ", ngoRegistrationNumber='" + ngoRegistrationNumber + '\'' +
                ", dateOfRegistration='" + dateOfRegistration + '\'' +
                ", refAccountNumber='" + refAccountNumber + '\'' +
                ", refferalBalance=" + refferalBalance +
                ", socialMatrix=" + socialMatrix +
                ", customerDocument=" + customerDocument +
                '}';
    }
}

