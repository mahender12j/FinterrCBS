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

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.apache.fineract.cn.mariadb.util.LocalDateConverter;
import org.apache.fineract.cn.mariadb.util.LocalDateTimeConverter;

@Entity
@Table(name = "maat_aml_details")
public class AmlDetailEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;
  @ManyToOne(fetch = FetchType.LAZY, optional = true)
  @JoinColumn(name = "customer_id")
  private CustomerEntity customer;
  @Column(name = "status")
  private String status;
  @Column(name = "aml_object")
  private String amlObject;
  @Column(name = "created_by")
  private String createdBy;
  @Column(name = "created_on")
  @Convert(converter = LocalDateTimeConverter.class)
  private LocalDateTime createdOn;
  @Column(name = "last_modified_by")
  private String lastModifiedBy;
  @Column(name = "last_modified_on")
  @Convert(converter = LocalDateTimeConverter.class)
  private LocalDateTime lastModifiedOn;

  public AmlDetailEntity() {
    super();
  }

  public Long getId() {
    return this.id;
  }

  public void setId(final Long id) {
    this.id = id;
  }

  public CustomerEntity getCustomer() {
    return this.customer;
  }

  public void setCustomer(final CustomerEntity customer) {
    this.customer = customer;
  }

  public String getStatus() {
    return this.status;
  }

  public void setStatus(final String status) {
    this.status = status;
  }

  public String getAmlObject() {
    return this.amlObject;
  }

  public void setAmlObject(final String amlObject) {
    this.amlObject = amlObject;
  }

  public String getCreatedBy() {
    return this.createdBy;
  }

  public void setCreatedBy(final String createdBy) {
    this.createdBy = createdBy;
  }

  public LocalDateTime getCreatedOn() {
    return this.createdOn;
  }

  public void setCreatedOn(final LocalDateTime createdOn) {
    this.createdOn = createdOn;
  }

  public String getLastModifiedBy() {
    return this.lastModifiedBy;
  }

  public void setLastModifiedBy(final String lastModifiedBy) {
    this.lastModifiedBy = lastModifiedBy;
  }

  public LocalDateTime getLastModifiedOn() {
    return this.lastModifiedOn;
  }

  public void setLastModifiedOn(final LocalDateTime lastModifiedOn) {
    this.lastModifiedOn = lastModifiedOn;
  }
}

