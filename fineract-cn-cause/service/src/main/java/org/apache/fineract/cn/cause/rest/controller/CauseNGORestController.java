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
package org.apache.fineract.cn.cause.rest.controller;

import org.apache.fineract.cn.anubis.annotation.AcceptedTokenType;
import org.apache.fineract.cn.anubis.annotation.Permittable;
import org.apache.fineract.cn.cause.ServiceConstants;
import org.apache.fineract.cn.cause.api.v1.PermittableGroupIds;
import org.apache.fineract.cn.cause.api.v1.domain.*;
import org.apache.fineract.cn.cause.internal.command.CreateCauseCommand;
import org.apache.fineract.cn.cause.internal.command.CreateCauseUpdateCommand;
import org.apache.fineract.cn.cause.internal.repository.CauseStateRepository;
import org.apache.fineract.cn.cause.internal.service.CauseService;
import org.apache.fineract.cn.cause.internal.service.TaskService;
import org.apache.fineract.cn.command.gateway.CommandGateway;
import org.apache.fineract.cn.lang.ServiceException;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
public class CauseNGORestController {

    private final Logger logger;
    private final CommandGateway commandGateway;
    private final CauseService causeService;
    private final TaskService taskService;
    private final Environment environment;
    private final CauseStateRepository causeStateRepository;

    @Autowired
    public CauseNGORestController(@Qualifier(ServiceConstants.LOGGER_NAME) final Logger logger,
                                  final CommandGateway commandGateway,
                                  final CauseService causeService,
                                  final TaskService taskService,
                                  final Environment environment, CauseStateRepository causeStateRepository) {
        super();
        this.logger = logger;
        this.commandGateway = commandGateway;
        this.causeService = causeService;
        this.taskService = taskService;
        this.environment = environment;
        this.causeStateRepository = causeStateRepository;
    }


    @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.CAUSE)
    @RequestMapping(
            value = "/causes/ngo/{identifier}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.ALL_VALUE
    )
    public
    @ResponseBody
    ResponseEntity<NGOStatistics> findCausebyCreatedBy(@PathVariable("identifier") final String createdBy) {
        return ResponseEntity.ok(this.causeService.fetchCauseByCreatedBy(createdBy));
    }


    //    post cause update to database
    @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.CAUSE)
    @RequestMapping(value = "/causes/{identifier}/ngo/update",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.ALL_VALUE
    )
    public
    @ResponseBody
    ResponseEntity<Void> createCause(@PathVariable("identifier") final String identifier,
                                     @RequestBody final CauseUpdate causeUpdate) {
        throwIfCauseNotExists(identifier);

        this.commandGateway.process(new CreateCauseUpdateCommand(identifier, causeUpdate));
        return ResponseEntity.accepted().build();
    }


//    util function

    private void throwIfCauseNotExists(final String identifier) {
        if (!this.causeService.causeExists(identifier)) {
            throw ServiceException.notFound("Cause {0} not found", identifier);
        }
    }


}
