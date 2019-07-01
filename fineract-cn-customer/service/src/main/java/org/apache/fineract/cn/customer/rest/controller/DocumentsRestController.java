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
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

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
            value = "/countries",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.ALL_VALUE
    )
    public ResponseEntity<HashMap<String, String>> fetchBankList(@PathVariable("customeridentifier") final String customerIdentifier) throws NoSuchAlgorithmException, IOException, KeyManagementException {
        throwIfCustomerNotExists(customerIdentifier);

        //        default value
        String final_checkSum = "566DACEE09ADFA8D65733CC05E7599964556E8FE1E7396A84717CAEA79DEC96022C226593B35B1E4EF441A8052C636861E1DC298CB3BA3C5FA1F6F7D409AE01DB0A9BBD26EA27F6DC98BFFE1758C1746922C6A9A8BA18120C15B4B8C05F994767A715C834C09B313895AEDB25E8CBA36B5CB7A82CB5496BA1857F4AB0BAEDD3E5239B5B5441729A683199B90C7AD9B537AD9DBE9168EDA1D1E82ECC0F111BA33DD4A6FB097FDA38DB80CFBF9FB8B7773E062C11545F6C7B94FBAC3707AF72297D11DF4A21C5E70C07F242ADA8F597F0C3BC16C14D840A0010B46BE96F8B5BA6CDAF21B9514B71D332B3543B19DBDDF6DCAF8A4EBE31A0445F9AD4A0C5C9BDC60";
        String fpx_msgType = "BE";
        String fpx_msgToken = "01";
        String fpx_sellerExId = "EX00009694";
        String fpx_version = "7.0";


        return ResponseEntity.ok(this.customerService.fetchBankList(final_checkSum, fpx_msgType, fpx_msgToken, fpx_sellerExId, fpx_version));
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
            value = "/submit",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.ALL_VALUE
    )
    public
    @ResponseBody
    ResponseEntity<Void> submitKycDocuments(@PathVariable("customeridentifier") final String customerIdentifier,
                                            @RequestBody CustomerDocument customerDocument) {
        CustomerEntity customerEntity = this.customerService.getCustomerEntity(customerIdentifier);
        for (KycDocuments kycDocuments : customerDocument.getKycDocuments()) {
            DocumentTypeEntity documentTypeEntity = this.documentService.findDocumentTypeEntityByUserTypeAndUuid(customerEntity.getType(), kycDocuments.getType()).orElseThrow(() -> ServiceException.notFound("Document types not available"));
            this.documentService.findDocumentSubTypeEntityByUuid(documentTypeEntity, kycDocuments.getSubType()).orElseThrow(() -> ServiceException.notFound("Document sub-types not available"));
        }

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


    @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.DOCUMENTS)
    @RequestMapping(value = "/{documentidentifier}/approved",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public @ResponseBody
    ResponseEntity<Void> approvedDocumentStatus(
            @PathVariable("customeridentifier") final String customerIdentifier,
            @PathVariable("documentidentifier") final Long documentIdentifier) {
        throwIfCustomerNotExists(customerIdentifier);
        throwIfCustomerDocumentNotExists(customerIdentifier, documentIdentifier);
        throwIfDocumentCompleted(customerIdentifier, documentIdentifier);

        commandGateway.process(new ChangeDocumentStatusCommand(customerIdentifier, documentIdentifier));
        return ResponseEntity.accepted().build();
    }


    @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.DOCUMENTS)
    @RequestMapping(value = "/{documentidentifier}/pending",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public @ResponseBody
    ResponseEntity<Void> undoDocumentStatus(@PathVariable("customeridentifier") final String customerIdentifier,
                                            @PathVariable("documentidentifier") final Long documentIdentifier) {
        throwIfCustomerNotExists(customerIdentifier);
        throwIfCustomerDocumentNotExists(customerIdentifier, documentIdentifier);
        throwIfDocumentCompleted(customerIdentifier, documentIdentifier);

        commandGateway.process(new UndoDocumentStatusCommand(customerIdentifier, documentIdentifier));
        return ResponseEntity.accepted().build();
    }


    @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.DOCUMENTS)
    @RequestMapping(value = "/{documentidentifier}/rejected",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public
    @ResponseBody
    ResponseEntity<Void> rejectCustomerDocument(@PathVariable("customeridentifier") final String customerIdentifier,
                                                @PathVariable("documentidentifier") final Long documentIdentifier,
                                                @RequestBody final RejectDocument reason) {
        throwIfCustomerNotExists(customerIdentifier);
        throwIfCustomerDocumentNotExists(customerIdentifier, documentIdentifier);
        throwIfDocumentCompleted(customerIdentifier, documentIdentifier);

        commandGateway.process(new RejectDocumentCommand(customerIdentifier, documentIdentifier, reason.getRejectReason()));
        return ResponseEntity.accepted().build();
    }


    @Permittable(value = AcceptedTokenType.TENANT, groupId = PermittableGroupIds.DOCUMENTS)
    @RequestMapping(value = "/{documentidentifier}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.ALL_VALUE
    )
    public @ResponseBody
    ResponseEntity<Void> deleteDocument(@PathVariable("customeridentifier") final String customerIdentifier,
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
    public ResponseEntity<DocumentStorage> uploadNewDocument(
            @PathVariable(value = "customeridentifier") final String customeridentifier,
            @RequestParam(value = "file") final MultipartFile file,
            @RequestParam("docType") String docType) {
        throwIfCustomerNotExists(customeridentifier);

        try {
            final CommandCallback<DocumentStorage> commandCallback = commandGateway.process(new UploadDocumentCommand(docType, file), DocumentStorage.class);
            return ResponseEntity.ok(commandCallback.get());
        } catch (CommandProcessingException | InterruptedException | ExecutionException e) {
            throw ServiceException.badRequest("Sorry! Something went wrong");
        }
//        return this.documentService.addNewDocument(file, customeridentifier, docType);
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
        throwIfCustomerNotExists(customerIdentifier);

        final DocumentStorageEntity storageEntity = documentService.findDocumentStorageByUUID(uuid)
                .orElseThrow(() -> ServiceException.notFound("document ''{0}'' not found.", uuid));
        return ResponseEntity
                .ok()
                .contentType(MediaType.parseMediaType(storageEntity.getContentType()))
                .contentLength(storageEntity.getImage().length)
                .body(storageEntity.getImage());
    }


//    util fucntion

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
