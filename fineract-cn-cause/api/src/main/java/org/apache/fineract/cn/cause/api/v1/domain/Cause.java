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
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Padma Raju Sattineni
 */

public class Cause {

    public enum State {
        PENDING, APPROVED, ACTIVE, LOCKED, REJECTED, CLOSED, DELETED, EXTENDED, INACTIVE
    }

    public enum RemovableCauseState {
        APPROVED, PENDING, REJECTED, INACTIVE
    }

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
    private String completedDate;

    @NotNull
    @Valid
    private Address address;
    @NotBlank
    private String currentState;
    @NotNull
    private Double softTarget;
    @NotNull
    private Double hardTarget;

    private Boolean isTaxExamption;
    private Boolean isResubmited;
    private Boolean isExtended;

    private Double actualRaisedFiat;
    private Double actualRaisedFin;
    private Double minAmount;
    private Double maxAmount;
    private String acceptedDenominationAmounts;
    private Double managementFee;
    private Double finCollLimit;
    private String finRate;
    private String approvedBy;
    private String approvedOn;
    private String createdBy;
    private String createdOn;
    private String lastModifiedBy;
    private String lastModifiedOn;
    @NotNull
    private boolean isFeeRevisionRequired;


    @Valid
    private CauseCategory causeCategories;
    private String taxExemptionPercentage;
    private String websiteUrl;
    private String smediaLinks;
    private String videoUrls;
    private String avgRating;
    private String causeTxHash;
    private String accountNumber;
    private String ethAddress;
    private CauseStatistics causeStatistics;
    private CauseDocument causeDocument;


    //    1a update
    @NotNull
    @Valid
    private int causeImplementationDuration;
    @NotNull
    @Valid
    private int frequencyCauseImplementationUpdates;

    private String rejectedReason;

    private LocalDateTime publishDate;
    private List<CauseRating> causeRatingList;
    private List<CauseFiles> causeFiles;

    //    resubmit and extended
    private Long numberOfResubmit;
    private Long numberOfExtended;


    public Cause() {
        super();
    }


    public CauseStatistics getCauseStatistics() {
        return causeStatistics;
    }

    public void setCauseStatistics(CauseStatistics causeStatistics) {
        this.causeStatistics = causeStatistics;
    }

    public String getIdentifier() {
        return this.identifier;
    }

