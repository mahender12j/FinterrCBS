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

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.fineract.cn.anubis.annotation.AcceptedTokenType;
import org.apache.fineract.cn.anubis.annotation.Permittable;
import org.apache.fineract.cn.api.util.UserContextHolder;
import org.apache.fineract.cn.cause.ServiceConstants;
import org.apache.fineract.cn.cause.api.v1.PermittableGroupIds;
import org.apache.fineract.cn.cause.api.v1.domain.*;
import org.apache.fineract.cn.cause.internal.command.*;
import org.apache.fineract.cn.cause.internal.repository.CauseEntity;
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
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/")
public class CauseRestController {

    private final Logger logger;
    private final CommandGateway commandGateway;
    private final CauseService causeService;
    private final TaskService taskService;
    private final Environment environment;

    @Autowired
    public CauseRestController(@Qualifier(ServiceConstants.LOGGER_NAME) final Logger logger,
                               final CommandGateway commandGateway,
                               final CauseService causeService,
                               final TaskService taskService,
                               final Environment environment) {
        super();
        this.logger = logger;
        this.commandGateway = commandGateway;
        this.causeService = causeService;
        this.taskService = taskService;
        this.environment = environment;
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

    @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.CAUSE)
    @RequestMapping(value = "/causes",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public @ResponseBody
    ResponseEntity<Void> createCause(
            @RequestParam("data") final String data,
            @RequestParam("feature") final MultipartFile feature,
            @RequestParam(value = "gallery", required = false) final List<MultipartFile> gallery,
            @RequestParam(value = "tax", required = false) final MultipartFile tax,
            @RequestParam("terms") final MultipartFile terms,
            @RequestParam(value = "other", required = false) final MultipartFile other) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Cause cause = mapper.readValue(data, Cause.class);
        if (this.causeService.causeExists(cause.getIdentifier())) {
            throw ServiceException.conflict("Cause {0} already exists in this system, Please try another name.", cause.getIdentifier());
        }

        this.commandGateway.process(new CreateCauseCommand(cause, feature, gallery, tax, terms, other));


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

        return ResponseEntity.ok(this.causeService.fetchCause((includeClosed != null ? includeClosed : Boolean.FALSE), param,
                this.createPageRequest(pageIndex, size, sortColumn, sortDirection)));
    }


//    @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.CAUSE)
//    @RequestMapping(
//            value = "/causes/filter",
//            method = RequestMethod.GET,
//            produces = MediaType.APPLICATION_JSON_VALUE,
//            consumes = MediaType.ALL_VALUE
//    )
//    public @ResponseBody
//    ResponseEntity<CausePage> fetchCausesByCategory(
//            @RequestParam(value = "category", required = false) final String category,
//            @RequestParam(value = "pageIndex", required = false) final Integer pageIndex,
//            @RequestParam(value = "size", required = false) final Integer size,
//            @RequestParam(value = "sortColumn", required = false) final String sortColumn,
//            @RequestParam(value = "sortDirection", required = false) final String sortDirection) {
//
//        return ResponseEntity.ok(this.causeService.fetchCauseByCategory(category, this.createPageRequest(pageIndex, size, sortColumn, sortDirection)));
//    }

