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
import org.apache.fineract.cn.cause.internal.command.ChangeDocumentCommand;
import org.apache.fineract.cn.cause.internal.command.CompleteDocumentCommand;
import org.apache.fineract.cn.cause.internal.command.DeleteDocumentCommand;
import org.apache.fineract.cn.cause.internal.service.CauseService;
import org.apache.fineract.cn.cause.internal.service.DocumentService;
import org.apache.fineract.cn.command.gateway.CommandGateway;
import org.apache.fineract.cn.lang.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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


//    post cause document in uploaded state
//
//    @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.DOCUMENTS)
//    @RequestMapping(
//            value = "/upload",
//            method = RequestMethod.POST,
//            produces = MediaType.APPLICATION_JSON_VALUE,
//            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
//    )
//    public @ResponseBody
//    ResponseEntity<Void> postCauseDocument(
//            @PathVariable("causeidentifier") final String causeIdentifier,
//            @RequestParam("docType") final String docType,
//            @RequestParam("doc") final MultipartFile doc) {
//        throwIfCauseNotExists(causeIdentifier);
//        this.commandGateway.process(new CreateCauseDocumentCommand(docType, doc, causeIdentifier));
//        return ResponseEntity.accepted().build();
//    }


//    @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.DOCUMENTS)
//    @RequestMapping(
//            value = "/{pageId}",
//            method = RequestMethod.DELETE,
//            produces = MediaType.APPLICATION_JSON_VALUE,
//            consumes = MediaType.APPLICATION_JSON_VALUE
//    )
//    public @ResponseBody
//    ResponseEntity<Void> deleteCauseDocument(@PathVariable("causeidentifier") final String causeIdentifier,
//                                             @PathVariable("pageId") final Long pageId) {
//        throwIfCauseNotExists(causeIdentifier);
//        throwIfCauseDocumentPageNotExists(causeIdentifier, pageId);
//        this.commandGateway.process(new DeleteCauseDocumentCommand(pageId, causeIdentifier));
//        return ResponseEntity.accepted().build();
//    }


//    @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.CAUSE)
//    @RequestMapping(
//            value = "/inactive",
//            method = RequestMethod.GET,
//            produces = MediaType.APPLICATION_JSON_VALUE,
//            consumes = MediaType.ALL_VALUE
//    )
//    public
//    @ResponseBody
//    ResponseEntity<List<CauseDocumentPage>> fetchCauses(@PathVariable("causeidentifier") final String causeidentifier) {
//        return ResponseEntity.ok(this.causeService.causeDocumentPages(causeidentifier));
//    }


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

//        commandGateway.process(new CreateDocumentCommand(causeIdentifier, instance));

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


//    @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.DOCUMENTS)
//    @RequestMapping(
//            value = "/{documentidentifier}/file",
//            method = RequestMethod.GET,
//            produces = MediaType.APPLICATION_JSON_VALUE,
//            consumes = MediaType.ALL_VALUE
//    )
//    public ResponseEntity<byte[]> getDocumentPage(
//            @PathVariable("causeidentifier") final String causeIdentifier,
//            @PathVariable("documentidentifier") final Long documentId) {
//
//        final DocumentPageEntity documentPageEntity = documentService.findPage(documentId)
//                .orElseThrow(() -> ServiceException.notFound("Page ''{0}'' of document id ''{1}'' for cause ''{2}'' not found.", documentId, causeIdentifier));
//
//        return ResponseEntity
//                .ok()
//                .contentType(MediaType.parseMediaType(documentPageEntity.getContentType()))
//                .contentLength(documentPageEntity.getImage().length)
//                .body(documentPageEntity.getImage());
//    }

//    @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.DOCUMENTS)
//    @RequestMapping(method = RequestMethod.POST,
//            produces = MediaType.APPLICATION_JSON_VALUE,
//            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
//    )
//    public @ResponseBody
//    ResponseEntity<Void> createCauseDocuments(
//            @PathVariable("causeidentifier") final String causeIdentifier,
//            @RequestParam("data") final String data,
//            @RequestParam("feature") final MultipartFile feature,
//            @RequestParam("gallery") final List<MultipartFile> gallery,
//            @RequestParam("tax") final MultipartFile tax,
//            @RequestParam("terms") final MultipartFile terms,
//            @RequestParam("other") final MultipartFile other) throws IOException {
//        ObjectMapper mapper = new ObjectMapper();
//        Cause cause = mapper.readValue(data, Cause.class);
//        commandGateway.process(new CreateDocumentCommand(causeIdentifier, cause, feature, gallery, tax, terms, other));
//        return ResponseEntity.accepted().build();
//    }


    private void throwIfCauseNotExists(final String causeIdentifier) {
        if (!this.causeService.causeExists(causeIdentifier)) {
//            System.out.println("Cause not found from here------------------------->");
            throw ServiceException.notFound("Cause ''{0}'' not found.", causeIdentifier);
        }
    }

    private void throwIfCauseDocumentNotExists(final String causeIdentifier, final String documentIdentifier) {
        if (!this.documentService.documentExists(causeIdentifier, documentIdentifier)) {
            throw ServiceException.notFound("Cause ''{0}'' not found.", causeIdentifier);
        }
    }

    private void throwIfCauseDocumentPageNotExists(final String causeIdentifier, final Long documentId) {
        if (!this.documentService.documentPageExists(documentId)) {
            throw ServiceException.notFound("Cause ''{0}'' with document ''{1}'' not found.", causeIdentifier, documentId);
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
//        if (documentService.isDocumentMissingPages(causeIdentifier, documentIdentifier))
//            throw ServiceException.badRequest("The document ''{0}'' for cause ''{1}'' is missing pages.",
//                    documentIdentifier, causeIdentifier);
    }
}
