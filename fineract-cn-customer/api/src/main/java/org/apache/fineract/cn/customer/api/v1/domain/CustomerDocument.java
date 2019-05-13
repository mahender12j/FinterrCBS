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

import java.util.List;
import java.util.Objects;

import org.apache.fineract.cn.lang.validation.constraints.ValidIdentifier;
import org.hibernate.validator.constraints.Length;

/**
 * @author Myrle Krantz
 */
public class CustomerDocument {

    public enum Status {PENDING, DELETED, APPROVED, REJECTED, NOTUPLOADED}

    @ValidIdentifier
    private String identifier;
    @Length(max = 4096)
    private String description;
    //    private boolean completed;
    private String createdBy;
    private String createdOn;
    private boolean kycStatus;
    private String kycStatusText;


    private List<DocumentsType> documentsTypes;
    private List<KycDocuments> kycDocuments;


    public CustomerDocument() {
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

//    public boolean isCompleted() {
//        return completed;
//    }
//
//    public void setCompleted(boolean completed) {
//        this.completed = completed;
//    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    public List<DocumentsType> getDocumentsTypes() {
        return documentsTypes;
    }

    public void setDocumentsTypes(List<DocumentsType> documentsTypes) {
        this.documentsTypes = documentsTypes;
    }

    public List<KycDocuments> getKycDocuments() {
        return kycDocuments;
    }

    public void setKycDocuments(List<KycDocuments> kycDocuments) {
        this.kycDocuments = kycDocuments;
    }

    public boolean isKycStatus() {
        return kycStatus;
    }

    public void setKycStatus(boolean kycStatus) {
        this.kycStatus = kycStatus;
    }

    public String getKycStatusText() {
        return kycStatusText;
    }

    public void setKycStatusText(String kycStatusText) {
        this.kycStatusText = kycStatusText;
    }

    @Override
    public String toString() {
        return "CustomerDocument{" +
                "identifier='" + identifier + '\'' +
                ", description='" + description + '\'' +
                ", createdBy='" + createdBy + '\'' +
                ", createdOn='" + createdOn + '\'' +
                ", kycStatus='" + kycStatus + '\'' +
                ", documentsTypes=" + documentsTypes +
                ", kycDocuments=" + kycDocuments +
                '}';
    }
}