    @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.PORTRAIT)
    @RequestMapping(
            value = "/causes/{identifier}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.ALL_VALUE
    )
    public @ResponseBody
    ResponseEntity<Void> deleteCause(@PathVariable("identifier") final String identifier) {
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


//    -------------find cause for ngo by the ngo username ----------------

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
            value = "/causes/{identifier}",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public @ResponseBody
    ResponseEntity<Void> updateCause(@PathVariable("identifier") final String identifier, @RequestBody final Cause cause) {
        if (this.causeService.causeExists(identifier)) {
            this.commandGateway.process(new UpdateCauseCommand(cause));
        } else {
            throw ServiceException.notFound("Cause {0} not found.", identifier);
        }
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
    public @ResponseBody
    ResponseEntity<Void> publishCause(@PathVariable("identifier") final String identifier) {
        Optional<CauseEntity> causeEntity = causeService.findCauseEntity(identifier);
        if (causeEntity.isPresent()) {
            if (causeEntity.get().getCurrentState().toLowerCase().equals(Cause.State.APPROVED.name().toLowerCase())) {
                this.commandGateway.process(new PublishCauseCommand(identifier));
            } else {
                throw ServiceException.conflict("Cause {0} not ACTIVE state. Currently the cause is in {1} state.", identifier, causeEntity.get().getCurrentState());
            }

        } else {
            throw ServiceException.notFound("Cause {0} not found.", identifier);
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
                    if (Cause.State.PENDING.name().equals(currentState)) {
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
                            || Cause.State.PENDING.name().equals(currentState)) {
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

            // if (this.causeService.causeRatingExists(identifier, rating.getCreatedBy())) {
            //     throw ServiceException.notFound("Already rating given for this Cause {0}.", identifier);
            // } else {
            this.commandGateway.process(new CreateRatingCommand(identifier, rating));
            //}

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
                        /*  final Stream<IdentificationCard> identificationCards = this.causeService.fetchIdentificationCardsByCause(identifier);*/
/*            if (!identificationCards.findAny().isPresent()) {
              throw ServiceException.conflict("No identification cards for cause found.");
            }*/
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

 /* @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.IDENTIFICATIONS)
  @RequestMapping(
          value = "/causes/{identifier}/identifications",
          method = RequestMethod.GET,
          produces = MediaType.APPLICATION_JSON_VALUE,
          consumes = MediaType.ALL_VALUE
  )
  public @ResponseBody ResponseEntity<List<IdentificationCard>> fetchIdentificationCards(@PathVariable("identifier") final String identifier) {
    this.throwIfCauseNotExists(identifier);
    return ResponseEntity.ok(this.causeService.fetchIdentificationCardsByCause(identifier).collect(Collectors.toList()));
  }*/

/*  @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.IDENTIFICATIONS)
  @RequestMapping(
          value = "/causes/{identifier}/identifications/{number}",
          method = RequestMethod.GET,
          produces = MediaType.APPLICATION_JSON_VALUE,
          consumes = MediaType.ALL_VALUE
  )
  public
  @ResponseBody
  ResponseEntity<IdentificationCard> findIdentificationCard(@PathVariable("identifier") final String identifier,
                                            @PathVariable("number") final String number) {
    this.throwIfCauseNotExists(identifier);

    final Optional<IdentificationCard> identificationCard = this.causeService.findIdentificationCard(number);
    if (identificationCard.isPresent()) {
      return ResponseEntity.ok(identificationCard.get());
    } else {
      throw ServiceException.notFound("Identification card {0} not found.", number);
    }
  }*/

 /* @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.IDENTIFICATIONS)
  @RequestMapping(
          value = "/causes/{identifier}/identifications",
          method = RequestMethod.POST,
          produces = MediaType.APPLICATION_JSON_VALUE,
          consumes = MediaType.APPLICATION_JSON_VALUE
  )
  public
  @ResponseBody
  ResponseEntity<Void> createIdentificationCard(@PathVariable("identifier") final String identifier,
                                @RequestBody @Valid final IdentificationCard identificationCard) {
    if (this.causeService.causeExists(identifier)) {
      if (this.causeService.identificationCardExists(identificationCard.getNumber())) {
        throw ServiceException.conflict("IdentificationCard {0} already exists.", identificationCard.getNumber());
      }

      this.commandGateway.process(new CreateIdentificationCardCommand(identifier, identificationCard));
    } else {
      throw ServiceException.notFound("Cause {0} not found.", identifier);
    }

    return ResponseEntity.accepted().build();
  }*/

/*  @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.IDENTIFICATIONS)
  @RequestMapping(
      value = "/causes/{identifier}/identifications/{number}",
      method = RequestMethod.PUT,
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE
  )
  public
  @ResponseBody
  ResponseEntity<Void> updateIdentificationCard(@PathVariable("identifier") final String identifier,
                                                @PathVariable("number") final String number,
                                                @RequestBody @Valid final IdentificationCard identificationCard) {
    this.throwIfCauseNotExists(identifier);
    this.throwIfIdentificationCardNotExists(number);

    if(!number.equals(identificationCard.getNumber())) {
      throw ServiceException.badRequest("Number in path is different from number in request body");
    }

    this.commandGateway.process(new UpdateIdentificationCardCommand(identifier, identificationCard.getNumber(), identificationCard));

    return ResponseEntity.accepted().build();
  }*/

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
/*
  @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.IDENTIFICATIONS)
  @RequestMapping(
          value = "/causes/{identifier}/identifications/{number}/scans",
          method = RequestMethod.GET,
          produces = MediaType.APPLICATION_JSON_VALUE,
          consumes = MediaType.ALL_VALUE
  )
  public
  @ResponseBody
  ResponseEntity<List<IdentificationCardScan>> fetchIdentificationCardScans(@PathVariable("identifier") final String identifier,
                                                                            @PathVariable("number") final String number) {
    this.throwIfCauseNotExists(identifier);
    this.throwIfIdentificationCardNotExists(number);

    final List<IdentificationCardScan> identificationCardScans = this.causeService.fetchScansByIdentificationCard(number);

    return ResponseEntity.ok(identificationCardScans);
  }

  @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.IDENTIFICATIONS)
  @RequestMapping(
          value = "/causes/{identifier}/identifications/{number}/scans/{scanIdentifier}",
          method = RequestMethod.GET,
          produces = MediaType.APPLICATION_JSON_VALUE,
          consumes = MediaType.ALL_VALUE
  )
  public
  @ResponseBody
  ResponseEntity<IdentificationCardScan> findIdentificationCardScan(@PathVariable("identifier") final String identifier,
                                                                     @PathVariable("number") final String number,
                                                                     @PathVariable("scanIdentifier") final String scanIdentifier) {
    this.throwIfCauseNotExists(identifier);
    this.throwIfIdentificationCardNotExists(number);

    final Optional<IdentificationCardScan> identificationCardScan = this.causeService.findIdentificationCardScan(number, scanIdentifier);

    return identificationCardScan
            .map(ResponseEntity::ok)
            .orElseThrow(() -> ServiceException.notFound("Identification card scan {0} not found.", number));
  }

  @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.IDENTIFICATIONS)
  @RequestMapping(
          value = "/causes/{identifier}/identifications/{number}/scans/{scanIdentifier}/image",
          method = RequestMethod.GET,
          produces = MediaType.APPLICATION_JSON_VALUE,
          consumes = MediaType.ALL_VALUE
  )
  public
  @ResponseBody
  ResponseEntity<byte[]> fetchIdentificationCardScanImage(@PathVariable("identifier") final String identifier,
                                          @PathVariable("number") final String number,
                                          @PathVariable("scanIdentifier") final String scanIdentifier) {
    this.throwIfCauseNotExists(identifier);
    this.throwIfIdentificationCardNotExists(number);
    this.throwIfIdentificationCardScanNotExists(number, scanIdentifier);

    final Optional<byte[]> image = this.causeService.findIdentificationCardScanImage(number, scanIdentifier);

    return image.map(ResponseEntity::ok)
            .orElseThrow(() -> ServiceException.notFound("Identification card scan {0} not found.", number));
  }

  @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.IDENTIFICATIONS)
  @RequestMapping(
          value = "/causes/{identifier}/identifications/{number}/scans",
          produces = MediaType.APPLICATION_JSON_VALUE,
          consumes = MediaType.MULTIPART_FORM_DATA_VALUE
  )
  public
  @ResponseBody
  ResponseEntity<Void> postIdentificationCardScan(@PathVariable("identifier") final String identifier,
                                  @PathVariable("number") final String number,
                                  @RequestParam("scanIdentifier") @ValidIdentifier final String scanIdentifier,
                                  @RequestParam("description") @Size(max = 4096) final String description,
                                  @RequestBody final MultipartFile image) throws Exception {
    this.throwIfCauseNotExists(identifier);
    this.throwIfIdentificationCardNotExists(number);
    this.throwIfInvalidSize(image.getSize());
    this.throwIfInvalidContentType(image.getContentType());

    if (this.causeService.identificationCardScanExists(number, scanIdentifier)) {
      throw ServiceException.conflict("Scan {0} already exists.", scanIdentifier);
    }

    final IdentificationCardScan scan = new IdentificationCardScan();
    scan.setIdentifier(scanIdentifier);
    scan.setDescription(description);

    this.commandGateway.process(new CreateIdentificationCardScanCommand(number, scan, image));

    return ResponseEntity.accepted().build();
  }

  @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.IDENTIFICATIONS)
  @RequestMapping(
          value = "/causes/{identifier}/identifications/{number}/scans/{scanIdentifier}",
          method = RequestMethod.DELETE,
          produces = MediaType.APPLICATION_JSON_VALUE,
          consumes = MediaType.ALL_VALUE
  )
  public
  @ResponseBody
  ResponseEntity<Void> deleteScan(@PathVariable("identifier") final String identifier,
                  @PathVariable("number") final String number,
                  @PathVariable("scanIdentifier") final String scanIdentifier) {
    throwIfCauseNotExists(identifier);
    throwIfIdentificationCardNotExists(number);

    this.commandGateway.process(new DeleteIdentificationCardScanCommand(number, scanIdentifier));

    return ResponseEntity.accepted().build();
  }*/

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

    private Pageable createPageRequest(final Integer pageIndex, final Integer size, final String sortColumn, final String sortDirection) {
        final Integer pageIndexToUse = pageIndex != null ? pageIndex : 0;
        final Integer sizeToUse = size != null ? size : 20;
        final String sortColumnToUse = sortColumn != null ? sortColumn : "identifier";
        final Sort.Direction direction = sortDirection != null ? Sort.Direction.valueOf(sortDirection.toUpperCase()) : Sort.Direction.ASC;
        return new PageRequest(pageIndexToUse, sizeToUse, direction, sortColumnToUse);
    }

    private void throwIfCauseNotExists(final String identifier) {
        if (!this.causeService.causeExists(identifier)) {
            throw ServiceException.notFound("Cause {0} not found.", identifier);
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
