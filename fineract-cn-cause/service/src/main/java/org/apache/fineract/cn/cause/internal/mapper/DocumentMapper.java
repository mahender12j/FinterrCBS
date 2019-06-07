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
package org.apache.fineract.cn.cause.internal.mapper;

import org.apache.fineract.cn.api.util.UserContextHolder;
import org.apache.fineract.cn.cause.api.v1.domain.Cause;
import org.apache.fineract.cn.cause.api.v1.domain.CauseDocument;
import org.apache.fineract.cn.cause.api.v1.domain.CauseDocumentPage;
import org.apache.fineract.cn.cause.api.v1.domain.CauseFiles;
import org.apache.fineract.cn.cause.internal.command.CreateCauseUpdateFileCommand;
import org.apache.fineract.cn.cause.internal.repository.CauseEntity;
import org.apache.fineract.cn.cause.internal.repository.DocumentEntity;
import org.apache.fineract.cn.cause.internal.repository.DocumentPageEntity;
import org.apache.fineract.cn.cause.internal.repository.DocumentStorageEntity;
import org.apache.fineract.cn.lang.DateConverter;

import java.io.IOException;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Myrle Krantz
 */
public class DocumentMapper {
    private DocumentMapper() {
        super();
    }


//    public static DocumentPageEntity map(
//            final MultipartFile multipartFile,
//            final DocumentEntity documentEntity) throws IOException {
//        final DocumentPageEntity ret = new DocumentPageEntity();
//        ret.setDocument(documentEntity);
//        ret.setImage(multipartFile.getBytes());
//        ret.setDocumentName(multipartFile.getOriginalFilename());
//        ret.setSize(multipartFile.getSize());
//        ret.setContentType(multipartFile.getContentType());
//        return ret;
//    }

    public static CauseDocument map(final DocumentEntity documentEntity) {
        final CauseDocument ret = new CauseDocument();
        ret.setId(documentEntity.getId());
        ret.setCompleted(documentEntity.getCompleted());
        ret.setCreatedBy(documentEntity.getCreatedBy());
        ret.setCreatedOn(DateConverter.toIsoString(documentEntity.getCreatedOn()));
        ret.setIdentifier(documentEntity.getIdentifier());
        ret.setDescription(documentEntity.getDescription());
        return ret;
    }

    public static DocumentEntity map(final CauseDocument causeDocument, final CauseEntity causeEntity) {
        final DocumentEntity ret = new DocumentEntity();
        ret.setCause(causeEntity);
        ret.setCompleted(false);
        ret.setCreatedBy(UserContextHolder.checkedGetUser());
        ret.setCreatedOn(LocalDateTime.now(Clock.systemUTC()));
        ret.setIdentifier(causeDocument.getIdentifier());
        ret.setDescription(causeDocument.getDescription());
        return ret;
    }

    public static DocumentEntity map(CauseEntity causeEntity) throws IOException {
        DocumentEntity entity = new DocumentEntity();
        entity.setCause(causeEntity);
        entity.setCompleted(true);
        entity.setCreatedBy(causeEntity.getCreatedBy());
        entity.setCreatedOn(LocalDateTime.now(Clock.systemUTC()));
        entity.setDescription(causeEntity.getDescription());
        entity.setIdentifier(causeEntity.getIdentifier());
        return entity;
    }


    public static DocumentPageEntity map(CauseFiles causeFiles, DocumentEntity documentEntity) {
        DocumentPageEntity pageEntity = new DocumentPageEntity();
        pageEntity.setDocument(documentEntity);
        pageEntity.setDocRef(causeFiles.getUuid());
        pageEntity.setDocumentName(causeFiles.getDocName());
        pageEntity.setType(causeFiles.getType());
        return pageEntity;
    }


    public static List<CauseDocumentPage> map(List<DocumentPageEntity> pageEntity) {
        List<CauseDocumentPage> documentPages = new ArrayList<>();
        pageEntity.forEach(d -> {
            CauseDocumentPage causeDocumentPage = new CauseDocumentPage();
            causeDocumentPage.setId(d.getId());
            causeDocumentPage.setType(d.getType().toLowerCase());
            causeDocumentPage.setDocumentName(d.getDocumentName());
            causeDocumentPage.setDocRef(d.getDocRef());
            documentPages.add(causeDocumentPage);
        });

        return documentPages;
    }


    public static List<CauseFiles> mapFile(List<DocumentPageEntity> documentPageEntities) {
        List<CauseFiles> causeFiles = new ArrayList<>();
        documentPageEntities.forEach(d -> {
            CauseFiles files = new CauseFiles();
            files.setDocName(d.getDocumentName());
            files.setType(d.getType().toLowerCase());
            files.setUuid(d.getDocRef());
            causeFiles.add(files);
        });

        return causeFiles;
    }


    public static List<DocumentPageEntity> map(List<CauseFiles> causeFiles, DocumentEntity documentEntity) {
        List<DocumentPageEntity> documentPageEntityList = new ArrayList<>();
        causeFiles.forEach(d -> {
            DocumentPageEntity entity = new DocumentPageEntity();
            entity.setDocument(documentEntity);
            entity.setDocRef(d.getUuid());
            entity.setDocumentName(d.getDocName());
            entity.setType(d.getType());
            documentPageEntityList.add(entity);
        });

        return documentPageEntityList;
    }


    public static CauseDocumentPage map(DocumentPageEntity pageEntity) {
        CauseDocumentPage causeDocumentPage = new CauseDocumentPage();
        causeDocumentPage.setType(pageEntity.getType().toLowerCase());
        causeDocumentPage.setId(pageEntity.getId());
        causeDocumentPage.setDocRef(pageEntity.getDocRef());
        causeDocumentPage.setDocumentName(pageEntity.getDocumentName());

        return causeDocumentPage;
    }

    public static List<DocumentPageEntity> map(Cause cause, DocumentEntity documentEntity) {
        List<DocumentPageEntity> documentPageEntityList = new ArrayList<>();
        cause.getCauseFiles().forEach(causeFiles -> {
            DocumentPageEntity pageEntity = new DocumentPageEntity();
            pageEntity.setDocRef(causeFiles.getUuid());
            pageEntity.setType(causeFiles.getType().toLowerCase());
            pageEntity.setDocumentName(causeFiles.getDocName());
            pageEntity.setDocument(documentEntity);
            documentPageEntityList.add(pageEntity);
        });

        return documentPageEntityList;
    }


    public static DocumentStorageEntity map(final CreateCauseUpdateFileCommand fileCommand) throws IOException {
        DocumentStorageEntity entity = new DocumentStorageEntity();
        entity.setContentType(fileCommand.getFile().getContentType());
        entity.setCreatedBy(UserContextHolder.checkedGetUser());
        entity.setDocType(fileCommand.getDocType());
        entity.setDocumentName(fileCommand.getFile().getOriginalFilename());
        entity.setImage(fileCommand.getFile().getBytes());
        entity.setSize(fileCommand.getFile().getSize());
        return entity;
    }

}
