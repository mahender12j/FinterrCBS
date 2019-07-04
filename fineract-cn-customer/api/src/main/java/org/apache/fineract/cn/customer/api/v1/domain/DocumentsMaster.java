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
package org.apache.fineract.cn.customer.api.v1.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

/**
 * @author Md Robiul Hassan
 */
public class DocumentsMaster {

    @JsonIgnore
    private Long id;
    private String title;
    private String uuid;
    private String userType;
    private boolean active;
    private int maxUpload;
    private List<DocumentsMasterSubtype> documentsMasterSubtypes;

    public DocumentsMaster() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public List<DocumentsMasterSubtype> getDocumentsMasterSubtypes() {
        return documentsMasterSubtypes;
    }

    public void setDocumentsMasterSubtypes(List<DocumentsMasterSubtype> documentsMasterSubtypes) {
        this.documentsMasterSubtypes = documentsMasterSubtypes;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getMaxUpload() {
        return maxUpload;
    }

    public void setMaxUpload(int maxUpload) {
        this.maxUpload = maxUpload;
    }

    @Override
    public String toString() {
        return "DocumentsMaster{" +
                "title='" + title + '\'' +
                ", uuid='" + uuid + '\'' +
                ", userType='" + userType + '\'' +
                ", documentsMasterSubtypes=" + documentsMasterSubtypes +
                '}';
    }
}
