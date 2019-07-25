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
package org.apache.fineract.cn.customer.internal.repository;

import org.apache.fineract.cn.mariadb.util.LocalDateTimeConverter;

import javax.persistence.*;
import java.time.Clock;
import java.time.LocalDateTime;

/**
 * @author Md Robiul Hassan
 */
@Entity
@Table(name = "maat_documents_entry")
public class DocumentEntryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "document_id")
    private DocumentEntity document;


    @Column(name = "document_name")
    private String documentName;

    @Column(name = "uuid")
    private String docRef;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "description")
    private String description;

    @Column(name = "created_on")
    @Convert(converter = LocalDateTimeConverter.class)
    private LocalDateTime createdOn;

    @Column(name = "updated_on")
    @Convert(converter = LocalDateTimeConverter.class)
    private LocalDateTime updatedOn;

    @Column(name = "status")
    private String status;

    @Column(name = "type")
    private String type;

    @Column(name = "sub_type")
    private String subType;

    @Column(name = "approved_by")
    private String approvedBy;

    @Column(name = "approved_on")
    @Convert(converter = LocalDateTimeConverter.class)
    private LocalDateTime approvedOn;

    @Column(name = "reason_for_reject")
    private String reasonForReject;

    @Column(name = "rejected_by")
    private String rejectedBy;

    @Column(name = "rejected_on")
    @Convert(converter = LocalDateTimeConverter.class)
    private LocalDateTime rejectedOn;


    public DocumentEntryEntity() {
        this.createdOn = LocalDateTime.now(Clock.systemUTC());
        this.updatedOn = LocalDateTime.now(Clock.systemUTC());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DocumentEntity getDocument() {
        return document;
    }

    public void setDocument(DocumentEntity document) {
        this.document = document;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(LocalDateTime createdOn) {
        this.createdOn = createdOn;
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

    public LocalDateTime getUpdatedOn() {
        return updatedOn;
    }

    public void setUpdatedOn(LocalDateTime updatedOn) {
        this.updatedOn = updatedOn;
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

    public LocalDateTime getApprovedOn() {
        return approvedOn;
    }

    public void setApprovedOn(LocalDateTime approvedOn) {
        this.approvedOn = approvedOn;
    }

    public String getReasonForReject() {
        return reasonForReject;
    }

    public void setReasonForReject(String reasonForReject) {
        this.reasonForReject = reasonForReject;
    }

    public String getRejectedBy() {
        return rejectedBy;
    }

    public void setRejectedBy(String rejectedBy) {
        this.rejectedBy = rejectedBy;
    }

    public LocalDateTime getRejectedOn() {
        return rejectedOn;
    }

    public void setRejectedOn(LocalDateTime rejectedOn) {
        this.rejectedOn = rejectedOn;
    }

    public String getDescription() {
        return description;
    }

    public String getDocumentName() {
        return documentName;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }

    public String getDocRef() {
        return docRef;
    }

    public void setDocRef(String docRef) {
        this.docRef = docRef;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    @Override
    public String toString() {
        return "DocumentEntryEntity{" +
                "id=" + id +
                ", document=" + document +
                ", createdBy='" + createdBy + '\'' +
                ", description='" + description + '\'' +
                ", createdOn=" + createdOn +
                ", status='" + status + '\'' +
                ", type='" + type + '\'' +
                ", subType='" + subType + '\'' +
                ", approvedBy='" + approvedBy + '\'' +
                ", approvedOn=" + approvedOn +
                ", reasonForReject='" + reasonForReject + '\'' +
                ", rejectedBy='" + rejectedBy + '\'' +
                ", rejectedOn=" + rejectedOn +
                '}';
    }


}
