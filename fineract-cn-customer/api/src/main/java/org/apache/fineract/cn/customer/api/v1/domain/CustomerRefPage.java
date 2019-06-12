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

public class CustomerRefPage {

    private List<Customer> customers;
    private Integer totalPages;
    private Long totalElements;
    private String refAccountNumber;
    private Double refferalBalance;
    private String customerEmail;
    private SocialMatrix socialMatrix;

    public CustomerRefPage() {
        super();
    }

    public List<Customer> getCustomers() {
        return this.customers;
    }

    public void setCustomers(final List<Customer> customers) {
        this.customers = customers;
    }

    public Integer getTotalPages() {
        return this.totalPages;
    }

    public void setTotalPages(final Integer totalPages) {
        this.totalPages = totalPages;
    }

    public Long getTotalElements() {
        return this.totalElements;
    }

    public void setTotalElements(final Long totalElements) {
        this.totalElements = totalElements;
    }

    public String getReffAccountNumber() {
        return this.refAccountNumber;
    }

    public void setRefAccountNumber(final String refAccountNumber) {
        this.refAccountNumber = refAccountNumber;
    }

    public Double getRefferalBalance() {
        return this.refferalBalance;
    }

    public void setRefferalBalance(final Double refferalBalance) {
        this.refferalBalance = refferalBalance;
    }

    public String getRefAccountNumber() {
        return refAccountNumber;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public SocialMatrix getSocialMatrix() {
        return socialMatrix;
    }

    public void setSocialMatrix(SocialMatrix socialMatrix) {
        this.socialMatrix = socialMatrix;
    }
}
