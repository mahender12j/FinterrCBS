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
package org.apache.fineract.cn.identity.rest;

import org.apache.fineract.cn.anubis.annotation.AcceptedTokenType;
import org.apache.fineract.cn.anubis.annotation.Permittable;
import org.apache.fineract.cn.api.util.UserContextHolder;
import org.apache.fineract.cn.command.domain.CommandCallback;
import org.apache.fineract.cn.command.domain.CommandProcessingException;
import org.apache.fineract.cn.command.gateway.CommandGateway;
import org.apache.fineract.cn.identity.api.v1.PermittableGroupIds;
import org.apache.fineract.cn.identity.api.v1.domain.*;
import org.apache.fineract.cn.identity.internal.command.ChangeUserPasswordCommand;
import org.apache.fineract.cn.identity.internal.command.ChangeUserRoleCommand;
import org.apache.fineract.cn.identity.internal.command.CreateUserCommand;
import org.apache.fineract.cn.identity.internal.service.UserService;
import org.apache.fineract.cn.identity.internal.util.IdentityConstants;
import org.apache.fineract.cn.lang.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;
import java.util.concurrent.ExecutionException;


/**
 * @author Myrle Krantz
 */
@SuppressWarnings("unused")
@RestController
@RequestMapping("/users")
public class UserRestController {
    private final UserService service;
    private final CommandGateway commandGateway;

    @Autowired
    public UserRestController(
            final CommandGateway commandGateway,
            final UserService service) {
        this.commandGateway = commandGateway;
        this.service = service;
    }

    @RequestMapping(method = RequestMethod.GET,
            consumes = {MediaType.ALL_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.IDENTITY_MANAGEMENT)
    public @ResponseBody
    List<User> findAll() {
        return this.service.findAll();
    }


    @RequestMapping(method = RequestMethod.POST,
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.IDENTITY_MANAGEMENT)
    public @ResponseBody
    ResponseEntity<User> create(@RequestBody @Valid final UserWithPassword instance) {
        if (instance == null)
            throw ServiceException.badRequest("Instance may not be null.");

        if (service.findByIdentifier(instance.getIdentifier()).isPresent())
            throw ServiceException.conflict("This username is already taken. Please try another one.");

        final CreateUserCommand createCommand = new CreateUserCommand(instance);
        try {
            CommandCallback<String> commandCallback = this.commandGateway.process(createCommand, String.class);
            return new ResponseEntity<>(checkIdentifier(commandCallback.get()), HttpStatus.ACCEPTED);
        } catch (CommandProcessingException | InterruptedException | ExecutionException e) {
            throw ServiceException.internalError("Sorry! Something went wrong");
        }
    }


    @RequestMapping(value = PathConstants.IDENTIFIER_RESOURCE_STRING, method = RequestMethod.GET,
            consumes = {MediaType.ALL_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.IDENTITY_MANAGEMENT)
    public @ResponseBody
    ResponseEntity<User> get(@PathVariable(PathConstants.IDENTIFIER_PATH_VARIABLE) final String userIdentifier) {
        return new ResponseEntity<>(checkIdentifier(userIdentifier), HttpStatus.OK);
    }


//

    @Permittable(value = AcceptedTokenType.GUEST)
    @RequestMapping(value = "/{identifier}/find",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.ALL_VALUE)
    public @ResponseBody
    Map<String, Boolean> findValidCustomer(@PathVariable("identifier") final String identifier) {
        System.out.println("Customer exist api called");
        this.checkIdentifier(identifier);
        return Collections.singletonMap("success", true);

    }


//


    @RequestMapping(value = PathConstants.IDENTIFIER_RESOURCE_STRING + "/roleIdentifier", method = RequestMethod.PUT,
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.IDENTITY_MANAGEMENT)
    public @ResponseBody
    ResponseEntity<Void> changeUserRole(
            @PathVariable(PathConstants.IDENTIFIER_PATH_VARIABLE) final String userIdentifier,
            @RequestBody @Valid final RoleIdentifier roleIdentifier) {
        if (userIdentifier.equals(IdentityConstants.SU_NAME))
            throw ServiceException.badRequest("Role of user with identifier: " + userIdentifier + " cannot be changed.");

        checkIdentifier(userIdentifier);

        final ChangeUserRoleCommand changeCommand = new ChangeUserRoleCommand(userIdentifier, roleIdentifier.getIdentifier());
        this.commandGateway.process(changeCommand);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }


    @RequestMapping(value = PathConstants.IDENTIFIER_RESOURCE_STRING + "/permissions", method = RequestMethod.GET,
            consumes = {MediaType.ALL_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.IDENTITY_MANAGEMENT)
    @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.SELF_MANAGEMENT, permittedEndpoint = "/users/{useridentifier}/permissions")
    @ResponseBody
    Set<Permission> getUserPermissions(@PathVariable(PathConstants.IDENTIFIER_PATH_VARIABLE) String userIdentifier) {
        checkIdentifier(userIdentifier);

        return service.getPermissions(userIdentifier);
    }


    @RequestMapping(value = PathConstants.IDENTIFIER_RESOURCE_STRING + "/password", method = RequestMethod.PUT,
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.IDENTITY_MANAGEMENT)
    @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.SELF_MANAGEMENT, permittedEndpoint = "/users/{useridentifier}/password")
    public @ResponseBody
    ResponseEntity<Void> changeUserPassword(
            @PathVariable(PathConstants.IDENTIFIER_PATH_VARIABLE) final String userIdentifier,
            @RequestBody @Valid final Password password) {
        if (userIdentifier.equals(IdentityConstants.SU_NAME) && !UserContextHolder.checkedGetUser().equals(
                IdentityConstants.SU_NAME))
            throw ServiceException.badRequest("Password of ''{0}'' can only be changed by themselves.", IdentityConstants.SU_NAME);

        checkIdentifier(userIdentifier);

        checkPassword(password);

        final ChangeUserPasswordCommand changeCommand = new ChangeUserPasswordCommand(userIdentifier, password.getPassword());
        this.commandGateway.process(changeCommand);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }


    private void checkPassword(final Password password) {
        if (password == null || password.getPassword() == null || password.getPassword().isEmpty())
            throw ServiceException.badRequest("password may not be empty.");
    }

    private User checkIdentifier(final String identifier) {
        if (identifier == null)
            throw ServiceException.badRequest("identifier may not be null.");

        return service.findByIdentifier(identifier)
                .orElseThrow(() -> ServiceException.notFound("Instance with identifier " + identifier + " doesn't exist."));
    }
}
