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
package org.apache.fineract.cn.cause.api.v1.domain;


import org.hibernate.validator.constraints.NotBlank;

import javax.validation.Valid;
import java.util.List;

public final class CauseUpdate {

    @NotBlank
    @Valid
    private String title;
    @NotBlank
    @Valid
    private String description;
    @NotBlank
    @Valid
    private double amountSpend;

    private String updatedAt;
    private String createOn;

    private List<CauseUpdatePage> causeUpdatePages;

    public CauseUpdate() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getAmountSpend() {
        return amountSpend;
    }

    public void setAmountSpend(double amountSpend) {
        this.amountSpend = amountSpend;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getCreateOn() {
        return createOn;
    }

    public void setCreateOn(String createOn) {
        this.createOn = createOn;
    }

    public List<CauseUpdatePage> getCauseUpdatePages() {
        return causeUpdatePages;
    }

    public void setCauseUpdatePages(List<CauseUpdatePage> causeUpdatePages) {
        this.causeUpdatePages = causeUpdatePages;
    }

    @Override
    public String toString() {
        return "CauseUpdate{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", amount_spend=" + amountSpend +
                '}';
    }
}
