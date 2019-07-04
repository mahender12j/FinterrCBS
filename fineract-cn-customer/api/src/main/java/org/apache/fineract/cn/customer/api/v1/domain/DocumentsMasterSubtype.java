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

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author Md Robiul Hassan
 */
public class DocumentsMasterSubtype {

    @JsonIgnore
    private Long id;
    @NotNull
    private String title;
    private String uuid;
    @JsonIgnore
    private Long docTypeId;
    @NotNull
    private String docTypeUUID;
    @NotNull
    private boolean active;

    public DocumentsMasterSubtype() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Long getDocTypeId() {
        return docTypeId;
    }

    public void setDocTypeId(Long docTypeId) {
        this.docTypeId = docTypeId;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getDocTypeUUID() {
        return docTypeUUID;
    }

    public void setDocTypeUUID(String docTypeUUID) {
        this.docTypeUUID = docTypeUUID;
    }

    @Override
    public String toString() {
        return "DocumentsMasterSubtype{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", uuid='" + uuid + '\'' +
                ", docTypeId=" + docTypeId +
                '}';
    }
}
