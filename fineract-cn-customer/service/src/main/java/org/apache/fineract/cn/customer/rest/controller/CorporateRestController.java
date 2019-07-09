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
import org.apache.fineract.cn.command.domain.CommandCallback;
import org.apache.fineract.cn.command.domain.CommandProcessingException;
import org.apache.fineract.cn.command.gateway.CommandGateway;
import org.apache.fineract.cn.customer.PermittableGroupIds;
import org.apache.fineract.cn.customer.api.v1.domain.ContactDetail;
import org.apache.fineract.cn.customer.api.v1.domain.CorporateUser;
import org.apache.fineract.cn.customer.internal.command.CreateCorporateCommand;
import org.apache.fineract.cn.customer.internal.service.CorporateService;
import org.apache.fineract.cn.customer.internal.service.CustomerService;
import org.apache.fineract.cn.lang.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/")
public class CorporateRestController {

    private final CustomerService customerService;
    private final CorporateService corporateService;
    private final CommandGateway commandGateway;

    @Autowired
    public CorporateRestController(final CustomerService customerService,
                                   final CorporateService corporateService,
                                   final CommandGateway commandGateway) {
        super();
        this.customerService = customerService;
        this.corporateService = corporateService;
        this.commandGateway = commandGateway;
    }

    @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.CORPORATE)
    @RequestMapping(
            value = "/corporates",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public
    @ResponseBody
    ResponseEntity<CorporateUser> createCustomer(@RequestBody @Valid final CorporateUser corporateUser) {
        throwIfUserAlreadyExist(corporateUser.getIdentifier());
        throwIfUserContactDetailsIsAlreadyVerified(corporateUser.getContactDetails());
        try {
            CommandCallback<CorporateUser> res = this.commandGateway.process(new CreateCorporateCommand(corporateUser), CorporateUser.class);
            return ResponseEntity.ok(res.get());
        } catch (CommandProcessingException | InterruptedException | ExecutionException e) {
            throw ServiceException.internalError("Sorry! Something went wrong");
        }
    }


    private void throwIfUserContactDetailsIsAlreadyVerified(List<ContactDetail> contactDetails) {
        contactDetails.forEach(contactDetail -> {
            if (this.corporateService.isContactDetailExist(contactDetail.getType(), contactDetail.getValue())) {
                throw ServiceException.conflict("Sorry! User is already registered with this {0}: {1}", contactDetail.getType(), contactDetail.getValue());
            }
        });
    }


    private void throwIfUserAlreadyExist(String identifier) {
        if (this.customerService.customerExists(identifier)) {
            throw ServiceException.conflict("User {0} already exists.", identifier);
        }

    }
}
