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

import org.apache.fineract.cn.customer.api.v1.domain.ContactDetail;
import org.apache.fineract.cn.customer.api.v1.domain.Customer;
import org.apache.fineract.cn.customer.internal.repository.ContactDetailEntity;
import org.apache.fineract.cn.customer.internal.repository.ContactDetailRepository;
import org.apache.fineract.cn.customer.internal.repository.CustomerEntity;
import org.apache.fineract.cn.customer.internal.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CorporateService {

    private final CustomerRepository customerRepository;
    private final ContactDetailRepository contactDetailRepository;

    @Autowired
    public CorporateService(final CustomerRepository customerRepository,
                            final ContactDetailRepository contactDetailRepository) {
        super();
        this.customerRepository = customerRepository;
        this.contactDetailRepository = contactDetailRepository;
    }


    public boolean isContactDetailExist(final String userType, final String value) {
        return this.contactDetailRepository.findAllByTypeAndValueAndValidIsTrue(userType, value)
                .stream()
                .findAny().isPresent();
    }

    public boolean profileActivated(final CustomerEntity customerEntity) {

//        List<ContactDetailEntity> detailEntityList =  customerEntity.getContactDetail();
//        boolean isMobileNumberVerified =

//        check profile fields
//        check the mobile and email
//        check the is deposited

        return false;
    }
}
