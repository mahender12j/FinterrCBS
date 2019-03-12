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

/**
 * @author Md Robiul Hassan
 */
public class DocumentsSubType {
    private Long id;
    private String documentId;
    private String createdBy;
    private String status;
    private String type;
    private String subType;
    private String approvedBy;
    private String approvedOn;
    private String rejectedBy;
    private String reasonForReject;
    private String description;
    private String createdOn;


    public DocumentsSubType() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDocument_id() {
        return documentId;
    }

    public void setDocument_id(String document_id) {
        this.documentId = document_id;
    }

    public String getCreated_by() {
        return createdBy;
    }

    public void setCreated_by(String created_by) {
        this.createdBy = created_by;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSubType() {
        return subType;
    }

    public void setSubType(String subType) {
        this.subType = subType;
    }

    public String getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(String approvedBy) {
        this.approvedBy = approvedBy;
    }

    public String getApprovedOn() {
        return approvedOn;
    }

    public void setApprovedOn(String approvedOn) {
        this.approvedOn = approvedOn;
    }

    public String getRejectedBy() {
        return rejectedBy;
    }

    public void setRejectedBy(String rejectedBy) {
        this.rejectedBy = rejectedBy;
    }

    public String getReasonForReject() {
        return reasonForReject;
    }

    public void setReasonForReject(String reasonForReject) {
        this.reasonForReject = reasonForReject;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    @Override
    public String toString() {
        return "Documents{" +
                "id=" + id +
                ", document_id='" + documentId + '\'' +
                ", created_by='" + createdBy + '\'' +
                ", status='" + status + '\'' +
                ", type='" + type + '\'' +
                ", subType='" + subType + '\'' +
                ", approvedBy='" + approvedBy + '\'' +
                ", approvedOn='" + approvedOn + '\'' +
                ", rejectedBy='" + rejectedBy + '\'' +
                ", reasonForReject='" + reasonForReject + '\'' +
                ", description='" + description + '\'' +
                ", createdOn='" + createdOn + '\'' +
                '}';
    }
}
