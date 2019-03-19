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
import java.util.Objects;

/**
 * @author Padma Raju Sattineni
 */
@Entity
@Table(name = "cass_category")
public class CategoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    //    @OneToOne(fetch = FetchType.LAZY, optional = false)
    //    @JoinColumn(name = "cause_id")
    //    private List<CauseEntity> cause;

    @Column(name = "identifier", nullable = false)
    private String identifier;

    @SuppressWarnings("DefaultAnnotationParam")
    @Column(name = "description", nullable = true)
    private String description;

    @Column(name = "is_active", nullable = false)
    private Boolean active;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "created_on")
    @Convert(converter = LocalDateTimeConverter.class)
    private LocalDateTime createdOn;

    public CategoryEntity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CategoryEntity)) return false;
        CategoryEntity that = (CategoryEntity) o;
        return Objects.equals(getId(), that.getId()) &&
                Objects.equals(getIdentifier(), that.getIdentifier()) &&
                Objects.equals(getDescription(), that.getDescription()) &&
                Objects.equals(getActive(), that.getActive()) &&
                Objects.equals(getCreatedBy(), that.getCreatedBy()) &&
                Objects.equals(getCreatedOn(), that.getCreatedOn());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getIdentifier(), getDescription(), getActive(), getCreatedBy(), getCreatedOn());
    }
}

