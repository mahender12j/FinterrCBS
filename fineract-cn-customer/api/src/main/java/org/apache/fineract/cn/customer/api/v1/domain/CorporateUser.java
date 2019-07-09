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
    @NotNull
    private Customer.UserType type;
    @NotNull
    private Customer.RegistrationType registrationType;
    @NotBlank
    private String givenName;
    @NotBlank
    private String surname;
    private String middleName;
    @NotNull
    private String companyName;
    @NotNull
    private String companyRepresentativeName;
    @Valid
    private Address address;
    @Valid
    private List<ContactDetail> contactDetails;
    @NotNull
    private String designation;

    public CorporateUser() {
        super();
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

    public Customer.UserType getType() {
        return type;
    }

    public void setType(Customer.UserType type) {
        this.type = type;
    }

    public Customer.RegistrationType getRegistrationType() {
        return registrationType;
    }

    public void setRegistrationType(Customer.RegistrationType registrationType) {
        this.registrationType = registrationType;
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

    public String getCompanyRepresentativeName() {
        return companyRepresentativeName;
    }

    public void setCompanyRepresentativeName(String companyRepresentativeName) {
        this.companyRepresentativeName = companyRepresentativeName;
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
                ", companyRepresentativeName='" + companyRepresentativeName + '\'' +
                ", address=" + address +
                ", contactDetails=" + contactDetails +
                ", designation='" + designation + '\'' +
                '}';
    }
}

