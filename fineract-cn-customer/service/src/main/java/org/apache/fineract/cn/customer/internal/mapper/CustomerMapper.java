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
package org.apache.fineract.cn.customer.internal.mapper;

import org.apache.fineract.cn.api.util.UserContextHolder;
import org.apache.fineract.cn.customer.api.v1.domain.BusinessCustomer;
import org.apache.fineract.cn.customer.api.v1.domain.CorporateUser;
import org.apache.fineract.cn.customer.api.v1.domain.Customer;
import org.apache.fineract.cn.customer.api.v1.domain.UserVerification;
import org.apache.fineract.cn.customer.internal.repository.CustomerEntity;
import org.apache.fineract.cn.lang.DateConverter;
import org.apache.fineract.cn.lang.DateOfBirth;

import java.sql.Date;
import java.time.Clock;
import java.time.LocalDateTime;

public final class CustomerMapper {

    private CustomerMapper() {
        super();
    }

    public static CustomerEntity map(final Customer customer) {
        final CustomerEntity customerEntity = new CustomerEntity();
        customerEntity.setIdentifier(customer.getIdentifier());
        customerEntity.setType(customer.getType());
        customerEntity.setRegistrationType(customer.getRegistrationType());
        customerEntity.setGivenName(customer.getGivenName());
        customerEntity.setMiddleName(customer.getMiddleName());
        customerEntity.setSurname(customer.getSurname());
        customerEntity.setGender(customer.getGender());
        if (customer.getDateOfBirth() != null) {
            customerEntity.setDateOfBirth(Date.valueOf(customer.getDateOfBirth().toLocalDate()));
        }
        customerEntity.setReferenceCustomer(customer.getReferenceCustomer());
        customerEntity.setCurrentState(Customer.UserState.PENDING.name());
        if (customer.getApplicationDate() != null) {
            final String editedApplicationDate;
            if (!customer.getApplicationDate().endsWith("Z")) {
                editedApplicationDate = customer.getApplicationDate() + "Z";
            } else {
                editedApplicationDate = customer.getApplicationDate();
            }
            customerEntity.setApplicationDate(DateConverter.dateFromIsoString(editedApplicationDate));
        }
        customerEntity.setCreatedBy(UserContextHolder.checkedGetUser());
        customerEntity.setCreatedOn(LocalDateTime.now(Clock.systemUTC()));
        customerEntity.setRefferalCodeIdentifier(customer.getRefferalCodeIdentifier());
        customerEntity.setEthAddress(customer.getEthAddress());
        if (customer.getDeposited() != null) {
            customerEntity.setIsDeposited(customer.getDeposited());
        } else {
            customerEntity.setIsDeposited(Boolean.FALSE);
        }
        if (customer.getKycStatus() != null) {
            customerEntity.setKycStatus(customer.getKycStatus());
        } else {
            customerEntity.setKycStatus(Customer.KycStatus.NOTUPLOADED.name());
        }
        customerEntity.setAccountNumbers(customer.getAccountNumbers());
        if (customer.getAvgMonthlyIncome() != null) {
            customerEntity.setAvgMonthlyIncome(customer.getAvgMonthlyIncome());
        } else {
            customerEntity.setAvgMonthlyIncome(0.0);
        }

        customerEntity.setNgoName(customer.getNgoName());
        customerEntity.setDesignation(customer.getDesignation());
        customerEntity.setNgoRegistrationNumber(customer.getNgoRegistrationNumber());
        if (customer.getDateOfRegistration() != null) {
            customerEntity.setDateOfRegistration(LocalDateTime.parse(customer.getDateOfRegistration()));
        }
        customerEntity.setRefAccountNumber(customer.getRefAccountNumber());
        return customerEntity;
    }

