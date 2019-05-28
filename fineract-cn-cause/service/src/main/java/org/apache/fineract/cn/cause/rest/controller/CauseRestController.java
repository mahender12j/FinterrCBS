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
import org.apache.fineract.cn.api.util.UserContextHolder;
import org.apache.fineract.cn.cause.ServiceConstants;
import org.apache.fineract.cn.cause.api.v1.PermittableGroupIds;
import org.apache.fineract.cn.cause.api.v1.domain.*;
import org.apache.fineract.cn.cause.internal.command.*;
import org.apache.fineract.cn.cause.internal.repository.CauseEntity;
import org.apache.fineract.cn.cause.internal.repository.CauseStateRepository;
import org.apache.fineract.cn.cause.internal.repository.PortraitEntity;
import org.apache.fineract.cn.cause.internal.service.CauseService;
import org.apache.fineract.cn.cause.internal.service.TaskService;
import org.apache.fineract.cn.command.gateway.CommandGateway;
import org.apache.fineract.cn.lang.ServiceException;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static org.apache.fineract.cn.cause.api.v1.domain.Cause.State.*;

@RestController
@RequestMapping("/")
public class CauseRestController {

    private final Logger logger;
    private final CommandGateway commandGateway;
    private final CauseService causeService;
    private final TaskService taskService;
    private final Environment environment;
    private final CauseStateRepository causeStateRepository;

    @Autowired
    public CauseRestController(@Qualifier(ServiceConstants.LOGGER_NAME) final Logger logger,
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

    @Permittable(value = AcceptedTokenType.SYSTEM)
    @RequestMapping(
            value = "/initialize",
            method = RequestMethod.POST,
            consumes = MediaType.ALL_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Void>
    initialize() throws InterruptedException {
        this.commandGateway.process(new InitializeServiceCommand());
        return ResponseEntity.accepted().build();
    }


    //    post cause to database
    @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.CAUSE)
    @RequestMapping(value = "/causes",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.ALL_VALUE
    )
    public
    @ResponseBody
    ResponseEntity<Void> createCause(@RequestBody final Cause cause) {
        throwIfCauseExists(cause.getIdentifier());
        throwIfDocumentNotValid(cause);

        this.commandGateway.process(new CreateCauseCommand(cause));
        return ResponseEntity.accepted().build();
    }


    @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.CAUSE)
    @RequestMapping(
            value = "/causes",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.ALL_VALUE
    )
    public
    @ResponseBody
    ResponseEntity<CausePage> fetchCauses(@RequestParam(value = "param", required = false) final String param,
                                          @RequestParam(value = "includeClosed", required = false) final Boolean includeClosed,
                                          @RequestParam(value = "pageIndex", required = false) final Integer pageIndex,
                                          @RequestParam(value = "size", required = false) final Integer size,
                                          @RequestParam(value = "sortColumn", required = false) final String sortColumn,
                                          @RequestParam(value = "sortDirection", required = false) final String sortDirection) {

        return ResponseEntity.ok(this.causeService.fetchCause((includeClosed != null ? includeClosed : Boolean.TRUE), param,
                this.causeService.createPageRequest(pageIndex, size, sortColumn, sortDirection)));
    }

    @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.PORTRAIT)
    @RequestMapping(
            value = "/causes/{identifier}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.ALL_VALUE
    )
    public
    @ResponseBody
    ResponseEntity<Void> deleteCause(@PathVariable("identifier") final String identifier) {

        final Optional<Cause> cause = this.causeService.findCause(identifier);
        if (!cause.isPresent()) {
            throw ServiceException.notFound("Cause {0} not found.", identifier);
        }

        if (!CauseService.isRemovableState(cause.get().getCurrentState())) {
            throw ServiceException.conflict("Unable to delete this cause!");
        }

        this.commandGateway.process(new DeleteCauseCommand(identifier, "DELETING CAUSE"));
        return ResponseEntity.accepted().build();
    }

    @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.CAUSE)
    @RequestMapping(
            value = "/causes/{identifier}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.ALL_VALUE
    )
    public
    @ResponseBody
    ResponseEntity<Cause> findCause(@PathVariable("identifier") final String identifier) {
        final Optional<Cause> cause = this.causeService.findCause(identifier);
        if (cause.isPresent()) {
            return ResponseEntity.ok(cause.get());
        } else {
            throw ServiceException.notFound("Cause {0} not found.", identifier);
        }
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


    @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.CAUSE)
    @RequestMapping(
            value = "/causes/cadmin/all",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.ALL_VALUE
    )
    public
    @ResponseBody
    ResponseEntity<CaAdminCauseData> findAllCauseData() {
        return ResponseEntity.ok(this.causeService.findAllCauseData());
    }


