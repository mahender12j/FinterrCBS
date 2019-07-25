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

import org.apache.fineract.cn.lang.validation.constraints.ValidIdentifier;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author MD ROBIUL HASSAN
 */
public class CustomerDocumentApproval {

    public enum Status {
        APPROVED,
        REJECTED
    }

    @NotNull
    private Long id;
    @NotNull
    private Status status;
    private String rejectedReason;

    public CustomerDocumentApproval() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStatus() {
        return status.name();
    }

    public void setStatus(String status) {
        this.status = Status.valueOf(status);
    }

    public String getRejectedReason() {
        return rejectedReason;
    }

    public void setRejectedReason(String rejectedReason) {
        this.rejectedReason = rejectedReason;
    }

    @Override
    public String toString() {
        return "CustomerDocumentApproval{" +
                "id='" + id + '\'' +
                ", status=" + status +
                ", rejectedReason='" + rejectedReason + '\'' +
                '}';
    }
}
