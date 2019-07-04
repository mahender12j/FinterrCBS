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
package org.apache.fineract.cn.cause.internal.command.handler;

import org.apache.fineract.cn.api.util.UserContextHolder;
import org.apache.fineract.cn.cause.api.v1.CauseEventConstants;
import org.apache.fineract.cn.cause.api.v1.events.DocumentEvent;
import org.apache.fineract.cn.cause.internal.command.ChangeDocumentCommand;
import org.apache.fineract.cn.cause.internal.command.CompleteDocumentCommand;
import org.apache.fineract.cn.cause.internal.mapper.DocumentMapper;
import org.apache.fineract.cn.cause.internal.repository.CauseRepository;
import org.apache.fineract.cn.cause.internal.repository.DocumentEntity;
import org.apache.fineract.cn.cause.internal.repository.DocumentPageRepository;
import org.apache.fineract.cn.cause.internal.repository.DocumentRepository;
import org.apache.fineract.cn.command.annotation.Aggregate;
import org.apache.fineract.cn.command.annotation.CommandHandler;
import org.apache.fineract.cn.command.annotation.EventEmitter;
import org.apache.fineract.cn.lang.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.Clock;
import java.time.LocalDateTime;

/**
 * @author Myrle Krantz
 */
@Aggregate
public class DocumentCommandHandler {
    private final DocumentRepository documentRepository;
    private final DocumentPageRepository documentPageRepository;
    private final CauseRepository causeRepository;

    @Autowired
    public DocumentCommandHandler(
            final DocumentRepository documentRepository,
            final DocumentPageRepository documentPageRepository,
            final CauseRepository causeRepository) {
        this.documentRepository = documentRepository;
        this.documentPageRepository = documentPageRepository;
        this.causeRepository = causeRepository;
    }

//    @Transactional
//    @CommandHandler
//    @EventEmitter(selectorName = CauseEventConstants.SELECTOR_NAME, selectorValue = CauseEventConstants.POST_DOCUMENT_PAGE)
//    public DocumentPageEvent process(final CreateDocumentPageCommand command) throws IOException {
//        final DocumentEntity documentEntity = documentRepository.findByCauseIdAndDocumentIdentifier(
//                command.getCauseIdentifier(),
//                command.getDocumentIdentifier())
//                .orElseThrow(() -> ServiceException.badRequest("Document not found"));
//
//        final DocumentPageEntity documentPageEntity = DocumentMapper.map(command.getDocument(), command.getPageNumber(), documentEntity);
//        documentPageRepository.save(documentPageEntity);
//
//        return new DocumentPageEvent(command.getCauseIdentifier(), command.getDocumentIdentifier(), command.getPageNumber());
//    }

//    @Transactional
//    @CommandHandler
//    @EventEmitter(selectorName = CauseEventConstants.SELECTOR_NAME, selectorValue = CauseEventConstants.POST_DOCUMENT)
//    public DocumentEvent process(final CreateDocumentCommand command) throws IOException {
//        causeRepository.findByIdentifier(command.getCauseIdentifier())
//                .map(causeEntity -> DocumentMapper.map(command.getCauseDocument(), causeEntity))
//                .ifPresent(documentRepository::save);
//
//        return new DocumentEvent(command.getCauseIdentifier(), command.getCauseDocument().getRating());
//    }


//    @Transactional
//    @CommandHandler
//    @EventEmitter(selectorName = CauseEventConstants.SELECTOR_NAME, selectorValue = CauseEventConstants.POST_DOCUMENT)
//    public DocumentEvent process(final CreateDocumentCommand command) throws IOException {
//        CauseEntity causeEntity = causeRepository.findByIdentifier(command.getCauseIdentifier()).get();
//        System.out.println("------------------cause identifier----------------------" + causeEntity.toString());
//
//        DocumentEntity documentEntity = documentRepository.save(DocumentMapper.map(causeEntity));
//
//        System.out.println("-----------------------documentEntity--------------------" + documentEntity.toString());
//
//        DocumentPageEntity FeaturePageEntity = DocumentMapper.map(command.getFeature(), documentEntity, "Feature");
////        DocumentPageEntity FeaturePageEntity = DocumentMapper.map(command.getFeature(), documentEntity, "Gallary");
//        DocumentPageEntity TaxPageEntity = DocumentMapper.map(command.getTax(), documentEntity, "Tax");
//        DocumentPageEntity TermsPageEntity = DocumentMapper.map(command.getTerms(), documentEntity, "Terms");
//        DocumentPageEntity OtherPageEntity = DocumentMapper.map(command.getOther(), documentEntity, "Other");
//
//
//        documentPageRepository.save(FeaturePageEntity);
////        documentPageRepository.save(FeaturePageEntity);
//        documentPageRepository.save(TaxPageEntity);
//        documentPageRepository.save(TermsPageEntity);
//        documentPageRepository.save(OtherPageEntity);
//
//
//        return new DocumentEvent(command.getCauseIdentifier(), command.getCauseIdentifier());
//    }