//    cause update by cause ID

    @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.CAUSE)
    @RequestMapping(
            value = "/causes/{identifier}",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public
    @ResponseBody
    ResponseEntity<Void> updateCause(@PathVariable("identifier") final String identifier,
                                     @RequestBody final Cause cause) {
        throwIfCauseNotExists(identifier);
        throwIfDocumentNotValid(cause);
        throwIfActionMoreThan2Times(identifier, new HashSet<>(Arrays.asList(PENDING.name(), APPROVED.name(), REJECTED.name())));
        this.commandGateway.process(new UpdateCauseCommand(identifier, cause));
        return ResponseEntity.accepted().build();
    }


    //    publish cause

    @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.CAUSE)
    @RequestMapping(
            value = "/causes/{identifier}/publish",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public
    @ResponseBody
    ResponseEntity<Void> publishCause(@PathVariable("identifier") final String identifier) {
        CauseEntity causeEntity = causeService.findCauseEntity(identifier).orElseThrow(() -> ServiceException.notFound("Cause {0} not found.", identifier));
        if (causeEntity.getCurrentState().toLowerCase().equals(Cause.State.APPROVED.name().toLowerCase())) {
            this.commandGateway.process(new PublishCauseCommand(identifier));
        } else {
            throw ServiceException.conflict("Cause {0} not APPROVED state. Currently the cause is in {1} state.", identifier, causeEntity.getCurrentState());
        }

        return ResponseEntity.accepted().build();
    }


    @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.CAUSE)
    @RequestMapping(
            value = "/causes/{identifier}/extended",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public
    @ResponseBody
    ResponseEntity<Void> extendCause(@PathVariable("identifier") final String identifier,
                                     @RequestBody CauseState causeState) {
        CauseEntity causeEntity = causeService.findCauseEntity(identifier).orElseThrow(() -> ServiceException.notFound("Cause {0} not found.", identifier));
        throwIfCauseIsNotActive(causeEntity);
        throwIfMin2DaysLeft(causeEntity);
        throwIfActionMoreThan2Times(identifier, new HashSet<>(Collections.singletonList(EXTENDED.name())));
        LocalDateTime localDateTime = LocalDateTime.parse(causeState.getNewDate(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        this.commandGateway.process(new ExtendCauseCommand(identifier, localDateTime));
        return ResponseEntity.accepted().build();
    }

    private void throwIfCauseIsNotActive(CauseEntity causeEntity) {
        if (!causeEntity.getCurrentState().toLowerCase().equals(Cause.State.ACTIVE.name().toLowerCase()))
            throw ServiceException.conflict("Cause {0} not ACTIVE state. Currently the cause is in {1} state.", causeEntity.getIdentifier(), causeEntity.getCurrentState());
    }


    private void throwIfMin2DaysLeft(final CauseEntity causeEntity) {
        LocalDateTime causeEndDate = causeEntity.getEndDate().minusDays(2);
        if (causeEndDate.isBefore(LocalDateTime.now(Clock.systemDefaultZone()))) {
            throw ServiceException.conflict("Cause {0} required minimum two days at least. Current end date: {1}", causeEntity.getIdentifier(), causeEntity.getEndDate());
        }
    }


    @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.CAUSE)
    @RequestMapping(
            value = "/causes/{identifier}/approved",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public
    @ResponseBody
    ResponseEntity<Void> approveCause(@PathVariable("identifier") final String identifier,
                                      @RequestBody final CauseApprove cause) {
        CauseEntity causeEntity = causeService.findCauseEntity(identifier).orElseThrow(() -> ServiceException.notFound("Cause {0} not found.", identifier));
        throwIfActionMoreThan2Times(identifier, new HashSet<>(Arrays.asList(APPROVED.name())));

        if (causeEntity.getCurrentState().toLowerCase().equals(PENDING.name().toLowerCase())) {
            this.commandGateway.process(new ApproveCauseCommand(identifier, cause.getFinRate(), cause.getSuccessFees()));
        } else {
            throw ServiceException.conflict("Cause {0} not PENDING state. Currently the cause is in {1} state.", identifier, causeEntity.getCurrentState());
        }

        return ResponseEntity.accepted().build();
    }


    @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.CAUSE)
    @RequestMapping(value = "/causes/expired", method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public
    @ResponseBody
    ResponseEntity<Void> expiredCause() {
        this.commandGateway.process(new ExpiredCauseCommand());
        return ResponseEntity.accepted().build();
    }


    @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.CAUSE)
    @RequestMapping(value = "/causes/inactive", method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public
    @ResponseBody
    ResponseEntity<Void> inactiveCause() {
        this.commandGateway.process(new InactiveCauseCommand());
        return ResponseEntity.accepted().build();
    }


    @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.CAUSE)
    @RequestMapping(
            value = "/causes/{identifier}/rejected",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public
    @ResponseBody
    ResponseEntity<Void> RejectCause(@PathVariable("identifier") final String identifier,
                                     @RequestBody final Cause cause) {
        CauseEntity causeEntity = causeService.findCauseEntity(identifier).orElseThrow(() -> ServiceException.notFound("Cause {0} not found.", identifier));
        throwIfActionMoreThan2Times(identifier, new HashSet<>(Arrays.asList(Cause.State.REJECTED.name())));

        if (PENDING.name().toLowerCase().equals(causeEntity.getCurrentState().toLowerCase())) {
            this.commandGateway.process(new RejectCauseCommand(identifier, cause.getRejectedReason()));
        } else {
            throw ServiceException.conflict("Cause {0} not PENDING state. Currently the cause is in {1} state.", identifier, causeEntity.getCurrentState());
        }
        return ResponseEntity.accepted().build();
    }


    @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.CAUSE)
    @RequestMapping(
            value = "/causes/{identifier}/commands",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public
    @ResponseBody
    ResponseEntity<Void> causeCommand(@PathVariable("identifier") final String identifier,
                                      @RequestBody final Command command) {
        final Optional<Cause> causeOptional = this.causeService.findCause(identifier);
        if (causeOptional.isPresent()) {
            final Cause cause = causeOptional.get();
            final Command.Action action = Command.Action.valueOf(command.getAction());
            final String currentState = cause.getCurrentState();
            switch (action) {
                case ACTIVATE:
                    if (PENDING.name().equals(currentState)) {
                        this.commandGateway.process(new ActivateCauseCommand(identifier, command.getComment()));
                    }
                    break;
                case LOCK:
                    if (Cause.State.ACTIVE.name().equals(currentState)) {
                        this.commandGateway.process(new LockCauseCommand(identifier, command.getComment()));
                    }
                    break;
                case UNLOCK:
                    if (Cause.State.LOCKED.name().equals(currentState)) {
                        this.commandGateway.process(new UnlockCauseCommand(identifier, command.getComment()));
                    }
                    break;
                case CLOSE:
                    if (Cause.State.ACTIVE.name().equals(currentState)
                            || Cause.State.LOCKED.name().equals(currentState)
                            || PENDING.name().equals(currentState)) {
                        this.commandGateway.process(new CloseCauseCommand(identifier, command.getComment()));
                    }
                    break;
                case REOPEN:
                    if (Cause.State.CLOSED.name().equals(currentState)) {
                        this.commandGateway.process(new ReopenCauseCommand(identifier, command.getComment()));
                    }
                    break;
                default:
                    throw ServiceException.badRequest("Unsupported action {0}.", command.getAction());
            }
        } else {
            throw ServiceException.notFound("Cause {0} not found.", identifier);
        }
        return ResponseEntity.accepted().build();
    }

    @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.CAUSE)
    @RequestMapping(
            value = "/causes/{identifier}/commands",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.ALL_VALUE
    )
    public
    @ResponseBody
    ResponseEntity<List<Command>> fetchCauseCommands(@PathVariable("identifier") final String identifier) {
        if (this.causeService.causeExists(identifier)) {
            return ResponseEntity.ok(this.causeService.fetchCommandsByCause(identifier).collect(Collectors.toList()));
        } else {
            throw ServiceException.notFound("Cause {0} not found.", identifier);
        }
    }

    @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.CAUSE)
    @RequestMapping(
            value = "/causes/{identifier}/ratings",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )

    public
    @ResponseBody
    ResponseEntity<Void> causeRating(@PathVariable("identifier") final String identifier,
                                     @RequestBody final CauseRating rating) {

        if (this.causeService.causeExists(identifier)) {
            this.commandGateway.process(new CreateRatingCommand(identifier, rating));
        } else {
            throw ServiceException.notFound("Cause {0} not found.", identifier);
        }
        return ResponseEntity.accepted().build();
    }

    @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.CAUSE)
    @RequestMapping(
            value = "/causes/{identifier}/ratings",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.ALL_VALUE
    )

    public
    @ResponseBody
    ResponseEntity<List<CauseRating>> fetchCauseRatings(@PathVariable("identifier") final String identifier) {
        if (this.causeService.causeExists(identifier)) {
            return ResponseEntity.ok(this.causeService.fetchActiveRatingsByCause(identifier, Boolean.TRUE).collect(Collectors.toList()));
        } else {
            throw ServiceException.notFound("Cause {0} not found.", identifier);
        }
    }


    @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.CAUSE)
    @RequestMapping(
            value = "/causes/{identifier}/tasks/{taskIdentifier}",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public
    @ResponseBody
    ResponseEntity<Void> addTaskToCause(@PathVariable("identifier") final String identifier,
                                        @PathVariable("taskIdentifier") final String taskIdentifier) {
        if (this.causeService.causeExists(identifier)) {
            if (this.taskService.taskDefinitionExists(taskIdentifier)) {
                this.commandGateway.process(new AddTaskDefinitionToCauseCommand(identifier, taskIdentifier));
            } else {
                throw ServiceException.notFound("Task definition {0} not found.", taskIdentifier);
            }
        } else {
            throw ServiceException.notFound("Cause {0} not found.", identifier);
        }
        return ResponseEntity.accepted().build();
    }

    @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.CAUSE)
    @RequestMapping(
            value = "/causes/{identifier}/tasks/{taskIdentifier}",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public
    @ResponseBody
    ResponseEntity<Void> taskForCauseExecuted(@PathVariable("identifier") final String identifier,
                                              @PathVariable("taskIdentifier") final String taskIdentifier) {
        final Optional<Cause> optionalCause = this.causeService.findCause(identifier);
        if (optionalCause.isPresent()) {
            final Cause cause = optionalCause.get();
            final Optional<TaskDefinition> optionalTaskDefinition = this.taskService.findByIdentifier(taskIdentifier);
            if (optionalTaskDefinition.isPresent()) {
                final TaskDefinition taskDefinition = optionalTaskDefinition.get();
                switch (TaskDefinition.Type.valueOf(taskDefinition.getType())) {
                    case ID_CARD:
                        break;
                    case FOUR_EYES:
                        if (cause.getCreatedBy().equals(UserContextHolder.checkedGetUser())) {
                            throw ServiceException.conflict("Signing user must be different than creator.");
                        }
                        break;
                }
                this.commandGateway.process(new ExecuteTaskForCauseCommand(identifier, taskIdentifier));
            } else {
                throw ServiceException.notFound("Task definition {0} not found.", taskIdentifier);
            }
        } else {
            throw ServiceException.notFound("Cause {0} not found.", identifier);
        }
        return ResponseEntity.accepted().build();
    }

    @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.CAUSE)
    @RequestMapping(
            value = "/causes/{identifier}/tasks",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.ALL_VALUE
    )
    public
    @ResponseBody
    ResponseEntity<List<TaskDefinition>> findTasksForCause(@PathVariable("identifier") final String identifier,
                                                           @RequestParam(value = "includeExecuted", required = false) final Boolean includeExecuted) {
        if (this.causeService.causeExists(identifier)) {
            return ResponseEntity.ok(this.taskService.findTasksByCause(identifier, (includeExecuted != null ? includeExecuted : Boolean.FALSE)));
        } else {
            throw ServiceException.notFound("Cause {0} not found.", identifier);
        }
    }

    @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.CAUSE)
    @RequestMapping(
            value = "/causes/{identifier}/address",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public
    @ResponseBody
    ResponseEntity<Void> putAddress(@PathVariable("identifier") final String identifier,
                                    @RequestBody @Valid final Address address) {
        if (this.causeService.causeExists(identifier)) {
            this.commandGateway.process(new UpdateAddressCommand(identifier, address));
        } else {
            throw ServiceException.notFound("Cause {0} not found.", identifier);
        }
        return ResponseEntity.accepted().build();
    }

    @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.CAUSE)
    @RequestMapping(
            value = "/causes/{identifier}/contact",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public
    @ResponseBody
    ResponseEntity<Void> putContactDetails(@PathVariable("identifier") final String identifier,
                                           @RequestBody final List<ContactDetail> contactDetails) {
        if (this.causeService.causeExists(identifier)) {
            this.commandGateway.process(new UpdateContactDetailsCommand(identifier, contactDetails));
        } else {
            throw ServiceException.notFound("Cause {0} not found.", identifier);
        }
        return ResponseEntity.accepted().build();
    }

    @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.IDENTIFICATIONS)
    @RequestMapping(
            value = "/causes/{identifier}/identifications/{number}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.ALL_VALUE
    )
    public
    @ResponseBody
    ResponseEntity<Void> deleteIdentificationCard(@PathVariable("identifier") final String identifier,
                                                  @PathVariable("number") final String number) {
        this.throwIfCauseNotExists(identifier);

        this.commandGateway.process(new DeleteIdentificationCardCommand(number));

        return ResponseEntity.accepted().build();
    }

    @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.PORTRAIT)
    @RequestMapping(
            value = "/causes/{identifier}/portrait",
            method = RequestMethod.GET,
            consumes = MediaType.ALL_VALUE
    )
    public ResponseEntity<byte[]> getPortrait(@PathVariable("identifier") final String identifier) {
        final PortraitEntity portrait = this.causeService.findPortrait(identifier)
                .orElseThrow(() -> ServiceException.notFound("Portrait for Cause ''{0}'' not found.", identifier));

        return ResponseEntity
                .ok()
                .contentType(MediaType.parseMediaType(portrait.getContentType()))
                .contentLength(portrait.getImage().length)
                .body(portrait.getImage());
    }

    @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.PORTRAIT)
    @RequestMapping(
            value = "/causes/{identifier}/portrait",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public @ResponseBody
    ResponseEntity<Void> postPortrait(@PathVariable("identifier") final String identifier,
                                      @RequestBody final MultipartFile portrait) {
        if (portrait == null) {
            throw ServiceException.badRequest("Portrait not found");
        }

        this.throwIfCauseNotExists(identifier);
        this.throwIfInvalidSize(portrait.getSize());
        this.throwIfInvalidContentType(portrait.getContentType());

        try {
            this.commandGateway.process(new DeletePortraitCommand(identifier), String.class).get();
        } catch (Throwable e) {
            logger.warn("Could not delete portrait: {0}", e.getMessage());
        }

        this.commandGateway.process(new CreatePortraitCommand(identifier, portrait));

        return ResponseEntity.accepted().build();
    }

    @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.PORTRAIT)
    @RequestMapping(
            value = "/causes/{identifier}/portrait",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.ALL_VALUE
    )
    public @ResponseBody
    ResponseEntity<Void> deletePortrait(@PathVariable("identifier") final String identifier) {
        this.commandGateway.process(new DeletePortraitCommand(identifier));

        return ResponseEntity.accepted().build();
    }

    @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.TASK)
    @RequestMapping(
            value = "/causes/tasks",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public
    @ResponseBody
    ResponseEntity<Void> createTask(@RequestBody final TaskDefinition taskDefinition) {
        if (this.taskService.taskDefinitionExists(taskDefinition.getIdentifier())) {
            throw ServiceException.conflict("Task definition {0} already exists.", taskDefinition.getIdentifier());
        } else {
            this.commandGateway.process(new CreateTaskDefinitionCommand(taskDefinition));
        }
        return ResponseEntity.accepted().build();
    }

    @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.TASK)
    @RequestMapping(
            value = "/causes/tasks",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.ALL_VALUE
    )
    public
    @ResponseBody
    ResponseEntity<List<TaskDefinition>> fetchAllTasks() {
        return ResponseEntity.ok(this.taskService.fetchAll());
    }

    @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.TASK)
    @RequestMapping(
            value = "/causes/tasks/{identifier}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.ALL_VALUE
    )
    public
    @ResponseBody
    ResponseEntity<TaskDefinition> findTask(@PathVariable("identifier") final String identifier) {
        final Optional<TaskDefinition> taskDefinitionOptional = this.taskService.findByIdentifier(identifier);
        if (taskDefinitionOptional.isPresent()) {
            return ResponseEntity.ok(taskDefinitionOptional.get());
        } else {
            throw ServiceException.notFound("Task {0} not found.", identifier);
        }
    }

    @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.TASK)
    @RequestMapping(
            value = "/causes/tasks/{identifier}",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public
    @ResponseBody
    ResponseEntity<Void> updateTask(@PathVariable("identifier") final String identifier, @RequestBody final TaskDefinition taskDefinition) {
        if (this.taskService.taskDefinitionExists(identifier)) {
            this.commandGateway.process(new UpdateTaskDefinitionCommand(identifier, taskDefinition));
        } else {
            throw ServiceException.notFound("Task {0} not found.", identifier);
        }
        return ResponseEntity.accepted().build();
    }

    @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.CAUSE)
    @RequestMapping(
            value = "/causes/{identifier}/actions",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.ALL_VALUE
    )
    public
    @ResponseBody
    ResponseEntity<List<ProcessStep>> fetchProcessSteps(@PathVariable(value = "identifier") final String causeIdentifier) {
        this.throwIfCauseNotExists(causeIdentifier);
        return ResponseEntity.ok(this.causeService.getProcessSteps(causeIdentifier));
    }

    private void throwIfCauseNotExists(final String identifier) {
        if (!this.causeService.causeExists(identifier)) {
            throw ServiceException.notFound("Cause {0} not found.", identifier);
        }
    }


    private void throwIfCauseExists(final String identifier) {
        if (this.causeService.causeExists(identifier)) {
            throw ServiceException.notFound("Cause {0} already exist.", identifier);
        }
    }

    private void throwIfActionMoreThan2Times(String identifier, final Set<String> state) {
        if (this.causeStateRepository.totalStateByCauseIdentifier(identifier, state) >= 2) {
            throw ServiceException.conflict("Cause {0} cant be {1} more than two times.", identifier, state.toString());
        }
    }


    private void throwIfDocumentNotValid(Cause cause) {
        if (cause.getCauseFiles().stream().noneMatch(d -> d.getType().toLowerCase().equals("terms"))) {
            throw ServiceException.badRequest("Terms document is required");
        } else if (cause.getCauseFiles().stream().noneMatch(d -> d.getType().toLowerCase().equals("feature"))) {
            throw ServiceException.badRequest("Feature document is required");
        } else if (cause.getTaxExamption() && cause.getCauseFiles().stream().noneMatch(d -> d.getType().toLowerCase().equals("tax"))) {
            throw ServiceException.badRequest("Tax document is required when Tax is not exemption");
        } else if (cause.getCauseFiles().stream().filter(d -> d.getType().toLowerCase().equals("terms")).count() > 1) {
            throw ServiceException.badRequest("Max one terms document can be uploaded");
        } else if (cause.getCauseFiles().stream().filter(d -> d.getType().toLowerCase().equals("feature")).count() > 1) {
            throw ServiceException.badRequest("Max one feature document can be uploaded");
        } else if (cause.getCauseFiles().stream().filter(d -> d.getType().toLowerCase().equals("tax")).count() > 1) {
            throw ServiceException.badRequest("Max one Tax document can be uploaded");
        }
    }

    private void throwIfInvalidSize(final Long size) {
        final Long maxSize = this.environment.getProperty("upload.image.max-size", Long.class);

        if (size > maxSize) {
            throw ServiceException.badRequest("Image can''t exceed size of {0}", maxSize);
        }
    }

    private void throwIfInvalidContentType(final String contentType) {
        if (!contentType.contains(MediaType.IMAGE_JPEG_VALUE)
                && !contentType.contains(MediaType.IMAGE_PNG_VALUE)) {
            throw ServiceException.badRequest("Only content type {0} and {1} allowed", MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE);
        }
    }
}
