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

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author Md Robiul Hassan
 */
public class DocumentsType {

    @NotNull
    private String title;
    @NotNull
    private String userType;
    private String status;
    @NotNull
    private boolean active;
    @NotNull
    private int maxUpload;
    private List<DocumentsSubType> documentsSubType;

    public DocumentsType() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public List<DocumentsSubType> getDocumentsSubType() {
        return documentsSubType;
    }

    public int getMaxUpload() {
        return maxUpload;
    }

    public void setMaxUpload(int maxUpload) {
        this.maxUpload = maxUpload;
    }

    public void setDocumentsSubType(List<DocumentsSubType> documentsSubType) {
        this.documentsSubType = documentsSubType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "DocumentsType{" +
                "type='" + title + '\'' +
                ", documentsSubType=" + documentsSubType +
                '}';
    }
}