    @Transactional
    @CommandHandler
    @EventEmitter(selectorName = CauseEventConstants.SELECTOR_NAME, selectorValue = CauseEventConstants.PUT_DOCUMENT)
    public DocumentEvent process(final ChangeDocumentCommand command) throws IOException {
        final DocumentEntity existingDocument = documentRepository.findByCauseIdAndDocumentIdentifier(
                command.getCauseIdentifier(), command.getCauseDocument().getIdentifier())
                .orElseThrow(() ->
                        ServiceException.notFound("Document ''{0}'' for cause ''{1}'' not found",
                                command.getCauseDocument().getIdentifier(), command.getCauseIdentifier()));

        causeRepository.findByIdentifier(command.getCauseIdentifier())
                .map(causeEntity -> DocumentMapper.map(command.getCauseDocument(), causeEntity))
                .ifPresent(documentEntity -> {
                    documentEntity.setId(existingDocument.getId());
                    documentRepository.save(documentEntity);
                });

        return new DocumentEvent(command.getCauseIdentifier(), command.getCauseDocument().getIdentifier());
    }

//    @Transactional
//    @CommandHandler
//    @EventEmitter(selectorName = CauseEventConstants.SELECTOR_NAME, selectorValue = CauseEventConstants.DELETE_DOCUMENT)
//    public DocumentEvent process(final DeleteDocumentCommand command) throws IOException {
//        final DocumentEntity existingDocument = documentRepository.findByCauseIdAndDocumentIdentifier(
//                command.getCauseIdentifier(), command.getDocumentIdentifier())
//                .orElseThrow(() ->
//                        ServiceException.notFound("Document ''{0}'' for cause ''{1}'' not found",
//                                command.getDocumentIdentifier(), command.getCauseIdentifier()));
//        documentPageRepository.findByCauseIdAndDocumentIdentifier(command.getCauseIdentifier(), command.getDocumentIdentifier())
//                .forEach(documentPageRepository::delete);
//        documentRepository.delete(existingDocument);
//
//        return new DocumentEvent(command.getCauseIdentifier(), command.getDocumentIdentifier());
//    }

    @Transactional
    @CommandHandler
    @EventEmitter(selectorName = CauseEventConstants.SELECTOR_NAME, selectorValue = CauseEventConstants.POST_DOCUMENT_COMPLETE)
    public DocumentEvent process(final CompleteDocumentCommand command) throws IOException {
        final DocumentEntity documentEntity = documentRepository.findByCauseIdAndDocumentIdentifier(
                command.getCauseIdentifier(),
                command.getDocumentIdentifier())
                .orElseThrow(() -> ServiceException.badRequest("Document not found"));

        documentEntity.setCreatedOn(LocalDateTime.now(Clock.systemUTC()));
        documentEntity.setCreatedBy(UserContextHolder.checkedGetUser());
        documentEntity.setCompleted(true);
        documentRepository.save(documentEntity);


        return new DocumentEvent(command.getCauseIdentifier(), command.getDocumentIdentifier());
    }

//    @Transactional
//    @CommandHandler
//    @EventEmitter(selectorName = CauseEventConstants.SELECTOR_NAME, selectorValue = CauseEventConstants.DELETE_DOCUMENT_PAGE)
//    public DocumentPageEvent process(final DeleteDocumentPageCommand command) throws IOException {
//        documentPageRepository.findByCauseIdAndDocumentIdentifierAndPageNumber(
//                command.getCauseIdentifier(),
//                command.getDocumentIdentifier(),
//                command.getPageNumber())
//                .ifPresent(documentPageRepository::delete);
//
//        //No exception if it's not present, because why bother.  It's not present.  That was the goal.
//
//        return new DocumentPageEvent(command.getCauseIdentifier(), command.getDocumentIdentifier(), command.getPageNumber());
//    }
}
