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
package org.apache.fineract.cn.customer.api.v1.client;

import org.apache.fineract.cn.customer.api.v1.config.CustomerFeignClientConfig;
import org.apache.fineract.cn.customer.api.v1.domain.Address;
import org.apache.fineract.cn.customer.api.v1.domain.AmlDetail;
import org.apache.fineract.cn.customer.api.v1.domain.Command;
import org.apache.fineract.cn.customer.api.v1.domain.ContactDetail;
import org.apache.fineract.cn.customer.api.v1.domain.Customer;
import org.apache.fineract.cn.customer.api.v1.domain.CustomerPage;
import org.apache.fineract.cn.customer.api.v1.domain.IdentificationCard;
import org.apache.fineract.cn.customer.api.v1.domain.IdentificationCardScan;
import org.apache.fineract.cn.customer.api.v1.domain.ProcessStep;
import org.apache.fineract.cn.customer.api.v1.domain.TaskDefinition;

import java.util.List;
import java.util.Map;
import javax.validation.constraints.Size;

import org.apache.fineract.cn.api.annotation.ThrowsException;
import org.apache.fineract.cn.api.annotation.ThrowsExceptions;
import org.apache.fineract.cn.lang.validation.constraints.ValidIdentifier;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@SuppressWarnings("unused")
@FeignClient(name = "customer-v1", path = "/customer-v1", configuration = CustomerFeignClientConfig.class)
public interface CustomerManager {

    @RequestMapping(
            value = "/customers/{customeridentifier}/documents/new",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    void createCustomer(@RequestBody final Customer customer);

    @RequestMapping(
            value = "/customers",
            method = RequestMethod.GET,
            produces = MediaType.ALL_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    CustomerPage fetchCustomers(@RequestParam(value = "term", required = false) final String term,
                                @RequestParam(value = "includeClosed", required = false) final Boolean includeClosed,
                                @RequestParam(value = "pageIndex", required = false) final Integer pageIndex,
                                @RequestParam(value = "size", required = false) final Integer size,
                                @RequestParam(value = "sortColumn", required = false) final String sortColumn,
                                @RequestParam(value = "sortDirection", required = false) final String sortDirection);

}
