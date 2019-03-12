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
package org.apache.fineract.cn.customer.internal.mapper;

import org.apache.fineract.cn.customer.api.v1.domain.CustomerDocument;
import org.apache.fineract.cn.customer.api.v1.domain.CustomerDocumentEntry;
import org.apache.fineract.cn.customer.api.v1.domain.DocumentsSubType;
import org.apache.fineract.cn.customer.api.v1.domain.DocumentsType;
import org.apache.fineract.cn.customer.internal.repository.CustomerEntity;
import org.apache.fineract.cn.customer.internal.repository.DocumentEntity;
import org.apache.fineract.cn.customer.internal.repository.DocumentEntryEntity;
import org.apache.fineract.cn.customer.internal.repository.DocumentPageEntity;

import java.io.IOException;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.fineract.cn.api.util.UserContextHolder;
import org.apache.fineract.cn.lang.DateConverter;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Myrle Krantz
 */
public class DocumentMapper {
    private DocumentMapper() {
        super();
    }


    public static DocumentPageEntity map(
            final MultipartFile multipartFile,
            final int pageNumber,
            final DocumentEntity documentEntity, DocumentEntryEntity documentEntryEntity) throws IOException {
        final DocumentPageEntity ret = new DocumentPageEntity();
        ret.setDocumentEntry(documentEntryEntity);
        ret.setPageNumber(pageNumber);
        ret.setImage(multipartFile.getBytes());
        ret.setSize(multipartFile.getSize());
        ret.setContentType(multipartFile.getContentType());
        return ret;
    }

    public static List<DocumentsType> map(Map<String, List<DocumentEntryEntity>> documentEntryEntity) {
        final List<DocumentsType> ret = new ArrayList<>();
        documentEntryEntity.forEach((key, val) -> {
            final List<DocumentsSubType> documentsSubTypeList = new ArrayList<>();
            val.forEach(doc -> {
                final DocumentsSubType documentsSubType = new DocumentsSubType();
                documentsSubType.setId(doc.getId());
                documentsSubType.setCreated_by(doc.getCreatedBy());
                documentsSubType.setStatus(doc.getStatus());
                documentsSubType.setType(doc.getType());
                documentsSubType.setSubType(doc.getSubType());
                documentsSubType.setApprovedBy(doc.getApprovedBy());
//                documentsSubType.setApprovedOn(doc.getApprovedOn().toString());
                documentsSubType.setRejectedBy(doc.getRejectedBy());
                documentsSubType.setReasonForReject(doc.getReasonForReject());
                documentsSubType.setDescription(doc.getDescription());
                documentsSubType.setCreatedOn(doc.getCreatedOn().toString());
                documentsSubTypeList.add(documentsSubType);
            });
            final DocumentsType d = new DocumentsType();
            d.setType(key);
            d.setDocumentsSubType(documentsSubTypeList);
            ret.add(d);
        });
        return ret;
    }


    public static DocumentEntryEntity map(final CustomerDocumentEntry customerDocumentEntry, final DocumentEntity documentEntity) {
        final DocumentEntryEntity ret = new DocumentEntryEntity();
        ret.setDescription(customerDocumentEntry.getDescription());
        ret.setType(customerDocumentEntry.getType());
        ret.setSubType(customerDocumentEntry.getSubType());
        ret.setCreatedBy(documentEntity.getCreatedBy());
        ret.setDocument(documentEntity);
        return ret;
    }

    public static CustomerDocument map(final DocumentEntity documentEntity) {
        final CustomerDocument ret = new CustomerDocument();
        ret.setCompleted(documentEntity.getCompleted());
        ret.setCreatedBy(documentEntity.getCreatedBy());
        ret.setCreatedOn(DateConverter.toIsoString(documentEntity.getCreatedOn()));
        ret.setIdentifier(documentEntity.getIdentifier());
        ret.setDescription(documentEntity.getDescription());
        return ret;
    }

    public static DocumentEntity map(final CustomerDocument customerDocument, final CustomerEntity customerEntity) {
        final DocumentEntity ret = new DocumentEntity();
        ret.setCustomer(customerEntity);
        ret.setCompleted(false);
        ret.setCreatedBy(UserContextHolder.checkedGetUser());
        ret.setCreatedOn(LocalDateTime.now(Clock.systemUTC()));
        ret.setIdentifier(customerDocument.getIdentifier());
        ret.setDescription(customerDocument.getDescription());
        return ret;
    }
}
