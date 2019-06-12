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
import org.apache.fineract.cn.cause.api.v1.domain.CauseState;
import org.apache.fineract.cn.cause.api.v1.domain.CauseUpdate;
import org.apache.fineract.cn.cause.api.v1.domain.CauseUpdateInfo;
import org.apache.fineract.cn.cause.api.v1.domain.NGOStatistics;
import org.apache.fineract.cn.cause.internal.command.*;
import org.apache.fineract.cn.cause.internal.repository.CauseEntity;
import org.apache.fineract.cn.cause.internal.repository.DocumentStorageEntity;
import org.apache.fineract.cn.cause.internal.repository.DocumentStorageRepository;
import org.apache.fineract.cn.cause.internal.service.CauseService;
import org.apache.fineract.cn.cause.internal.service.CauseUpdateService;
import org.apache.fineract.cn.command.domain.CommandCallback;
import org.apache.fineract.cn.command.domain.CommandProcessingException;
import org.apache.fineract.cn.command.gateway.CommandGateway;
import org.apache.fineract.cn.lang.ServiceException;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashSet;
import java.util.concurrent.ExecutionException;

import static org.apache.fineract.cn.cause.api.v1.domain.Cause.State.EXTENDED;
import static org.apache.fineract.cn.cause.api.v1.domain.Cause.State.UNPUBLISH;

@RestController
@RequestMapping("/causes/ngo/{identifier}")
public class CauseNGORestController {

    private final Logger logger;
    private final CommandGateway commandGateway;
    private final CauseService causeService;
    private final CauseUpdateService causeUpdateService;
    private final DocumentStorageRepository storageRepository;

    @Autowired
    public CauseNGORestController(@Qualifier(ServiceConstants.LOGGER_NAME) final Logger logger,
                                  final CommandGateway commandGateway,
                                  final CauseService causeService,
                                  final CauseUpdateService causeUpdateService,
                                  final DocumentStorageRepository storageRepository) {
        super();
        this.logger = logger;
        this.commandGateway = commandGateway;
        this.causeService = causeService;
        this.causeUpdateService = causeUpdateService;
        this.storageRepository = storageRepository;
    }


    @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.CAUSE)
    @RequestMapping(
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.ALL_VALUE
    )
    public
    @ResponseBody
    ResponseEntity<NGOStatistics> findCausebyCreatedBy(@PathVariable("identifier") final String createdBy) {
        return ResponseEntity.ok(this.causeService.fetchCauseByCreatedBy(createdBy));
    }


    //    post cause update file to database
    @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.CAUSE)
    @RequestMapping(value = "/update/file",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.ALL_VALUE
    )
    public
    @ResponseBody
    ResponseEntity<CauseUpdateFile> createCauseUpdateFile(@PathVariable("identifier") final String identifier,
                                                          @RequestParam(value = "file") final MultipartFile file,
                                                          @RequestParam("docType") final String docType) {
        throwIfCauseNotExists(identifier);

        try {
            CommandCallback<CauseUpdateFile> ret = this.commandGateway.process(new CreateCauseUpdateFileCommand(file, docType), CauseUpdateFile.class);
            return new ResponseEntity<>(ret.get(), HttpStatus.OK);

        } catch (CommandProcessingException | InterruptedException | ExecutionException e) {
            throw ServiceException.internalError("Oops! Something went wrong. Try again…");
        }
    }


    //    receive the storage file
    @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.CAUSE)
    @RequestMapping(
            value = "/update/file/{uuid}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.ALL_VALUE
    )

    public ResponseEntity<byte[]> getStorageDocument(
            @PathVariable("identifier") final String identifier,
            @PathVariable("uuid") final String uuid) {
        throwIfCauseNotExists(identifier);

        final DocumentStorageEntity storageEntity = this.storageRepository.findByUuid(uuid).orElseThrow(() -> ServiceException.notFound("document ''{0}'' not found.", uuid));
        return ResponseEntity
                .ok()
                .contentType(MediaType.parseMediaType(storageEntity.getContentType()))
                .contentLength(storageEntity.getImage().length)
                .body(storageEntity.getImage());
    }


//    Get Cause update data

    @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.CAUSE)
    @RequestMapping(
            value = "/update",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.ALL_VALUE
    )
    public
    @ResponseBody
    ResponseEntity<CauseUpdateInfo> getCauseUpdateData(@PathVariable("identifier") final String identifier) {
        throwIfCauseNotExists(identifier);
        return ResponseEntity.ok(causeUpdateService.causeUpdateList(identifier));
    }


    //    post cause update to database
    @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.CAUSE)
    @RequestMapping(value = "/update",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.ALL_VALUE
    )
    public
    @ResponseBody
    ResponseEntity<Void> createCauseUpdate(@PathVariable("identifier") final String identifier,
                                           @RequestBody final CauseUpdate causeUpdate) {
        throwIfCauseNotExists(identifier);

        this.commandGateway.process(new CreateCauseUpdateCommand(identifier, causeUpdate));
        return ResponseEntity.accepted().build();
    }


    //    update cause update to database
    @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.CAUSE)
    @RequestMapping(value = "/update",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.ALL_VALUE
    )
    public
    @ResponseBody
    ResponseEntity<Void> updateCauseUpdateData(@PathVariable("identifier") final String identifier,
                                               @RequestBody final CauseUpdate causeUpdate) {
        throwIfCauseNotExists(identifier);

        this.commandGateway.process(new UpdateCauseUpdateCommand(identifier, causeUpdate));
        return ResponseEntity.accepted().build();
    }


    //    post cause update to database
    @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.CAUSE)
    @RequestMapping(value = "/update-info",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.ALL_VALUE
    )
    public
    @ResponseBody
    ResponseEntity<Void> createCauseInfo(@PathVariable("identifier") final String identifier,
                                         @RequestBody final CauseUpdateInfo causeUpdateInfo) {
        throwIfCauseNotExists(identifier);

        causeService.findCauseByIdentifier(identifier).ifPresent(this::throwIfCauseInfoExists);

        this.commandGateway.process(new CreateCauseUpdateInfoCommand(identifier, causeUpdateInfo));

        return ResponseEntity.accepted().build();
    }

//    utils function


    private void throwIfCauseInfoExists(final CauseEntity causeEntity) {
        boolean isPresent = causeUpdateService.causeInfoExists(causeEntity).isPresent();
        if (isPresent) {
            throw ServiceException.notFound("Cause info {0} already exist", causeEntity.toString());
        }

    }

    private void throwIfCauseNotExists(final String identifier) {
        if (!this.causeService.causeExists(identifier)) {
            throw ServiceException.notFound("Cause {0} not found", identifier);
        }
    }

//
//    private void throwIfCauseUpdateNotExists(final Long identifier) {
//        if (!this.causeUpdateService.causeUpdateExists(identifier)) {
//            throw ServiceException.notFound("Cause {0} not found", identifier);
//        }
//    }


}
