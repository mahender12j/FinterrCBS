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
import org.apache.fineract.cn.command.domain.CommandCallback;
import org.apache.fineract.cn.command.domain.CommandProcessingException;
import org.apache.fineract.cn.command.gateway.CommandGateway;
import org.apache.fineract.cn.customer.PermittableGroupIds;
import org.apache.fineract.cn.customer.api.v1.domain.*;
import org.apache.fineract.cn.customer.internal.command.*;
import org.apache.fineract.cn.customer.internal.repository.*;
import org.apache.fineract.cn.customer.internal.service.CustomerService;
import org.apache.fineract.cn.customer.internal.service.DocumentService;
import org.apache.fineract.cn.lang.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

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
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.ALL_VALUE
    )
    public ResponseEntity<CustomerDocument> getUploadedDocuments(@PathVariable("customeridentifier") final String customerIdentifier) {
        throwIfCustomerNotExists(customerIdentifier);
        return ResponseEntity.ok(documentService.findCustomerDocuments(customerIdentifier));
    }


    @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.DOCUMENTS)
    @RequestMapping(method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public @ResponseBody
    ResponseEntity<CustomerDocument> approveCustomerDocument(@PathVariable("customeridentifier") final String customerIdentifier,
                                                             @Valid @RequestBody List<CustomerDocumentApproval> documentApproval) {
        throwIfCustomerDocumentAlreadyApproved(customerIdentifier);
        documentApproval.forEach(doc -> this.throwIfCustomerDocumentNotExists(customerIdentifier, doc.getId()));

        try {
            CommandCallback<CustomerDocument> callback = commandGateway.process(new UpdateDocumentStatusCommand(customerIdentifier, documentApproval), CustomerDocument.class);
            return ResponseEntity.ok(this.documentService.findCustomerDocuments(callback.get().getIdentifier()));
        } catch (CommandProcessingException | InterruptedException | ExecutionException e) {
            throw ServiceException.internalError("Sorry! Something went wrong");
        }
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
    ResponseEntity<Void> submitKycDocuments(@PathVariable("customeridentifier") final String customerIdentifier,
                                            @RequestBody CustomerDocument customerDocument) {
        CustomerEntity customerEntity = this.customerService.getCustomerEntity(customerIdentifier).orElseThrow(() -> ServiceException.notFound("Customer not found"));
        throwIfCustomerDocumentTypeAndSubtypeNotExists(customerEntity, customerDocument.getKycDocuments());

        commandGateway.process(new CreateDocumentEntryCommand(customerDocument, customerIdentifier));
        return ResponseEntity.accepted().build();
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
    @RequestMapping(value = "/{documentidentifier}",
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
    @RequestMapping(
            value = "/master/type",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.ALL_VALUE
    )
    public ResponseEntity<List<DocumentsMaster>> getMasterDocumentsType(@PathVariable("customeridentifier") final String customerIdentifier) {
        throwIfCustomerNotExists(customerIdentifier);
        return ResponseEntity.ok(documentService.findDocumentsTypes());
    }

    @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.DOCUMENTS)
    @RequestMapping(
            value = "/master/type",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public @ResponseBody
    ResponseEntity<CreateDocumentTypeCommandResponse> createDocumentType(
            @PathVariable("customeridentifier") final String customerIdentifier,
            @RequestBody final @Valid DocumentsType instance) {
        throwIfCustomerNotExists(customerIdentifier);

        try {
            final CommandCallback<CreateDocumentTypeCommandResponse> commandCallback = commandGateway.process(new CreateDocumentTypeCommand(customerIdentifier, instance), CreateDocumentTypeCommandResponse.class);
            return ResponseEntity.ok(commandCallback.get());
        } catch (CommandProcessingException | InterruptedException | ExecutionException e) {
            throw ServiceException.internalError("Sorry! Something went wrong");
        }
    }


    @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.DOCUMENTS)
    @RequestMapping(value = "/master/type/{uuid}",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public @ResponseBody
    ResponseEntity<Void> changeDocumentType(
            @PathVariable("customeridentifier") final String customerIdentifier,
            @PathVariable("uuid") final String uuid,
            @RequestBody final @Valid DocumentsType instance) {
        throwIfCustomerNotExists(customerIdentifier);
        throwIfMasterTypeNotExists(uuid);

        commandGateway.process(new UpdateDocumentTypeCommand(uuid, instance));
        return ResponseEntity.accepted().build();
    }

//    document sub type


    @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.DOCUMENTS)
    @RequestMapping(
            value = "/master/subtype",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.ALL_VALUE
    )
    public ResponseEntity<List<DocumentsMasterSubtype>> getMasterDocumentsSubType(
            @PathVariable("customeridentifier") final String customerIdentifier) {
        throwIfCustomerNotExists(customerIdentifier);
        return ResponseEntity.ok(documentService.findDocumentsSubTypes());
    }


    @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.DOCUMENTS)
    @RequestMapping(
            value = "/master/subtype",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public @ResponseBody
    ResponseEntity<Void> createDocumentSubType(
            @PathVariable("customeridentifier") final String customerIdentifier,
            @RequestBody final @Valid DocumentsMasterSubtype instance) {
        throwIfCustomerNotExists(customerIdentifier);
        throwIfDocumentTypeNotExists(instance.getDocTypeUUID());

        commandGateway.process(new CreateDocumentSubTypeCommand(instance, instance.getDocTypeUUID()));
        return ResponseEntity.accepted().build();
    }

    private void throwIfDocumentTypeNotExists(String uuid) {
        if (!this.customerService.masterTypeExists(uuid)) {
            throw ServiceException.notFound("Customer ''{0}'' not found.", uuid);
        }
    }


    @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.DOCUMENTS)
    @RequestMapping(value = "/master/subtype/{uuid}",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public @ResponseBody
    ResponseEntity<Void> changeDocumentSubType(
            @PathVariable("uuid") final String uuid,
            @PathVariable("customeridentifier") final String customerIdentifier,
            @RequestBody final @Valid DocumentsMasterSubtype instance) {
        throwIfCustomerNotExists(customerIdentifier);
        throwIfMasterTypeNotExists(instance.getDocTypeUUID());
        throwIfMasterSubTypeNotExists(uuid);


        commandGateway.process(new UpdateDocumentSubTypeCommand(uuid, instance));
        return ResponseEntity.accepted().build();
    }


// - document sub type


    // example: documents = [
    //   {id: 19, status: 'rejected', rejectedReason: 'Blurry'},
    //   {id: 20, status: 'approved'},
    // ];

//    todo kyc document update

//    @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.DOCUMENTS)
//    @RequestMapping(value = "/{documentidentifier}/approved",
//            method = RequestMethod.PUT,
//            produces = MediaType.APPLICATION_JSON_VALUE,
//            consumes = MediaType.APPLICATION_JSON_VALUE
//    )
//    public @ResponseBody
//    ResponseEntity<Void> approvedDocumentStatus(@PathVariable("customeridentifier") final String customerIdentifier,
//                                                @PathVariable("documentidentifier") final Long documentIdentifier) {
//        throwIfCustomerNotExists(customerIdentifier);
//        throwIfCustomerDocumentNotExists(customerIdentifier, documentIdentifier);
//        throwIfDocumentCompleted(customerIdentifier, documentIdentifier);
//        commandGateway.process(new ChangeDocumentStatusCommand(customerIdentifier, documentIdentifier));
//        return ResponseEntity.accepted().build();
//    }
//
//
//    @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.DOCUMENTS)
//    @RequestMapping(value = "/{documentidentifier}/pending",
//            method = RequestMethod.PUT,
//            produces = MediaType.APPLICATION_JSON_VALUE,
//            consumes = MediaType.APPLICATION_JSON_VALUE
//    )
//    public @ResponseBody
//    ResponseEntity<Void> undoDocumentStatus(@PathVariable("customeridentifier") final String customerIdentifier,
//                                            @PathVariable("documentidentifier") final Long documentIdentifier) {
//        throwIfCustomerNotExists(customerIdentifier);
//        throwIfCustomerDocumentNotExists(customerIdentifier, documentIdentifier);
//        throwIfDocumentCompleted(customerIdentifier, documentIdentifier);
//
//        commandGateway.process(new UndoDocumentStatusCommand(customerIdentifier, documentIdentifier));
//        return ResponseEntity.accepted().build();
//    }

//
//    @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.DOCUMENTS)
//    @RequestMapping(value = "/{documentidentifier}/rejected",
//            method = RequestMethod.PUT,
//            produces = MediaType.APPLICATION_JSON_VALUE,
//            consumes = MediaType.APPLICATION_JSON_VALUE
//    )
//    public
//    @ResponseBody
//    ResponseEntity<Void> rejectCustomerDocument(@PathVariable("customeridentifier") final String customerIdentifier,
//                                                @PathVariable("documentidentifier") final Long documentIdentifier,
//                                                @RequestBody final RejectDocument reason) {
//        throwIfCustomerNotExists(customerIdentifier);
//        throwIfCustomerDocumentNotExists(customerIdentifier, documentIdentifier);
//        throwIfDocumentCompleted(customerIdentifier, documentIdentifier);
//
//        commandGateway.process(new RejectDocumentCommand(customerIdentifier, documentIdentifier, reason.getRejectReason()));
//        return ResponseEntity.accepted().build();
//    }


    @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.DOCUMENTS)
    @RequestMapping(value = "/{documentidentifier}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.ALL_VALUE
    )
    public @ResponseBody
    ResponseEntity<Void> deleteDocument(@PathVariable("customeridentifier") final String customerIdentifier,
                                        @PathVariable("documentidentifier") final Long documentIdentifier) {
        throwIfCustomerDocumentAlreadyApproved(customerIdentifier);
        throwIfCustomerDocumentNotExists(customerIdentifier, documentIdentifier);

        commandGateway.process(new DeleteDocumentCommand(customerIdentifier, documentIdentifier));

        return ResponseEntity.accepted().build();
    }

    //    upload document on each and return the document from the storage
    @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.DOCUMENTS)
    @RequestMapping(value = "/new",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<DocumentStorage> uploadNewDocument(@PathVariable(value = "customeridentifier") final String customeridentifier,
                                                             @RequestParam(value = "file") final MultipartFile file,
                                                             @RequestParam("docType") String docType) {
        throwIfCustomerNotExists(customeridentifier);

        try {
            final CommandCallback<DocumentStorage> commandCallback = commandGateway.process(new UploadDocumentCommand(docType, file), DocumentStorage.class);
            return ResponseEntity.ok(commandCallback.get());
        } catch (CommandProcessingException | InterruptedException | ExecutionException e) {
            throw ServiceException.badRequest("Sorry! Something went wrong");
        }
    }


    //    receive the storage file
    @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.DOCUMENTS)
    @RequestMapping(value = "/file/{uuid}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.ALL_VALUE
    )

    public ResponseEntity<byte[]> getStorageDocument(@PathVariable("customeridentifier") final String customerIdentifier,
                                                     @PathVariable("uuid") final String uuid) {
        throwIfCustomerNotExists(customerIdentifier);
        final DocumentStorageEntity storageEntity = documentService.findDocumentStorageByUUID(uuid).orElseThrow(() -> ServiceException.notFound("document ''{0}'' not found.", uuid));
        return ResponseEntity
                .ok()
                .contentType(MediaType.parseMediaType(storageEntity.getContentType()))
                .contentLength(storageEntity.getImage().length)
                .body(storageEntity.getImage());
    }


//    util fucntion

    private void throwIfCustomerNotExists(final String customerIdentifier) {
        if (!this.customerService.customerExists(customerIdentifier)) {
            throw ServiceException.notFound("Customer ''{0}'' not found.", customerIdentifier);
        }
    }

    private void throwIfMasterTypeNotExists(final String uuid) {
        if (!this.customerService.masterTypeExists(uuid)) {
            throw ServiceException.notFound("Document ''{0}'' not found.", uuid);
        }
    }


    private void throwIfMasterSubTypeNotExists(String uuid) {
        if (!this.customerService.masterSubTypeExists(uuid)) {
            throw ServiceException.notFound("Document sub-type''{0}'' not found.", uuid);
        }
    }

    private void throwIfCustomerDocumentNotExists(final String customerIdentifier, final Long documentIdentifier) {
        if (!this.documentService.documentExists(customerIdentifier, documentIdentifier)) {
            throw ServiceException.notFound("Customer document ''{0}'' not found.", documentIdentifier);
        }
    }

//    private void throwIfDocumentCompleted(final String customerIdentifier, final Long documentIdentifier) {
//        if (documentService.isDocumentCompleted(customerIdentifier, documentIdentifier))
//            throw ServiceException.conflict("The document ''{0}'' for customer ''{1}'' is completed and cannot be uncompleted.",
//                    documentIdentifier, customerIdentifier);
//    }

    private void throwIfCustomerDocumentAlreadyApproved(String customerIdentifier) {
        CustomerEntity customerEntity = this.customerService.getCustomerEntity(customerIdentifier).orElseThrow(() -> ServiceException.notFound("Customer not found"));
        final DocumentEntity documentEntity = this.documentService.documentEntity(customerEntity).orElseThrow(() -> ServiceException.notFound("Sorry Document Not found for customer {0}", customerIdentifier));
        if (documentEntity.getStatus().equals(CustomerDocument.Status.APPROVED.name())) {
            throw ServiceException.conflict("Sorry! Customer document already approved!!");
        }
    }


    private void throwIfCustomerDocumentTypeAndSubtypeNotExists(CustomerEntity customerEntity, List<KycDocuments> kycDocuments) {
        for (KycDocuments documents : kycDocuments) {
            DocumentTypeEntity documentTypeEntity = this.documentService.findByUserTypeAndUuidAndActiveTrue(customerEntity.getType(), documents.getType()).orElseThrow(() -> ServiceException.notFound("Document types not available"));
            this.documentService.findDocumentSubTypeEntityByUuid(documentTypeEntity, documents.getSubType()).orElseThrow(() -> ServiceException.notFound("Document sub-types not available"));


            int uploadedDocSize = this.documentService.findAllByTypeAndStatusNot(documentTypeEntity.getUuid()).size();

            int docSize = (int) kycDocuments.stream()
                    .filter(doc -> this.documentService.getDocumentTypeTitle(doc.getType()).equals(documentTypeEntity.getTitle())).count();


            if (documentTypeEntity.getMaxUpload() < (docSize + uploadedDocSize)) {
                throw ServiceException.conflict("Sorry ! You have reached max documents for {0}.", documentTypeEntity.getTitle());
            }


        }
    }
}
