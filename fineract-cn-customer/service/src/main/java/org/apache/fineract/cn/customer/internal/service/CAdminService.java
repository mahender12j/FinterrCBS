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

import org.apache.fineract.cn.customer.api.v1.domain.CAdminPage;
import org.apache.fineract.cn.customer.api.v1.domain.Customer;
import org.apache.fineract.cn.customer.internal.repository.CustomerEntity;
import org.apache.fineract.cn.customer.internal.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CAdminService {

    private final CustomerRepository customerRepository;

    @Autowired
    public CAdminService(final CustomerRepository customerRepository) {
        super();
        this.customerRepository = customerRepository;
    }


    public CAdminPage getCaAdminStatistics() {
        CAdminPage cAdminPage = new CAdminPage();
        List<CustomerEntity> customerEntities = this.customerRepository.findAll();

//        getIsDeposited is checked for active since deposit is only possible after kyc is approved

        cAdminPage.setActiveMember(customerEntities.stream().filter(customerEntity -> customerEntity.getIsDeposited() && customerEntity.getType().equals(Customer.Type.PERSON.name())).count());
        cAdminPage.setNoOfMember(customerEntities.stream().filter(customerEntity -> customerEntity.getType().equals(Customer.Type.PERSON.name())).count());

        cAdminPage.setActiveNGO(customerEntities.stream().filter(customerEntity -> customerEntity.getIsDeposited() && customerEntity.getType().equals(Customer.Type.BUSINESS.name())).count());
        cAdminPage.setNoOfNGO(customerEntities.stream().filter(customerEntity -> customerEntity.getType().equals(Customer.Type.BUSINESS.name())).count());


        return cAdminPage;
    }
}
