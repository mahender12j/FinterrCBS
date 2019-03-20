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
import org.apache.fineract.cn.cause.internal.repository.CauseEntity;
import org.apache.fineract.cn.cause.internal.repository.DocumentEntity;
import org.apache.fineract.cn.cause.internal.repository.DocumentPageEntity;
import org.apache.fineract.cn.lang.DateConverter;
import org.springframework.web.multipart.MultipartFile;

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


    public static DocumentPageEntity map(
            final MultipartFile multipartFile,
            final DocumentEntity documentEntity) throws IOException {
        final DocumentPageEntity ret = new DocumentPageEntity();
        ret.setDocument(documentEntity);
        ret.setImage(multipartFile.getBytes());
        ret.setSize(multipartFile.getSize());
        ret.setContentType(multipartFile.getContentType());
        return ret;
    }

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


    public static DocumentPageEntity map(final MultipartFile multipartFile, DocumentEntity documentEntity, final String type) throws IOException {
        DocumentPageEntity pageEntity = new DocumentPageEntity();
        pageEntity.setDocument(documentEntity);
        pageEntity.setContentType(multipartFile.getContentType());
        pageEntity.setImage(multipartFile.getBytes());
        pageEntity.setSize(multipartFile.getSize());
        pageEntity.setType(type);
        return pageEntity;
    }


    public static List<CauseDocumentPage> map(List<DocumentPageEntity> pageEntity) {
        List<CauseDocumentPage> documentPages = new ArrayList<>();

        pageEntity.forEach(d -> {
            CauseDocumentPage causeDocumentPage = new CauseDocumentPage();
            causeDocumentPage.setId(d.getId());
            causeDocumentPage.setType(d.getType());
            documentPages.add(causeDocumentPage);
        });

        return documentPages;
    }


}
