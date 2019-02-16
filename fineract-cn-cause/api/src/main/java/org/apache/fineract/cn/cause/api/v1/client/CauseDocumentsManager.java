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

import org.apache.fineract.cn.cause.api.v1.domain.CauseDocument;
import org.apache.fineract.cn.api.annotation.ThrowsException;
import org.apache.fineract.cn.api.annotation.ThrowsExceptions;
import org.apache.fineract.cn.cause.api.v1.config.CauseFeignClientConfig;
import org.hibernate.validator.constraints.Range;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author Myrle Krantz
 */
@FeignClient(name = "cause-v1", path = "/cause/v1", configuration = CauseFeignClientConfig.class)
public interface CauseDocumentsManager {

    @RequestMapping(
            value = "/causes/{causeidentifier}/documents",
            method = RequestMethod.GET,
            produces = MediaType.ALL_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    List<CauseDocument> getDocuments(
            @PathVariable("causeidentifier") final String causeIdentifier);


    @RequestMapping(
            value = "/causes/{causeidentifier}/documents/{documentidentifier}",
            method = RequestMethod.GET,
            produces = MediaType.ALL_VALUE
    )
    CauseDocument getDocument(
            @PathVariable("causeidentifier") final String causeIdentifier,
            @PathVariable("documentidentifier") final String documentIdentifier);


    @RequestMapping(
            value = "/causes/{causeidentifier}/documents/{documentidentifier}",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    @ThrowsExceptions({
            @ThrowsException(status = HttpStatus.BAD_REQUEST, exception = DocumentValidationException.class)
    })
    void createDocument(
            @PathVariable("causeidentifier") final String causeIdentifier,
            @PathVariable("documentidentifier") final String documentIdentifier,
            @RequestBody final CauseDocument causeDocument);


    @RequestMapping(
            value = "/causes/{causeidentifier}/documents/{documentidentifier}",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    @ThrowsExceptions({
            @ThrowsException(status = HttpStatus.CONFLICT, exception = CompletedDocumentCannotBeChangedException.class),
            @ThrowsException(status = HttpStatus.BAD_REQUEST, exception = DocumentValidationException.class)
    })
    void changeDocument(
            @PathVariable("causeidentifier") final String causeIdentifier,
            @PathVariable("documentidentifier") final String documentIdentifier,
            @RequestBody final CauseDocument causeDocument);


    @RequestMapping(
            value = "/causes/{causeidentifier}/documents/{documentidentifier}",
            method = RequestMethod.DELETE,
            produces = MediaType.ALL_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    @ThrowsExceptions({
            @ThrowsException(status = HttpStatus.CONFLICT, exception = CompletedDocumentCannotBeChangedException.class)
    })
    void deleteDocument(
            @PathVariable("causeidentifier") final String causeIdentifier,
            @PathVariable("documentidentifier") final String documentIdentifier);


    /**
     * Once a document is "completed" its name and images cannot be changed again.  Only completed
     * documents should be referenced by other services.
     *
     * @param completed once this is set to true it cannot be changed back again.
     */
    @RequestMapping(
            value = "/causes/{causeidentifier}/documents/{documentidentifier}/completed",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    @ThrowsExceptions({
            @ThrowsException(status = HttpStatus.CONFLICT, exception = CompletedDocumentCannotBeChangedException.class),
            @ThrowsException(status = HttpStatus.BAD_REQUEST, exception = DocumentValidationException.class),
    })
    void completeDocument(
            @PathVariable("causeidentifier") final String causeIdentifier,
            @PathVariable("documentidentifier") final String documentIdentifier,
            @RequestBody final Boolean completed);


    @RequestMapping(
            value = "/causes/{causeidentifier}/documents/{documentidentifier}/pages",
            method = RequestMethod.GET,
            produces = MediaType.ALL_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    List<Integer> getDocumentPageNumbers(
            @PathVariable("causeidentifier") final String causeIdentifier,
            @PathVariable("documentidentifier") final String documentIdentifier);


    @RequestMapping(
            value = "/causes/{causeidentifier}/documents/{documentidentifier}/pages/{pagenumber}",
            method = RequestMethod.GET,
            produces = MediaType.ALL_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    byte[] getDocumentPage(
            @PathVariable("causeidentifier") final String causeIdentifier,
            @PathVariable("documentidentifier") final String documentIdentifier,
            @PathVariable("pagenumber") final Integer pageNumber);


    @RequestMapping(
            value = "/causes/{causeidentifier}/documents/{documentidentifier}/pages/{pagenumber}",
            method = RequestMethod.POST,
            produces = MediaType.ALL_VALUE,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @ThrowsExceptions({
            @ThrowsException(status = HttpStatus.CONFLICT, exception = CompletedDocumentCannotBeChangedException.class),
            @ThrowsException(status = HttpStatus.BAD_REQUEST, exception = DocumentValidationException.class),
    })
    void createDocumentPage(
            @PathVariable("causeidentifier") final String causeIdentifier,
            @PathVariable("documentidentifier") final String documentIdentifier,
            @PathVariable("pagenumber") @Range(min = 0) final Integer pageNumber,
            @RequestBody final MultipartFile page);


    @RequestMapping(
            value = "/causes/{causeidentifier}/documents/{documentidentifier}/pages/{pagenumber}",
            method = RequestMethod.DELETE,
            produces = MediaType.ALL_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    @ThrowsExceptions({
            @ThrowsException(status = HttpStatus.CONFLICT, exception = CompletedDocumentCannotBeChangedException.class)
    })
    void deleteDocumentPage(
            @PathVariable("causeidentifier") final String causeIdentifier,
            @PathVariable("documentidentifier") final String documentIdentifier,
            @PathVariable("pagenumber") @Range(min = 0) final Integer pageNumber);
}