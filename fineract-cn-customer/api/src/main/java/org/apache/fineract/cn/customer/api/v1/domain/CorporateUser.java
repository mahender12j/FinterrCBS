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

import org.apache.fineract.cn.customer.catalog.api.v1.domain.Value;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

public class CorporateUser {


    private Long id;
    @NotBlank
    private String identifier;
    private Customer.UserType type;

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

    private String ethAddress;

    @NotNull
    private String refferalCodeIdentifier;
    private String refferalUserIdentifier;

    @NotNull
    private String refAccountNumber;

    @NotNull
    private String accountNumbers;
    @Valid
    private List<Value> customValues; //custom values - company name

    CorporateUser() {
        super();
    }

    public CorporateUser(Long id, String identifier, String type, String givenName, String surname, String designation) {
        this.id = id;
        this.identifier = identifier;
        this.type = Customer.UserType.valueOf(type);
        this.givenName = givenName;
        this.surname = surname;
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
        return type == null ? null : type.name();
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

    public String getEthAddress() {
        return ethAddress;
    }

    public void setEthAddress(String ethAddress) {
        this.ethAddress = ethAddress;
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

    public String getAccountNumbers() {
        return accountNumbers;
    }

    public void setAccountNumbers(String accountNumbers) {
        this.accountNumbers = accountNumbers;
    }

    public String getRefAccountNumber() {
        return refAccountNumber;
    }

    public void setRefAccountNumber(String refAccountNumber) {
        this.refAccountNumber = refAccountNumber;
    }

    public List<Value> getCustomValues() {
        return customValues;
    }

    public void setCustomValues(List<Value> customValues) {
        this.customValues = customValues;
    }

    @Override
    public String toString() {
        return "CorporateUser{" +
                "id=" + id +
                ", identifier='" + identifier + '\'' +
                ", type=" + type +
                ", givenName='" + givenName + '\'' +
                ", surname='" + surname + '\'' +
                ", middleName='" + middleName + '\'' +
                ", address=" + address +
                ", contactDetails=" + contactDetails +
                ", designation='" + designation + '\'' +
                '}';
    }
}

