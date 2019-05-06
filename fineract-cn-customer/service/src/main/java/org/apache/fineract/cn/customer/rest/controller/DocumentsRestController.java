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
import org.apache.fineract.cn.customer.api.v1.domain.*;
import org.apache.fineract.cn.customer.internal.command.*;
import org.apache.fineract.cn.customer.internal.repository.CustomerEntity;
import org.apache.fineract.cn.customer.internal.repository.DocumentStorageEntity;
import org.apache.fineract.cn.customer.internal.repository.DocumentTypeEntity;
import org.apache.fineract.cn.customer.internal.service.CustomerService;
import org.apache.fineract.cn.customer.internal.service.DocumentService;
import org.apache.fineract.cn.lang.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

/**
 * @author Myrle Krantz
 */
@RestController
@RequestMapping("/customers/{customeridentifier}/documents")
public class DocumentsRestController {
    private final CommandGateway commandGateway;
    private final CustomerService customerService;
    private final DocumentService documentService;

    @Autowired
    public DocumentsRestController(
            final CommandGateway commandGateway,
            final CustomerService customerService,
            final DocumentService documentService) {
        this.commandGateway = commandGateway;
        this.customerService = customerService;
        this.documentService = documentService;
    }

    @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.DOCUMENTS)
    @RequestMapping(
            value = "/uploaded",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.ALL_VALUE
    )
    public ResponseEntity<CustomerDocument> getDocuments(@PathVariable("customeridentifier") final String customerIdentifier) {
        throwIfCustomerNotExists(customerIdentifier);
        return ResponseEntity.ok(documentService.findCustomerUploadedDocuments(customerIdentifier));
    }


    @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.DOCUMENTS)
    @RequestMapping(
            value = "/master",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.ALL_VALUE
    )
    public ResponseEntity<List<DocumentsMaster>> getMasterDocuments(@PathVariable("customeridentifier") final String customerIdentifier) {
        throwIfCustomerNotExists(customerIdentifier);
        return ResponseEntity.ok(documentService.findDocumentsTypesMaster(customerIdentifier));
    }

    @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.DOCUMENTS)
    @RequestMapping(
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.ALL_VALUE
    )
    public ResponseEntity<CustomerDocument> getUploadedDocuments(@PathVariable("customeridentifier") final String customerIdentifier) {
        throwIfCustomerNotExists(customerIdentifier);
        return ResponseEntity.ok(documentService.findCustomerDocuments(customerIdentifier));
    }


    @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.DOCUMENTS)
    @RequestMapping(
            value = "/{documentidentifier}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.ALL_VALUE
    )
    public ResponseEntity<CustomerDocumentEntry> getDocument(
            @PathVariable("customeridentifier") final String customerIdentifier,
            @PathVariable("documentidentifier") final Long documentIdentifier) {
        return ResponseEntity
                .ok(documentService.findDocument(customerIdentifier, documentIdentifier)
                        .orElseThrow(() -> ServiceException.notFound("Document ''{0}'' for customer ''{1}'' not found.",
                                documentIdentifier, customerIdentifier)));
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
            @PathVariable("customeridentifier") final String customerIdentifier,
            @PathVariable("documentidentifier") final String documentIdentifier,
            @RequestBody final @Valid CustomerDocument instance) {
        throwIfCustomerNotExists(customerIdentifier);
        if (!instance.getIdentifier().equals(documentIdentifier))
            throw ServiceException.badRequest("Document identifier in request body must match document identifier in request path.");
        commandGateway.process(new CreateDocumentCommand(customerIdentifier, instance));
        return ResponseEntity.accepted().build();
    }

    @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.DOCUMENTS)
    @RequestMapping(value = "/{documentidentifier}",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public @ResponseBody
    ResponseEntity<Void> changeDocumentStatus(
            @PathVariable("customeridentifier") final String customerIdentifier,
            @PathVariable("documentidentifier") final Long documentIdentifier) {
        throwIfCustomerNotExists(customerIdentifier);
        throwIfCustomerDocumentNotExists(customerIdentifier, documentIdentifier);
        throwIfDocumentCompleted(customerIdentifier, documentIdentifier);

        commandGateway.process(new ChangeDocumentStatusCommand(customerIdentifier, documentIdentifier));
        return ResponseEntity.accepted().build();
    }


    @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.DOCUMENTS)
    @RequestMapping(value = "/{documentidentifier}/reject",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public
    @ResponseBody
    ResponseEntity<Void> rejectCustomerDocument(
            @PathVariable("customeridentifier") final String customerIdentifier,
            @PathVariable("documentidentifier") final Long documentIdentifier,
            @RequestBody final RejectDocument reason) {
        throwIfCustomerNotExists(customerIdentifier);
        throwIfCustomerDocumentNotExists(customerIdentifier, documentIdentifier);
        throwIfDocumentCompleted(customerIdentifier, documentIdentifier);

        commandGateway.process(new RejectDocumentCommand(customerIdentifier, documentIdentifier, reason.getRejectReason()));
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
            @PathVariable("customeridentifier") final String customerIdentifier,
            @PathVariable("documentidentifier") final Long documentIdentifier) {
        throwIfCustomerNotExists(customerIdentifier);
        throwIfCustomerDocumentNotExists(customerIdentifier, documentIdentifier);
        throwIfDocumentCompleted(customerIdentifier, documentIdentifier);

        commandGateway.process(new DeleteDocumentCommand(customerIdentifier, documentIdentifier));

        return ResponseEntity.accepted().build();
    }


    //    upload document on each and return the document from the storage
    @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.DOCUMENTS)
    @RequestMapping(
            value = "/new",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public @ResponseBody
    DocumentStorage uploadNewDocument(
            @PathVariable(value = "customeridentifier") final String customeridentifier,
            @RequestParam(value = "file") final MultipartFile file,
            @RequestParam("docType") String docType) throws IOException {

        return this.documentService.addNewDocument(file, customeridentifier, docType);
    }


    //    receive the storage file
    @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.DOCUMENTS)
    @RequestMapping(
            value = "/file/{uuid}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.ALL_VALUE
    )

    public ResponseEntity<byte[]> getStorageDocument(
            @PathVariable("customeridentifier") final String customerIdentifier,
            @PathVariable("uuid") final String uuid) {

        final DocumentStorageEntity storageEntity = documentService.findDocumentStorageByUUID(uuid)
                .orElseThrow(() -> ServiceException.notFound("document ''{0}'' not found.", uuid));
        return ResponseEntity
                .ok()
                .contentType(MediaType.parseMediaType(storageEntity.getContentType()))
                .contentLength(storageEntity.getImage().length)
                .body(storageEntity.getImage());
    }


    @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.DOCUMENTS)
    @RequestMapping(
            value = "/submit",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.ALL_VALUE
    )
    public
    @ResponseBody
    ResponseEntity<Void> submitKycDocuments(
            @PathVariable("customeridentifier") final String customerIdentifier,
            @RequestBody CustomerDocument customerDocument) {
        CustomerEntity customerEntity = this.customerService.getCustomerEntity(customerIdentifier);

        customerDocument.getKycDocuments().forEach(kycDocuments -> {
            DocumentTypeEntity documentTypeEntity = this.documentService.findDocumentTypeEntityByUserTypeAndUuid(customerEntity.getType(), kycDocuments.getType()).orElseThrow(() -> ServiceException.notFound("Document types not available"));
            this.documentService.findDocumentSubTypeEntityByUuid(documentTypeEntity, kycDocuments.getSubType()).orElseThrow(() -> ServiceException.notFound("Document sub-types not available"));
        });

        commandGateway.process(new CreateDocumentEntryCommand(customerDocument, customerIdentifier));
        return ResponseEntity.accepted().build();
    }


    //    ------------------ create document data with data --------------------------
