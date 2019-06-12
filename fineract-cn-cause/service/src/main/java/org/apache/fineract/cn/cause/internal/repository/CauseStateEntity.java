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
@Table(name = "cass_causes_state")
public class CauseStateEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "cause_id")
    private CauseEntity cause;


    @Column(name = "comment")
    private String comment;

    @Column(name = "created_by", nullable = false)
    private String createdBy;

    @Convert(converter = LocalDateTimeConverter.class)
    @Column(name = "new_date", nullable = false)
    private LocalDateTime newDate;

    @Column(name = "type", nullable = false)
    private String type;


    @Column(name = "status", nullable = false)
    private String status;

    @Convert(converter = LocalDateTimeConverter.class)
    @Column(name = "created_on", nullable = false)
    private LocalDateTime createdOn;

    @Convert(converter = LocalDateTimeConverter.class)
    @Column(name = "modified_at", nullable = false)
    private LocalDateTime modifiedAt;

    public CauseStateEntity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CauseEntity getCause() {
        return cause;
    }

    public void setCause(CauseEntity cause) {
        this.cause = cause;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getNewDate() {
        return newDate;
    }

    public void setNewDate(LocalDateTime newDate) {
        this.newDate = newDate;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(LocalDateTime createdOn) {
        this.createdOn = createdOn;
    }

    public LocalDateTime getModifiedAt() {
        return modifiedAt;
    }

    public void setModifiedAt(LocalDateTime modifiedAt) {
        this.modifiedAt = modifiedAt;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public String toString() {
        return "CauseStateEntity{" +
                "id=" + id +
                ", cause=" + cause +
                ", createdBy='" + createdBy + '\'' +
                ", newDate=" + newDate +
                ", type='" + type + '\'' +
                ", status='" + status + '\'' +
                ", createdOn=" + createdOn +
                ", modifiedAt=" + modifiedAt +
                '}';
    }
}