    public static Customer map(final CustomerEntity customerEntity) {
        final Customer customer = new Customer();
        customer.setId(customerEntity.getId());
        customer.setIdentifier(customerEntity.getIdentifier());
        customer.setType(customerEntity.getType());
        customer.setRegistrationType(customerEntity.getRegistrationType());
        customer.setGivenName(customerEntity.getGivenName());
        customer.setMiddleName(customerEntity.getMiddleName());
        customer.setGender(customerEntity.getGender());
        customer.setSurname(customerEntity.getSurname());
        if (customerEntity.getDateOfBirth() != null) {
            customer.setDateOfBirth(DateOfBirth.fromLocalDate(customerEntity.getDateOfBirth().toLocalDate()));
        }
        customer.setReferenceCustomer(customerEntity.getReferenceCustomer());
        customer.setCurrentState(customerEntity.getCurrentState());
        if (customerEntity.getApplicationDate() != null) {
            final String editedApplicationDate =
                    DateConverter.toIsoString(customerEntity.getApplicationDate()).substring(0, 10);
            customer.setApplicationDate(editedApplicationDate);
        }
        customer.setCreatedBy(customerEntity.getCreatedBy());
        customer.setCreatedOn(DateConverter.toIsoString(customerEntity.getCreatedOn()));

        if (customerEntity.getLastModifiedBy() != null) {
            customer.setLastModifiedBy(customerEntity.getLastModifiedBy());
            customer.setLastModifiedOn(DateConverter.toIsoString(customerEntity.getLastModifiedOn()));
        }
        customer.setRefferalCodeIdentifier(customerEntity.getRefferalCodeIdentifier());
        customer.setEthAddress(customerEntity.getEthAddress());

        if (customerEntity.getIsDeposited() != null) {
            customer.setDeposited(customerEntity.getIsDeposited());
        } else {
            customer.setDeposited(false);
        }
        if (customerEntity.getKycStatus() != null) {
            customer.setKycStatus(customerEntity.getKycStatus());
        } else {
            customer.setKycStatus("NOTUPLOADED");
        }
        customer.setAccountNumbers(customerEntity.getAccountNumbers());
        if (customerEntity.getAvgMonthlyIncome() != null) {
            customer.setAvgMonthlyIncome(customerEntity.getAvgMonthlyIncome());
        } else {
            customer.setAvgMonthlyIncome(0.0);
        }

        customer.setNgoName(customerEntity.getNgoName());
        customer.setDesignation(customerEntity.getDesignation());
        customer.setNgoRegistrationNumber(customerEntity.getNgoRegistrationNumber());
        if (customerEntity.getDateOfRegistration() != null) {
            customer.setDateOfRegistration(DateConverter.toIsoString(customerEntity.getDateOfRegistration()));
        }
        customer.setRefAccountNumber(customerEntity.getRefAccountNumber());
        return customer;
    }


    public static Customer map(Customer customer, UserVerification userVerification) {
        customer.setEmailVerified(userVerification.isEmailVerified());
        customer.setMobileVerified(userVerification.isMobileVerified());
        customer.setVerifiedMobile(userVerification.getVerifiedMobileNumber());
        customer.setVerifiedEmail(userVerification.getVerifiedEmailAddress());
        customer.setProfileComplete(userVerification.isProfileComplete());
        return customer;
    }


    public static CustomerEntity map(final CorporateUser customer) {
        final CustomerEntity customerEntity = new CustomerEntity();
        customerEntity.setIdentifier(customer.getIdentifier());
        customerEntity.setType(Customer.UserType.CORPORATE.name());
        customerEntity.setRegistrationType(Customer.RegistrationType.EMAIL.name());
        customerEntity.setGivenName(customer.getGivenName());
        customerEntity.setMiddleName(customer.getMiddleName());
        customerEntity.setSurname(customer.getSurname());
        customerEntity.setCurrentState(Customer.UserState.NOT_REQUIRED.name());
        customerEntity.setCreatedBy(UserContextHolder.checkedGetUser());
        customerEntity.setCreatedOn(LocalDateTime.now(Clock.systemUTC()));
        customerEntity.setRefferalCodeIdentifier(customer.getRefferalCodeIdentifier());
        customerEntity.setEthAddress(customer.getEthAddress());
        customerEntity.setIsDeposited(false);
        customerEntity.setAccountNumbers(customer.getAccountNumbers());
        customerEntity.setDesignation(customer.getDesignation());
        customerEntity.setRefAccountNumber(customer.getAccountNumbers());
        customerEntity.setLastModifiedBy(UserContextHolder.checkedGetUser());
        customerEntity.setLastModifiedOn(LocalDateTime.now());
        customerEntity.setRefAccountNumber(customer.getRefAccountNumber());
        customerEntity.setAccountNumbers(customer.getAccountNumbers());
        customerEntity.setKycStatus(Customer.KycStatus.NOT_REQUIRED.name());
        return customerEntity;
    }


    //    update map
    public static CustomerEntity map(final CustomerEntity customerEntity, final CorporateUser customer) {
        customerEntity.setType(Customer.UserType.CORPORATE.name());
        customerEntity.setGivenName(customer.getGivenName());
        customerEntity.setMiddleName(customer.getMiddleName());
        customerEntity.setSurname(customer.getSurname());
        customerEntity.setCreatedBy(UserContextHolder.checkedGetUser());
        customerEntity.setEthAddress(customer.getEthAddress());
        customerEntity.setDesignation(customer.getDesignation());
        customerEntity.setLastModifiedBy(UserContextHolder.checkedGetUser());
        customerEntity.setLastModifiedOn(LocalDateTime.now());
        return customerEntity;
    }


    public static BusinessCustomer mapBusinessCustomer(final CustomerEntity customerEntity) {
        final BusinessCustomer businessCustomer = new BusinessCustomer();
        businessCustomer.setIdentifier(customerEntity.getIdentifier());
        businessCustomer.setDateOfBirth(customerEntity.getDateOfBirth().toString());
        businessCustomer.setGivenName(customerEntity.getGivenName());
        businessCustomer.setMiddleName(customerEntity.getMiddleName());
        businessCustomer.setSurname(customerEntity.getSurname());
        return businessCustomer;
    }
}