//    @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.DOCUMENTS)
//    @RequestMapping(
//            value = "/upload",
//            method = RequestMethod.POST,
//            produces = MediaType.APPLICATION_JSON_VALUE,
//            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
//    )
//    public @ResponseBody
//    ResponseEntity<Void> uploadKycDocuments(@PathVariable("customeridentifier") final String customerIdentifier,
//                                            @RequestParam("data") String data,
//                                            @RequestParam(value = "file") MultipartFile file) throws IOException {
//        throwIfCustomerNotExists(customerIdentifier);
////        throwIfCustomerDocumentAlreadyExist(customerIdentifier);
//
//        ObjectMapper mapper = new ObjectMapper();
//        CustomerDocumentsBody documentEntry = mapper.readValue(data, CustomerDocumentsBody.class);
//        if (file == null) {
//            throw ServiceException.badRequest("Document not found");
//        }
//        commandGateway.process(new CreateKYCDocumentCommand(customerIdentifier, file, documentEntry));
//        return ResponseEntity.accepted().build();
//    }


    private void throwIfCustomerDocumentAlreadyExist(String customerIdentifier) {
        if (this.documentService.isDocumentExistByCustomerIdentifier(customerIdentifier)) {
            throw ServiceException.notFound("Customer Document identifier already exist in the system");
        }
    }


    private void throwIfCustomerNotExists(final String customerIdentifier) {
        if (!this.customerService.customerExists(customerIdentifier)) {
            throw ServiceException.notFound("Customer ''{0}'' not found.", customerIdentifier);
        }
    }

    private void throwIfCustomerDocumentNotExists(final String customerIdentifier, final Long documentIdentifier) {
        if (!this.documentService.documentExists(customerIdentifier, documentIdentifier)) {
            throw ServiceException.notFound("Customer ''{0}'' not found.", customerIdentifier);
        }
    }

    private void throwIfInvalidContentType(final String contentType) {
        System.out.println("contentType :: " + contentType);
   /* if(!contentType) {
	    throw ServiceException.badRequest("Image has contentType ''{0}'', but only content types ''{1}'', ''{2}'' and ''{3}'' allowed.",
          contentType, MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE, MediaType.APPLICATION_PDF_VALUE);
    }*/
    }

    private void throwIfDocumentCompleted(final String customerIdentifier, final Long documentIdentifier) {
        if (documentService.isDocumentCompleted(customerIdentifier, documentIdentifier))
            throw ServiceException.conflict("The document ''{0}'' for customer ''{1}'' is completed and cannot be uncompleted.",
                    documentIdentifier, customerIdentifier);
    }
}
