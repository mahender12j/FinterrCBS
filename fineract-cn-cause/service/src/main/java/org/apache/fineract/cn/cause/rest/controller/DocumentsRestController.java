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
import org.apache.fineract.cn.cause.api.v1.PermittableGroupIds;

import org.apache.fineract.cn.cause.api.v1.domain.CauseDocument;
import org.apache.fineract.cn.cause.internal.command.*;
import org.apache.fineract.cn.cause.internal.repository.DocumentPageEntity;
import org.apache.fineract.cn.cause.internal.service.CauseService;
import org.apache.fineract.cn.cause.internal.service.DocumentService;
import org.apache.fineract.cn.command.gateway.CommandGateway;
import org.apache.fineract.cn.lang.ServiceException;
import org.hibernate.validator.constraints.Range;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Myrle Krantz
 */
@RestController
@RequestMapping("/causes/{causeidentifier}/documents")
public class DocumentsRestController {
    private final CommandGateway commandGateway;
    private final CauseService causeService;
    private final DocumentService documentService;

    @Autowired
    public DocumentsRestController(
            final CommandGateway commandGateway,
            final CauseService causeService,
            final DocumentService documentService) {
        this.commandGateway = commandGateway;
        this.causeService = causeService;
        this.documentService = documentService;
    }

