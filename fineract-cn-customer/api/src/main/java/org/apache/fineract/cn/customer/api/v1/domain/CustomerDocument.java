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

import java.util.Objects;
import org.apache.fineract.cn.lang.validation.constraints.ValidIdentifier;
import org.hibernate.validator.constraints.Length;

/**
 * @author Myrle Krantz
 */
public class CustomerDocument {

  public enum Status {
    NOTUPLOADED,
    PENDING,
    APPROVED,
    REJECTED
  }

  @ValidIdentifier
  private String identifier;

  @Length(max = 4096)
  private String description;

  private boolean completed;
  private String createdBy;
  private String createdOn;
  
  private Status status;
  private String approvedBy;
  private String approvedOn;
  private String reasonForReject;
  private String rejectedBy;
  private String rejectedOn;

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

  public boolean isCompleted() {
    return completed;
  }

  public void setCompleted(boolean completed) {
    this.completed = completed;
  }

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
  
  public String getStatus() {
    return this.status != null ? this.status.name() : null;
  }

  public void setStatus(final String status) {
    this.status = Status.valueOf(status);
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

  public String getRejectedOn() {
	return rejectedOn;
  }

  public void setRejectedOn(String rejectedOn) {
	this.rejectedOn = rejectedOn;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    CustomerDocument that = (CustomerDocument) o;
    return completed == that.completed &&
        Objects.equals(identifier, that.identifier) &&
        Objects.equals(description, that.description);
  }

  @Override
  public int hashCode() {
    return Objects.hash(identifier, description, completed);
  }

  @Override
  public String toString() {
	return "CustomerDocument [identifier=" + identifier + ", description=" + description + ", completed="
				+ completed + ", createdBy=" + createdBy + ", createdOn=" + createdOn + ", status=" + status
				+ ", approvedBy=" + approvedBy + ", approvedOn=" + approvedOn + ", reasonForReject=" + reasonForReject
				+ ", rejectedBy=" + rejectedBy + ", rejectedOn=" + rejectedOn + ", toString()=" + super.toString()
				+ "]";
  }
}
