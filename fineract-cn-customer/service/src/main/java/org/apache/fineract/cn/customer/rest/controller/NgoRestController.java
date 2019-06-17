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
import org.apache.fineract.cn.customer.api.v1.domain.Customer;
import org.apache.fineract.cn.customer.api.v1.domain.NgoProfile;
import org.apache.fineract.cn.customer.internal.command.CreateNGOCommand;
import org.apache.fineract.cn.customer.internal.service.CAdminService;
import org.apache.fineract.cn.customer.internal.service.CustomerService;
import org.apache.fineract.cn.customer.internal.service.NGOService;
import org.apache.fineract.cn.lang.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/")
public class NgoRestController {


    private final CommandGateway commandGateway;
    private final CustomerService customerService;
    private final NGOService ngoService;

    @Autowired
    public NgoRestController(final CommandGateway commandGateway,
                             final CustomerService customerService,
                             final NGOService ngoService) {
        super();
        this.commandGateway = commandGateway;
        this.customerService = customerService;
        this.ngoService = ngoService;
    }

    @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.NGO)
    @RequestMapping(
            value = "/ngo/{identifier}/profile",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.ALL_VALUE
    )
    public
    @ResponseBody
    ResponseEntity<NgoProfile> getNgoProfile(@PathVariable("identifier") final String identifier) {
        throwIfNGONotExists(identifier);
        return ResponseEntity.ok(ngoService.getNgoProfile(identifier));
    }


    @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.NGO)
    @RequestMapping(
            value = "/ngo/{identifier}/profile",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public
    @ResponseBody
    ResponseEntity<Void> createCustomer(
            @PathVariable("identifier") final String identifier,
            @RequestBody @Valid final NgoProfile ngoProfile) {

        throwIfIdentifierNotMatch(identifier, ngoProfile.getNgoIdentifier());
        throwIfNGONotExists(identifier);
        throwIfNGOProfileExist(identifier);

        this.commandGateway.process(new CreateNGOCommand(ngoProfile, identifier));
        return ResponseEntity.accepted().build();
    }

    private void throwIfIdentifierNotMatch(String identifier, String ngoIdentifier) {
        if (!identifier.equals(ngoIdentifier)) {
            throw ServiceException.conflict("Sorry !! identifier didn't match!!!");
        }
    }

    private void throwIfNGOProfileExist(String ngoIdentifier) {
        if (this.ngoService.ngoProfileExist(ngoIdentifier)) {
            throw ServiceException.conflict("Sorry !! NGO Profile already exist !!!");
        }
    }


    private void throwIfNGONotExists(final String identifier) {
        if (!this.customerService.ngoExist(identifier)) {
            throw ServiceException.notFound("Invalid Username or NGO not available");
        }
    }

}
