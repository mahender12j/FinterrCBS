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
package org.apache.fineract.cn.cause.internal.command;

import org.hibernate.validator.constraints.NotBlank;

public class CauseUpdateFile {

    private Long id;
    @NotBlank
    private String uuid;
    @NotBlank
    private String createdBy;
    @NotBlank
    private String documentName;
    @NotBlank
    private String contentType;
    @NotBlank
    private Long size;
    private String docType;

    public CauseUpdateFile() {
    }

    public CauseUpdateFile(Long id, String uuid, String createdBy, String documentName, String contentType, Long size, String docType) {
        this.id = id;
        this.uuid = uuid;
        this.createdBy = createdBy;
        this.documentName = documentName;
        this.contentType = contentType;
        this.size = size;
        this.docType = docType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getDocumentName() {
        return documentName;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public String getDocType() {
        return docType;
    }

    public void setDocType(String docType) {
        this.docType = docType;
    }

    @Override
    public String toString() {
        return "CreateCauseUpdateFileResponseCommand{" +
                "id=" + id +
                ", uuid='" + uuid + '\'' +
                ", createdBy='" + createdBy + '\'' +
                ", documentName='" + documentName + '\'' +
                ", contentType='" + contentType + '\'' +
                ", size=" + size +
                ", docType='" + docType + '\'' +
                '}';
    }
}