    @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.DOCUMENTS)
    @RequestMapping(
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.ALL_VALUE
    )
    public ResponseEntity<List<CauseDocument>> getDocuments(
            @PathVariable("causeidentifier") final String causeIdentifier) {
        throwIfCauseNotExists(causeIdentifier);

        return ResponseEntity.ok(documentService.find(causeIdentifier).collect(Collectors.toList()));
    }


    @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.DOCUMENTS)
    @RequestMapping(
            value = "/{documentidentifier}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.ALL_VALUE
    )
    public ResponseEntity<CauseDocument> getDocument(
            @PathVariable("causeidentifier") final String causeIdentifier,
            @PathVariable("documentidentifier") final String documentIdentifier) {
        return ResponseEntity
                .ok(documentService.findDocument(causeIdentifier, documentIdentifier)
                        .orElseThrow(() -> ServiceException.notFound("Document ''{0}'' for cause ''{1}'' not found.",
                                documentIdentifier, causeIdentifier)));
    }


    @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.DOCUMENTS)
    @RequestMapping(
            value = "/{documentidentifier}",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public @ResponseBody
    ResponseEntity<Void> createDocument(
            @PathVariable("causeidentifier") final String causeIdentifier,
            @PathVariable("documentidentifier") final String documentIdentifier,
            @RequestBody final @Valid CauseDocument instance) {
        throwIfCauseNotExists(causeIdentifier);

        if (!instance.getIdentifier().equals(documentIdentifier))
            throw ServiceException.badRequest("Document identifier in request body must match document identifier in request path.");

        commandGateway.process(new CreateDocumentCommand(causeIdentifier, instance));

        return ResponseEntity.accepted().build();
    }


    @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.DOCUMENTS)
    @RequestMapping(
            value = "/{documentidentifier}",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public @ResponseBody
    ResponseEntity<Void> changeDocument(
            @PathVariable("causeidentifier") final String causeIdentifier,
            @PathVariable("documentidentifier") final String documentIdentifier,
            @RequestBody final @Valid CauseDocument instance) {
        throwIfCauseNotExists(causeIdentifier);
        throwIfCauseDocumentNotExists(causeIdentifier, documentIdentifier);

        throwIfDocumentCompleted(causeIdentifier, documentIdentifier);

        if (!instance.getIdentifier().equals(documentIdentifier))
            throw ServiceException.badRequest("Document identifier in request body must match document identifier in request path.");

        commandGateway.process(new ChangeDocumentCommand(causeIdentifier, instance));

        return ResponseEntity.accepted().build();
    }


    @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.DOCUMENTS)
    @RequestMapping(
            value = "/{documentidentifier}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.ALL_VALUE
    )
    public @ResponseBody
    ResponseEntity<Void> deleteDocument(
            @PathVariable("causeidentifier") final String causeIdentifier,
            @PathVariable("documentidentifier") final String documentIdentifier) {
        throwIfCauseNotExists(causeIdentifier);
        throwIfCauseDocumentNotExists(causeIdentifier, documentIdentifier);

        throwIfDocumentCompleted(causeIdentifier, documentIdentifier);

        commandGateway.process(new DeleteDocumentCommand(causeIdentifier, documentIdentifier));

        return ResponseEntity.accepted().build();
    }


    @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.DOCUMENTS)
    @RequestMapping(
            value = "/{documentidentifier}/completed",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public @ResponseBody
    ResponseEntity<Void> completeDocument(
            @PathVariable("causeidentifier") final String causeIdentifier,
            @PathVariable("documentidentifier") final String documentIdentifier,
            @RequestBody final @Valid Boolean completed) {
        throwIfCauseDocumentNotExists(causeIdentifier, documentIdentifier);

        if (!completed)
            throwIfDocumentCompleted(causeIdentifier, documentIdentifier);

        throwIfPagesMissing(causeIdentifier, documentIdentifier);

        if (completed)
            commandGateway.process(new CompleteDocumentCommand(causeIdentifier, documentIdentifier));

        return ResponseEntity.accepted().build();
    }


    @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.DOCUMENTS)
    @RequestMapping(
            value = "/{documentidentifier}/pages",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.ALL_VALUE
    )
    public @ResponseBody
    ResponseEntity<List<Integer>> getDocumentPageNumbers(
            @PathVariable("causeidentifier") final String causeIdentifier,
            @PathVariable("documentidentifier") final String documentIdentifier) {
        throwIfCauseDocumentNotExists(causeIdentifier, documentIdentifier);

        return ResponseEntity.ok(documentService.findPageNumbers(causeIdentifier, documentIdentifier).collect(Collectors.toList()));
    }


    @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.DOCUMENTS)
    @RequestMapping(
            value = "/{documentidentifier}/pages/{pagenumber}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.ALL_VALUE
    )
    public ResponseEntity<byte[]> getDocumentPage(
            @PathVariable("causeidentifier") final String causeIdentifier,
            @PathVariable("documentidentifier") final String documentIdentifier,
            @PathVariable("pagenumber") final Integer pageNumber) {
        final DocumentPageEntity documentPageEntity = documentService.findPage(causeIdentifier, documentIdentifier, pageNumber)
                .orElseThrow(() -> ServiceException.notFound("Page ''{0}'' of document ''{1}'' for cause ''{2}'' not found.",
                        pageNumber, documentIdentifier, causeIdentifier));

        return ResponseEntity
                .ok()
                .contentType(MediaType.parseMediaType(documentPageEntity.getContentType()))
                .contentLength(documentPageEntity.getImage().length)
                .body(documentPageEntity.getImage());
    }


    @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.DOCUMENTS)
    @RequestMapping(
            value = "/{documentidentifier}/pages/{pagenumber}",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public @ResponseBody
    ResponseEntity<Void> createDocumentPage(
            @PathVariable("causeidentifier") final String causeIdentifier,
            @PathVariable("documentidentifier") final String documentIdentifier,
            @PathVariable("pagenumber") @Range(min = 0) final Integer pageNumber,
            @RequestBody final MultipartFile page) {
        if (page == null) {
            throw ServiceException.badRequest("Document not found");
        }

        throwIfCauseNotExists(causeIdentifier);
        throwIfDocumentCompleted(causeIdentifier, documentIdentifier);
        throwIfInvalidContentType(page.getContentType());

        commandGateway.process(new CreateDocumentPageCommand(causeIdentifier, documentIdentifier, pageNumber, page));

        return ResponseEntity.accepted().build();
    }

    @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.DOCUMENTS)
    @RequestMapping(
            value = "/{documentidentifier}/pages/{pagenumber}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.ALL_VALUE
    )
    public @ResponseBody
    ResponseEntity<Void> deleteDocumentPage(
            @PathVariable("causeidentifier") final String causeIdentifier,
            @PathVariable("documentidentifier") final String documentIdentifier,
            @PathVariable("pagenumber") final Integer pageNumber) {
        throwIfCauseDocumentNotExists(causeIdentifier, documentIdentifier);

        throwIfDocumentCompleted(causeIdentifier, documentIdentifier);

        commandGateway.process(new DeleteDocumentPageCommand(causeIdentifier, documentIdentifier, pageNumber));

        return ResponseEntity.accepted().build();
    }

    private void throwIfCauseNotExists(final String causeIdentifier) {
        if (!this.causeService.causeExists(causeIdentifier)) {
            throw ServiceException.notFound("Cause ''{0}'' not found.", causeIdentifier);
        }
    }

    private void throwIfCauseDocumentNotExists(final String causeIdentifier, final String documentIdentifier) {
        if (!this.documentService.documentExists(causeIdentifier, documentIdentifier)) {
            throw ServiceException.notFound("Cause ''{0}'' not found.", causeIdentifier);
        }
    }

    private void throwIfInvalidContentType(final String contentType) {
        // if (!contentType.contains(MediaType.IMAGE_JPEG_VALUE)
        //         && !contentType.contains(MediaType.IMAGE_PNG_VALUE)) {
        //     throw ServiceException.badRequest("Image has contentType ''{0}'', but only content types ''{1}'' and ''{2}'' allowed.",
        //             contentType, MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE);
        // }
    }

    private void throwIfDocumentCompleted(final String causeIdentifier, final String documentIdentifier) {
        if (documentService.isDocumentCompleted(causeIdentifier, documentIdentifier))
            throw ServiceException.conflict("The document ''{0}'' for cause ''{1}'' is completed and cannot be uncompleted.",
                    documentIdentifier, causeIdentifier);
    }

    private void throwIfPagesMissing(final String causeIdentifier, final String documentIdentifier) {
        if (documentService.isDocumentMissingPages(causeIdentifier, documentIdentifier))
            throw ServiceException.badRequest("The document ''{0}'' for cause ''{1}'' is missing pages.",
                    documentIdentifier, causeIdentifier);
    }
}
