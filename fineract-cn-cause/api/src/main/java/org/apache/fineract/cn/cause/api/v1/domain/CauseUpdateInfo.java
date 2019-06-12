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
package org.apache.fineract.cn.cause.api.v1.domain;


import org.hibernate.validator.constraints.NotBlank;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

public final class CauseUpdateInfo {


    @Valid
    @NotBlank
    private Long id;

    @NotBlank
    @Valid
    private String description;
    @NotBlank
    @Valid
    private String docRef;

    private List<CauseUpdate> causeUpdateList;

    private String updatedAt;
    private String createOn;


    public CauseUpdateInfo() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDocRef() {
        return docRef;
    }

    public void setDocRef(String docRef) {
        this.docRef = docRef;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getCreateOn() {
        return createOn;
    }

    public void setCreateOn(String createOn) {
        this.createOn = createOn;
    }

    public List<CauseUpdate> getCauseUpdateList() {
        return causeUpdateList;
    }

    public void setCauseUpdateList(List<CauseUpdate> causeUpdateList) {
        this.causeUpdateList = causeUpdateList;
    }

    @Override
    public String toString() {
        return "CauseUpdateInfo{" +
                "description='" + description + '\'' +
                ", docInfo=" + docRef +
                ", updatedAt='" + updatedAt + '\'' +
                ", createOn='" + createOn + '\'' +
                '}';
    }
}
