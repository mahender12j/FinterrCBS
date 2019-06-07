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
package org.apache.fineract.cn.customer.rest.controller;

import org.apache.fineract.cn.anubis.annotation.AcceptedTokenType;
import org.apache.fineract.cn.anubis.annotation.Permittable;
import org.apache.fineract.cn.command.gateway.CommandGateway;
import org.apache.fineract.cn.customer.PermittableGroupIds;
import org.apache.fineract.cn.customer.api.v1.domain.CAdminPage;
import org.apache.fineract.cn.customer.internal.service.CAdminService;
import org.apache.fineract.cn.customer.internal.service.CustomerService;
import org.apache.fineract.cn.lang.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
public class CAdminRestController {


    private final CommandGateway commandGateway;
    private final CAdminService cAdminService;
    private final CustomerService customerService;

    @Autowired
    public CAdminRestController(final CommandGateway commandGateway,
                                final CAdminService cAdminService,
                                final CustomerService customerService) {
        super();
        this.commandGateway = commandGateway;
        this.cAdminService = cAdminService;
        this.customerService = customerService;
    }

    //    GET NGO statistics

    @Permittable(value = AcceptedTokenType.TENANT,
            groupId = PermittableGroupIds.CADMIN)
    @RequestMapping(
            value = "/cadmin/{identifier}/statistics",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.ALL_VALUE
    )
    public
    @ResponseBody
    ResponseEntity<CAdminPage> getCadminStatistics(@PathVariable("identifier") final String identifier) {
        this.throwIfCustomerNotExists(identifier);
        return ResponseEntity.ok(this.cAdminService.getCaAdminStatistics());
    }


    private void throwIfCustomerNotExists(final String identifier) {
        if (!this.customerService.customerExists(identifier)) {
            throw ServiceException.notFound("Invalid Username");
        }
    }

}