    public void setIdentifier(final String identifier) {
        this.identifier = identifier;
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

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getCompletedDate() {
        return completedDate;
    }

    public void setCompletedDate(String completedDate) {
        this.completedDate = completedDate;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getCurrentState() {
        return currentState;
    }

    public void setCurrentState(String currentState) {
        this.currentState = currentState;
    }

    public Double getSoftTarget() {
        return softTarget;
    }

    public void setSoftTarget(Double softTarget) {
        this.softTarget = softTarget;
    }

    public Double getHardTarget() {
        return hardTarget;
    }

    public void setHardTarget(Double hardTarget) {
        this.hardTarget = hardTarget;
    }

    public Boolean getIsTaxExamption() {
        return isTaxExamption;
    }

    public void setIsTaxExamption(Boolean isTaxExamption) {
        this.isTaxExamption = isTaxExamption;
    }

    public Double getActualRaisedFiat() {
        return actualRaisedFiat;
    }

    public void setActualRaisedFiat(Double actualRaisedFiat) {
        this.actualRaisedFiat = actualRaisedFiat;
    }

    public Double getActualRaisedFin() {
        return actualRaisedFin;
    }

    public void setActualRaisedFin(Double actualRaisedFin) {
        this.actualRaisedFin = actualRaisedFin;
    }

    public Double getMinAmount() {
        return minAmount;
    }

    public void setMinAmount(Double minAmount) {
        this.minAmount = minAmount;
    }

    public Double getMaxAmount() {
        return maxAmount;
    }

    public void setMaxAmount(Double maxAmount) {
        this.maxAmount = maxAmount;
    }

    public String getAcceptedDenominationAmounts() {
        return acceptedDenominationAmounts;
    }

    public void setAcceptedDenominationAmounts(String acceptedDenominationAmounts) {
        this.acceptedDenominationAmounts = acceptedDenominationAmounts;
    }

    public Double getManagementFee() {
        return managementFee;
    }

    public void setManagementFee(Double managementFee) {
        this.managementFee = managementFee;
    }

    public Double getFinCollLimit() {
        return finCollLimit;
    }

    public void setFinCollLimit(Double finCollLimit) {
        this.finCollLimit = finCollLimit;
    }

    public String getFinRate() {
        return finRate;
    }

    public void setFinRate(String finRate) {
        this.finRate = finRate;
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

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getCreatedOn() {
        return this.createdOn;
    }

    public void setCreatedOn(final String createdOn) {
        this.createdOn = createdOn;
    }

    public String getLastModifiedBy() {
        return this.lastModifiedBy;
    }

    public void setLastModifiedBy(final String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public String getLastModifiedOn() {
        return this.lastModifiedOn;
    }

    public void setLastModifiedOn(final String lastModifiedOn) {
        this.lastModifiedOn = lastModifiedOn;
    }

    public Boolean getTaxExamption() {
        return isTaxExamption;
    }

    public void setTaxExamption(Boolean taxExamption) {
        isTaxExamption = taxExamption;
    }

    public CauseCategory getCauseCategories() {
        return causeCategories;
    }

    public void setCauseCategories(CauseCategory causeCategories) {
        this.causeCategories = causeCategories;
    }

    public String getTaxExemptionPercentage() {
        return taxExemptionPercentage;
    }

    public void setTaxExemptionPercentage(String taxExemptionPercentage) {
        this.taxExemptionPercentage = taxExemptionPercentage;
    }

    public String getWebsiteUrl() {
        return websiteUrl;
    }

    public void setWebsiteUrl(String websiteUrl) {
        this.websiteUrl = websiteUrl;
    }

    public String getSmediaLinks() {
        return smediaLinks;
    }

    public void setSmediaLinks(String smediaLinks) {
        this.smediaLinks = smediaLinks;
    }

    public String getVideoUrls() {
        return videoUrls;
    }

    public void setVideoUrls(String videoUrls) {
        this.videoUrls = videoUrls;
    }

    public String getAvgRating() {
        return avgRating;
    }

    public void setAvgRating(String avgRating) {
        this.avgRating = avgRating;
    }

    public String getCauseTxHash() {
        return causeTxHash;
    }

    public void setCauseTxHash(String causeTxHash) {
        this.causeTxHash = causeTxHash;
    }

    public String getAccountNumber() {
        return this.accountNumber;
    }

    public void setAccountNumber(final String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getEthAddress() {
        return this.ethAddress;
    }

    public void setEthAddress(final String ethAddress) {
        this.ethAddress = ethAddress;
    }

    public CauseDocument getCauseDocument() {
        return causeDocument;
    }

    public LocalDateTime getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(LocalDateTime publishDate) {
        this.publishDate = publishDate;
    }

    public void setCauseDocument(CauseDocument causeDocument) {
        this.causeDocument = causeDocument;
    }

    public List<CauseRating> getCauseRatingList() {
        return causeRatingList;
    }

    public void setCauseRatingList(List<CauseRating> causeRatingList) {
        this.causeRatingList = causeRatingList;
    }

    public Boolean getResubmited() {
        return isResubmited;
    }

    public void setResubmited(Boolean resubmited) {
        isResubmited = resubmited;
    }

    public Boolean getExtended() {
        return isExtended;
    }

    public void setExtended(Boolean extended) {
        isExtended = extended;
    }

    public String getRejectedReason() {
        return rejectedReason;
    }

    public int getCauseImplementationDuration() {
        return causeImplementationDuration;
    }

    public void setCauseImplementationDuration(int causeImplementationDuration) {
        this.causeImplementationDuration = causeImplementationDuration;
    }

    public int getFrequencyCauseImplementationUpdates() {
        return frequencyCauseImplementationUpdates;
    }

    public void setFrequencyCauseImplementationUpdates(int frequencyCauseImplementationUpdates) {
        this.frequencyCauseImplementationUpdates = frequencyCauseImplementationUpdates;
    }

    public void setRejectedReason(String rejectedReason) {
        this.rejectedReason = rejectedReason;
    }

    public List<CauseFiles> getCauseFiles() {
        return causeFiles;
    }

    public void setCauseFiles(List<CauseFiles> causeFiles) {
        this.causeFiles = causeFiles;
    }

    public Long getNumberOfResubmit() {
        return numberOfResubmit;
    }

    public void setNumberOfResubmit(Long numberOfResubmit) {
        this.numberOfResubmit = numberOfResubmit;
    }

    public Long getNumberOfExtended() {
        return numberOfExtended;
    }

    public void setNumberOfExtended(Long numberOfExtended) {
        this.numberOfExtended = numberOfExtended;
    }

    public boolean isFeeRevisionRequired() {
        return isFeeRevisionRequired;
    }

    public void setFeeRevisionRequired(boolean feeRevisionRequired) {
        isFeeRevisionRequired = feeRevisionRequired;
    }

    @Override
    public String toString() {
        return "Cause [identifier=" + identifier + ", title=" + title + ", description=" + description + ", startDate="
                + startDate + ", endDate=" + endDate + ", completedDate=" + completedDate + ", softTarget=" + softTarget
                + ", hardTarget=" + hardTarget + ", isTaxExamption=" + isTaxExamption + ", actualRaisedFiat="
                + actualRaisedFiat + ", actualRaisedFin=" + actualRaisedFin + ", minAmount=" + minAmount
                + ", maxAmount=" + maxAmount + ", acceptedDenominationAmounts=" + acceptedDenominationAmounts
                + ", managementFee=" + managementFee + ", finCollLimit=" + finCollLimit + ", finRate=" + finRate
                + ", approvedBy=" + approvedBy + ", approvedOn=" + approvedOn + ", createdBy=" + createdBy
                + ", createdOn=" + createdOn + ", lastModifiedBy=" + lastModifiedBy + ", lastModifiedOn="
                + lastModifiedOn + ", taxExemptionPercentage=" + taxExemptionPercentage + ", websiteUrl=" + websiteUrl
                + ", smediaLinks=" + smediaLinks + ", videoUrls=" + videoUrls + ", avgRating=" + avgRating
                + ", causeTxHash=" + causeTxHash + ", accountNumber=" + accountNumber + ", ethAddress=" + ethAddress
                + " , toString()=" + super.toString() + "]";
    }
}

