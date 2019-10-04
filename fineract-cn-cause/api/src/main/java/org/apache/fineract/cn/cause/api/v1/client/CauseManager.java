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
package org.apache.fineract.cn.cause.api.v1.client;

import org.apache.fineract.cn.cause.api.v1.domain.*;
import org.apache.fineract.cn.cause.api.v1.domain.*;
import org.apache.fineract.cn.api.annotation.ThrowsException;
import org.apache.fineract.cn.api.annotation.ThrowsExceptions;
import org.apache.fineract.cn.cause.api.v1.config.CauseFeignClientConfig;
import org.apache.fineract.cn.cause.api.v1.domain.*;
import org.apache.fineract.cn.lang.validation.constraints.ValidIdentifier;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Size;
import java.util.List;

@SuppressWarnings("unused")
@FeignClient(name = "cause-v1", path = "/cause-v1", configuration = CauseFeignClientConfig.class)
public interface CauseManager {

    @RequestMapping(
            value = "/causes",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    @ThrowsExceptions({
            @ThrowsException(status = HttpStatus.CONFLICT, exception = CauseAlreadyExistsException.class),
            @ThrowsException(status = HttpStatus.BAD_REQUEST, exception = CauseValidationException.class)
    })
    void createCause(@RequestBody final Cause cause);

    @RequestMapping(
            value = "/causes",
            method = RequestMethod.GET,
            produces = MediaType.ALL_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    CausePage fetchCauses(@RequestParam(value = "term", required = false) final String term,
                          @RequestParam(value = "includeClosed", required = false) final Boolean includeClosed,
                          @RequestParam(value = "onlyActive", required = false) final Boolean onlyActive,
                          @RequestParam(value = "pageIndex", required = false) final Integer pageIndex,
                          @RequestParam(value = "size", required = false) final Integer size,
                          @RequestParam(value = "sortColumn", required = false) final String sortColumn,
                          @RequestParam(value = "sortDirection", required = false) final String sortDirection);


//    @RequestMapping(value = "/causes/list",
//            method = RequestMethod.GET,
//            produces = MediaType.ALL_VALUE,
//            consumes = MediaType.APPLICATION_JSON_VALUE
//    )
//    List<Cause> fetchCauseList();


    @RequestMapping(value = "/causes/CompleteOnHardCapReach",
            method = RequestMethod.GET,
            produces = MediaType.ALL_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    void causeCompleteOnHardCapReach();


    @RequestMapping(value = "/causes/expired",
            method = RequestMethod.PUT,
            produces = MediaType.ALL_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    void expiredCause();

    @RequestMapping(
            value = "/causes/{identifier}",
            method = RequestMethod.GET,
            produces = MediaType.ALL_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    @ThrowsException(status = HttpStatus.NOT_FOUND, exception = CauseNotFoundException.class)
    Cause findCause(@PathVariable("identifier") final String identifier);

    default boolean isCauseInGoodStanding(final String causeIdentifier) {
        final Cause cause;
        try {
            cause = this.findCause(causeIdentifier);
        } catch (CauseNotFoundException e) {
            return false;
        }
        final Cause.State state = Cause.State.valueOf(cause.getCurrentState());
        return (state == Cause.State.ACTIVE);
    }

    @RequestMapping(
            value = "/causes/{identifier}",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    @ThrowsExceptions({
            @ThrowsException(status = HttpStatus.NOT_FOUND, exception = CauseNotFoundException.class),
            @ThrowsException(status = HttpStatus.BAD_REQUEST, exception = CauseValidationException.class)
    })
    void updateCause(@PathVariable("identifier") final String identifier, @RequestBody final Cause cause);

    @RequestMapping(
            value = "/causes/{identifier}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    @ThrowsExceptions({
            @ThrowsException(status = HttpStatus.NOT_FOUND, exception = CauseNotFoundException.class),
            @ThrowsException(status = HttpStatus.BAD_REQUEST, exception = CauseValidationException.class)
    })
    void deleteCause(@PathVariable("identifier") final String identifier);

    @RequestMapping(
            value = "/causes/{identifier}/commands",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    @ThrowsExceptions({
            @ThrowsException(status = HttpStatus.NOT_FOUND, exception = CauseNotFoundException.class),
            @ThrowsException(status = HttpStatus.BAD_REQUEST, exception = CommandExecutionException.class)
    })
    void causeCommand(@PathVariable("identifier") final String identifier, @RequestBody final Command command);

    @RequestMapping(
            value = "/causes/{identifier}/commands",
            method = RequestMethod.GET,
            produces = MediaType.ALL_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    @ThrowsException(status = HttpStatus.NOT_FOUND, exception = CauseNotFoundException.class)
    List<Command> fetchCauseCommands(@PathVariable("identifier") final String identifier);

    @RequestMapping(
            value = "/causes/{identifier}/ratings",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    @ThrowsExceptions({
            @ThrowsException(status = HttpStatus.NOT_FOUND, exception = CauseNotFoundException.class),
            @ThrowsException(status = HttpStatus.BAD_REQUEST, exception = RatingValidationException.class)
    })
    void causeRating(@PathVariable("identifier") final String identifier, @RequestBody final CauseRating rating);


    @RequestMapping(
            value = "/causes/{identifier}/ratings",
            method = RequestMethod.GET,
            produces = MediaType.ALL_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    @ThrowsException(status = HttpStatus.NOT_FOUND, exception = CauseNotFoundException.class)
    List<Command> fetchCauseRatings(@PathVariable("identifier") final String identifier);

    @RequestMapping(
            value = "/causes/{identifier}/tasks/{taskIdentifier}",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    @ThrowsExceptions({
            @ThrowsException(status = HttpStatus.NOT_FOUND, exception = CauseNotFoundException.class),
            @ThrowsException(status = HttpStatus.CONFLICT, exception = TaskAlreadyExistsException.class),
            @ThrowsException(status = HttpStatus.BAD_REQUEST, exception = TaskValidationException.class)
    })
    void addTaskToCause(@PathVariable("identifier") final String identifier,
                        @PathVariable("taskIdentifier") final String taskIdentifier);

    @RequestMapping(
            value = "/causes/{identifier}/tasks/{taskIdentifier}",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    @ThrowsExceptions({
            @ThrowsException(status = HttpStatus.NOT_FOUND, exception = CauseNotFoundException.class),
            @ThrowsException(status = HttpStatus.CONFLICT, exception = TaskExecutionException.class)
    })
    void taskForCauseExecuted(@PathVariable("identifier") final String identifier,
                              @PathVariable("taskIdentifier") final String taskIdentifier);

    @RequestMapping(
            value = "/causes/{identifier}/tasks",
            method = RequestMethod.GET,
            produces = MediaType.ALL_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    @ThrowsException(status = HttpStatus.NOT_FOUND, exception = TaskNotFoundException.class)
    List<TaskDefinition> findTasksForCause(@PathVariable("identifier") final String identifier,
                                           @RequestParam(value = "includeExecuted", required = false) final Boolean includeExecuted);

    @RequestMapping(
            value = "/causes/{identifier}/address",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    @ThrowsExceptions({
            @ThrowsException(status = HttpStatus.NOT_FOUND, exception = CauseNotFoundException.class),
            @ThrowsException(status = HttpStatus.BAD_REQUEST, exception = AddressValidationException.class)
    })
    void putAddress(@PathVariable("identifier") final String identifier, @RequestBody final Address address);

    @RequestMapping(
            value = "/causes/{identifier}/contact",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    @ThrowsExceptions({
            @ThrowsException(status = HttpStatus.NOT_FOUND, exception = CauseNotFoundException.class),
            @ThrowsException(status = HttpStatus.BAD_REQUEST, exception = ContactDetailValidationException.class)
    })
    void putContactDetails(@PathVariable("identifier") final String identifier,
                           @RequestBody final List<ContactDetail> contactDetails);

    @RequestMapping(
            value = "/causes/{identifier}/identifications",
            method = RequestMethod.GET,
            produces = MediaType.ALL_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    @ThrowsExceptions({
            @ThrowsException(status = HttpStatus.NOT_FOUND, exception = CauseNotFoundException.class)
    })
    List<IdentificationCard> fetchIdentificationCards(@PathVariable("identifier") final String identifier);

    @RequestMapping(
            value = "/causes/{identifier}/identifications/{number}",
            method = RequestMethod.GET,
            produces = MediaType.ALL_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    @ThrowsException(status = HttpStatus.NOT_FOUND, exception = IdentificationCardNotFoundException.class)
    IdentificationCard findIdentificationCard(@PathVariable("identifier") final String identifier,
                                              @PathVariable("number") final String number);

    @RequestMapping(
            value = "/causes/{identifier}/identifications",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    @ThrowsExceptions({
            @ThrowsException(status = HttpStatus.NOT_FOUND, exception = CauseNotFoundException.class),
            @ThrowsException(status = HttpStatus.BAD_REQUEST, exception = IdentificationCardValidationException.class)
    })
    void createIdentificationCard(@PathVariable("identifier") final String identifier,
                                  @RequestBody final IdentificationCard identificationCard);

    @RequestMapping(
            value = "/causes/{identifier}/identifications/{number}",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    @ThrowsExceptions({
            @ThrowsException(status = HttpStatus.NOT_FOUND, exception = IdentificationCardNotFoundException.class),
            @ThrowsException(status = HttpStatus.BAD_REQUEST, exception = IdentificationCardValidationException.class)
    })
    void updateIdentificationCard(@PathVariable("identifier") final String identifier,
                                  @PathVariable("number") final String number,
                                  @RequestBody final IdentificationCard identificationCard);

    @RequestMapping(
            value = "/causes/{identifier}/identifications/{number}",
            method = RequestMethod.DELETE,
            produces = MediaType.ALL_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    @ThrowsExceptions({
            @ThrowsException(status = HttpStatus.NOT_FOUND, exception = CauseNotFoundException.class)
    })
    void deleteIdentificationCard(@PathVariable("identifier") final String identifier,
                                  @PathVariable("number") final String number);

    @RequestMapping(
            value = "/causes/{identifier}/identifications/{number}/scans",
            method = RequestMethod.GET,
            produces = MediaType.ALL_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    @ThrowsExceptions({
            @ThrowsException(status = HttpStatus.NOT_FOUND, exception = IdentificationCardNotFoundException.class)
    })
    List<IdentificationCardScan> fetchIdentificationCardScans(@PathVariable("identifier") final String identifier,
                                                              @PathVariable("number") final String number);

    @RequestMapping(
            value = "/causes/{identifier}/identifications/{number}/scans/{scanIdentifier}",
            method = RequestMethod.GET,
            produces = MediaType.ALL_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    @ThrowsExceptions({
            @ThrowsException(status = HttpStatus.NOT_FOUND, exception = ScanNotFoundException.class)
    })
    IdentificationCardScan findIdentificationCardScan(@PathVariable("identifier") final String identifier,
                                                      @PathVariable("number") final String number,
                                                      @PathVariable("scanIdentifier") final String scanIdentifier);

    @RequestMapping(
            value = "/causes/{identifier}/identifications/{number}/scans/{scanIdentifier}/image",
            method = RequestMethod.GET,
            produces = MediaType.ALL_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    @ThrowsExceptions({
            @ThrowsException(status = HttpStatus.NOT_FOUND, exception = ScanNotFoundException.class)
    })
    byte[] fetchIdentificationCardScanImage(@PathVariable("identifier") final String identifier,
                                            @PathVariable("number") final String number,
                                            @PathVariable("scanIdentifier") final String scanIdentifier);

    @RequestMapping(
            value = "/causes/{identifier}/identifications/{number}/scans",
            method = RequestMethod.POST,
            produces = MediaType.ALL_VALUE,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @ThrowsExceptions({
            @ThrowsException(status = HttpStatus.NOT_FOUND, exception = IdentificationCardNotFoundException.class),
            @ThrowsException(status = HttpStatus.BAD_REQUEST, exception = ScanValidationException.class),
            @ThrowsException(status = HttpStatus.CONFLICT, exception = ScanAlreadyExistsException.class)
    })
    void postIdentificationCardScan(@PathVariable("identifier") final String identifier,
                                    @PathVariable("number") final String number,
                                    @RequestParam("scanIdentifier") @ValidIdentifier final String scanIdentifier,
                                    @RequestParam("description") @Size(max = 4096) final String description,
                                    @RequestBody final MultipartFile image);

    @RequestMapping(
            value = "/causes/{identifier}/identifications/{number}/scans/{scanIdentifier}",
            method = RequestMethod.DELETE,
            produces = MediaType.ALL_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    void deleteScan(@PathVariable("identifier") final String identifier,
                    @PathVariable("number") final String number,
                    @PathVariable("scanIdentifier") final String scanIdentifier);

    @RequestMapping(
            value = "/causes/{identifier}/portrait",
            method = RequestMethod.GET,
            produces = MediaType.ALL_VALUE
    )
    @ThrowsExceptions({
            @ThrowsException(status = HttpStatus.NOT_FOUND, exception = PortraitNotFoundException.class),
    })
    byte[] getPortrait(@PathVariable("identifier") final String identifier);

    @RequestMapping(
            value = "/causes/{identifier}/portrait",
            method = RequestMethod.POST,
            produces = MediaType.ALL_VALUE,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @ThrowsExceptions({
            @ThrowsException(status = HttpStatus.NOT_FOUND, exception = CauseNotFoundException.class),
            @ThrowsException(status = HttpStatus.BAD_REQUEST, exception = DocumentValidationException.class),
    })
    void postPortrait(@PathVariable("identifier") final String identifier,
                      @RequestBody final MultipartFile portrait);

    @RequestMapping(
            value = "/causes/{identifier}/portrait",
            method = RequestMethod.DELETE,
            produces = MediaType.ALL_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    void deletePortrait(@PathVariable("identifier") final String identifier);

    @RequestMapping(
            value = "/tasks",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    @ThrowsExceptions({
            @ThrowsException(status = HttpStatus.CONFLICT, exception = TaskAlreadyExistsException.class),
            @ThrowsException(status = HttpStatus.BAD_REQUEST, exception = TaskValidationException.class)
    })
    void createTask(@RequestBody final TaskDefinition taskDefinition);

    @RequestMapping(
            value = "/tasks",
            method = RequestMethod.GET,
            produces = MediaType.ALL_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    List<TaskDefinition> fetchAllTasks();

    @RequestMapping(
            value = "/tasks/{identifier}",
            method = RequestMethod.GET,
            produces = MediaType.ALL_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    @ThrowsException(status = HttpStatus.NOT_FOUND, exception = TaskNotFoundException.class)
    TaskDefinition findTask(@PathVariable("identifier") final String identifier);

    @RequestMapping(
            value = "/tasks/{identifier}",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    @ThrowsExceptions({
            @ThrowsException(status = HttpStatus.NOT_FOUND, exception = TaskNotFoundException.class),
            @ThrowsException(status = HttpStatus.BAD_REQUEST, exception = TaskValidationException.class)
    })
    void updateTask(@PathVariable("identifier") final String identifier, @RequestBody final TaskDefinition taskDefinition);

    @RequestMapping(
            value = "/causes/{identifier}/actions",
            method = RequestMethod.GET,
            produces = MediaType.ALL_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    @ThrowsException(status = HttpStatus.NOT_FOUND, exception = CauseNotFoundException.class)
    List<ProcessStep> fetchProcessSteps(@PathVariable(value = "identifier") final String causeIdentifier);
}
