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
package org.apache.fineract.cn.customer.api.v1.events;

import java.util.Objects;

/**
 * @author Md Robiul Hassan
 */
public class DocumentTypeEvent {

    private String title;
    private String userType;
    private String status;
    private boolean isActive;

    public DocumentTypeEvent() {
    }

    public DocumentTypeEvent(String title, String userType, String status, boolean isActive) {
        this.title = title;
        this.userType = userType;
        this.status = status;
        this.isActive = isActive;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    @Override
    public String toString() {
        return "DocumentTypeEvent{" +
                "title='" + title + '\'' +
                ", userType='" + userType + '\'' +
                ", status='" + status + '\'' +
                ", isActive=" + isActive +
                '}';
    }
}
