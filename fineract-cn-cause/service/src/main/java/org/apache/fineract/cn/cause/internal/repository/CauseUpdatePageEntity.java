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
package org.apache.fineract.cn.cause.internal.repository;

import org.apache.fineract.cn.mariadb.util.LocalDateTimeConverter;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * @author Padma Raju Sattineni
 */

@Entity
@Table(name = "cass_cause_update_page")
public class CauseUpdatePageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "update_id", nullable = false)
    private CauseUpdateEntity updateEntity;

    @Column(name = "doc_name")
    private String docName;

    @Column(name = "type")
    private String type;

    @Column(name = "doc_ref")
    private String docRef;

    @Column(name = "created_on")
    @Convert(converter = LocalDateTimeConverter.class)
    private LocalDateTime createdOn;

    @Column(name = "updated_at")
    @Convert(converter = LocalDateTimeConverter.class)
    private LocalDateTime updatedAt;


    public CauseUpdatePageEntity() {
        this.createdOn = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CauseUpdateEntity getUpdateEntity() {
        return updateEntity;
    }

    public void setUpdateEntity(CauseUpdateEntity updateEntity) {
        this.updateEntity = updateEntity;
    }

    public String getDocName() {
        return docName;
    }

    public void setDocName(String docName) {
        this.docName = docName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDocRef() {
        return docRef;
    }

    public void setDocRef(String docRef) {
        this.docRef = docRef;
    }

    public LocalDateTime getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(LocalDateTime createdOn) {
        this.createdOn = createdOn;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "CauseUpdatePageEntity{" +
                "id=" + id +
                ", updateEntity=" + updateEntity +
                ", docName='" + docName + '\'' +
                ", type='" + type + '\'' +
                ", docRef=" + docRef +
                ", createdOn=" + createdOn +
                ", updatedAt=" + updatedAt +
                '}';
    }
}

