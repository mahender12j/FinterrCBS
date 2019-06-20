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
package org.apache.fineract.cn.cause.internal.command;

import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

public class CreateCauseCommandResponse {

    @NotBlank
    private String identifier;
    @NotBlank
    private String title;
    @NotBlank
    private String description;
    @NotBlank
    private String startDate;
    @NotBlank
    private String endDate;
    @NotBlank
    private String currentState;
    @NotNull
    private Double softTarget;
    @NotNull
    private Double hardTarget;

    private Boolean isTaxExamption;
    private Double minAmount;
    private Double maxAmount;
    private String acceptedDenominationAmounts;
    private String createdBy;
    private String createdOn;
    @NotNull
    private boolean isFeeRevisionRequired;

    private String taxExemptionPercentage;
    private String websiteUrl;
    private String smediaLinks;
    private String videoUrls;
    private String causeTxHash;
    private String accountNumber;
    private String ethAddress;
    private String rejectedReason;


    public CreateCauseCommandResponse() {
    }


    public CreateCauseCommandResponse(String identifier, String title, String description, String startDate, String endDate, String currentState, Double softTarget, Double hardTarget, Boolean isTaxExamption, Double minAmount, Double maxAmount, String acceptedDenominationAmounts, String createdBy, String createdOn, boolean isFeeRevisionRequired, String taxExemptionPercentage, String websiteUrl, String smediaLinks, String videoUrls, String causeTxHash, String accountNumber, String ethAddress, String rejectedReason) {
        this.identifier = identifier;
        this.title = title;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.currentState = currentState;
        this.softTarget = softTarget;
        this.hardTarget = hardTarget;
        this.isTaxExamption = isTaxExamption;
        this.minAmount = minAmount;
        this.maxAmount = maxAmount;
        this.acceptedDenominationAmounts = acceptedDenominationAmounts;
        this.createdBy = createdBy;
        this.createdOn = createdOn;
        this.isFeeRevisionRequired = isFeeRevisionRequired;
        this.taxExemptionPercentage = taxExemptionPercentage;
        this.websiteUrl = websiteUrl;
        this.smediaLinks = smediaLinks;
        this.videoUrls = videoUrls;
        this.causeTxHash = causeTxHash;
        this.accountNumber = accountNumber;
        this.ethAddress = ethAddress;
        this.rejectedReason = rejectedReason;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public String getCurrentState() {
        return currentState;
    }

    public Double getSoftTarget() {
        return softTarget;
    }

    public Double getHardTarget() {
        return hardTarget;
    }

    public Boolean getTaxExamption() {
        return isTaxExamption;
    }

    public Double getMinAmount() {
        return minAmount;
    }

    public Double getMaxAmount() {
        return maxAmount;
    }

    public String getAcceptedDenominationAmounts() {
        return acceptedDenominationAmounts;
    }


    public String getCreatedBy() {
        return createdBy;
    }

    public String getCreatedOn() {
        return createdOn;
    }


    public boolean isFeeRevisionRequired() {
        return isFeeRevisionRequired;
    }

    public String getTaxExemptionPercentage() {
        return taxExemptionPercentage;
    }

    public String getWebsiteUrl() {
        return websiteUrl;
    }

    public String getSmediaLinks() {
        return smediaLinks;
    }

    public String getVideoUrls() {
        return videoUrls;
    }

    public String getCauseTxHash() {
        return causeTxHash;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getEthAddress() {
        return ethAddress;
    }

    public String getRejectedReason() {
        return rejectedReason;
    }
}
