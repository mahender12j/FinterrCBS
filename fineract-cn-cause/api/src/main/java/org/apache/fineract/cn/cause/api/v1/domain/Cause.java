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
        PENDING, APPROVED, ACTIVE, LOCKED, REJECTED, CLOSED, DELETED, EXTENDED, INACTIVE, UNPUBLISH
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

    private Boolean taxExemption;
    private Boolean resubmitted;
    private Boolean extended;


    //    todo comment out for unused value
//    private Double actualRaisedFiat;
//    private Double actualRaisedFin;
//    @NotNull
//    private Double minAmount;
//    @NotNull
//    private Double maxAmount;
    @NotNull
    private String acceptedDenominationAmounts;
    private Double successFees;

    //    todo for the node side calculation
    private Double finCollLimit;
    private String finRate;


    private String approvedBy;
    private String approvedOn;
    private String createdBy;
    private String createdOn;
    private String lastModifiedBy;
    private String lastModifiedOn;
    @NotNull
    private boolean feeRevisionRequired;


    @Valid
    private CauseCategory causeCategories;
    private String taxExemptionPercentage;
    private String websiteUrl;
    private String smediaLinks;
    private String videoUrls;
    private String causeTxHash;
    @NotNull
    private String accountNumber;
    @NotNull
    private String ethAddress;
    private CauseStatistics causeStatistics;
    private CauseDocument causeDocument;


    //    1a update
    @Valid
    private int causeImplementationDuration;
    @Valid
    private int frequencyCauseImplementationUpdates;

    //    new 1a requirements
    private int totalNumberOfUpdates;
    private int NumberOfUpdateProvided;
    private int NumberOfDaysLeftForNextUpdate;


    private String rejectedReason;

    private LocalDateTime publishDate;

    //    resubmit and extended
    private Long numberOfResubmit;
    private Long numberOfExtended;

    private double avgRating;
    private List<CauseRating> causeRatings;
    private List<CauseFiles> causeFiles;

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

    public Boolean getTaxExemption() {
        return taxExemption;
    }

    public void setTaxExemption(Boolean taxExemption) {
        this.taxExemption = taxExemption;
    }

    public String getAcceptedDenominationAmounts() {
        return acceptedDenominationAmounts;
    }

    public void setAcceptedDenominationAmounts(String acceptedDenominationAmounts) {
        this.acceptedDenominationAmounts = acceptedDenominationAmounts;
    }

    public Double getSuccessFees() {
        return successFees;
    }

    public void setSuccessFees(Double successFees) {
        this.successFees = successFees;
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
        return taxExemption;
    }

    public void setTaxExamption(Boolean taxExamption) {
        taxExemption = taxExamption;
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

    public double getAvgRating() {
        return avgRating;
    }

    public void setAvgRating(double avgRating) {
        this.avgRating = avgRating;
    }
    public Boolean getResubmitted() {
        return resubmitted;
    }

    public void setResubmitted(Boolean resubmitted) {
        this.resubmitted = resubmitted;
    }

    public List<CauseRating> getCauseRatings() {
        return causeRatings;
    }

    public void setCauseRatings(List<CauseRating> causeRatings) {
        this.causeRatings = causeRatings;
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

    public Boolean getResubmited() {
        return resubmitted;
    }

    public void setResubmited(Boolean resubmited) {
        resubmitted = resubmited;
    }

    public Boolean getExtended() {
        return extended;
    }

    public void setExtended(Boolean extended) {
        this.extended = extended;
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
        return feeRevisionRequired;
    }

    public void setFeeRevisionRequired(boolean feeRevisionRequired) {
        this.feeRevisionRequired = feeRevisionRequired;
    }

    public int getTotalNumberOfUpdates() {
        return totalNumberOfUpdates;
    }

    public void setTotalNumberOfUpdates(int totalNumberOfUpdates) {
        this.totalNumberOfUpdates = totalNumberOfUpdates;
    }

    public int getNumberOfUpdateProvided() {
        return NumberOfUpdateProvided;
    }

    public void setNumberOfUpdateProvided(int numberOfUpdateProvided) {
        NumberOfUpdateProvided = numberOfUpdateProvided;
    }

    public int getNumberOfDaysLeftForNextUpdate() {
        return NumberOfDaysLeftForNextUpdate;
    }

    public void setNumberOfDaysLeftForNextUpdate(int numberOfDaysLeftForNextUpdate) {
        NumberOfDaysLeftForNextUpdate = numberOfDaysLeftForNextUpdate;
    }

    @Override
    public String toString() {
        return "Cause{" +
                "identifier='" + identifier + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", startDate='" + startDate + '\'' +
                ", endDate='" + endDate + '\'' +
                ", completedDate='" + completedDate + '\'' +
                ", address=" + address +
                ", currentState='" + currentState + '\'' +
                ", softTarget=" + softTarget +
                ", hardTarget=" + hardTarget +
                ", taxExemption=" + taxExemption +
                ", resubmitted=" + resubmitted +
                ", extended=" + extended +
                ", acceptedDenominationAmounts='" + acceptedDenominationAmounts + '\'' +
                ", successFees=" + successFees +
                ", finCollLimit=" + finCollLimit +
                ", finRate='" + finRate + '\'' +
                ", approvedBy='" + approvedBy + '\'' +
                ", approvedOn='" + approvedOn + '\'' +
                ", createdBy='" + createdBy + '\'' +
                ", createdOn='" + createdOn + '\'' +
                ", lastModifiedBy='" + lastModifiedBy + '\'' +
                ", lastModifiedOn='" + lastModifiedOn + '\'' +
                ", isFeeRevisionRequired=" + feeRevisionRequired +
                ", causeCategories=" + causeCategories +
                ", taxExemptionPercentage='" + taxExemptionPercentage + '\'' +
                ", websiteUrl='" + websiteUrl + '\'' +
                ", smediaLinks='" + smediaLinks + '\'' +
                ", videoUrls='" + videoUrls + '\'' +
                ", causeTxHash='" + causeTxHash + '\'' +
                ", accountNumber='" + accountNumber + '\'' +
                ", ethAddress='" + ethAddress + '\'' +
                ", causeStatistics=" + causeStatistics +
                ", causeDocument=" + causeDocument +
                ", causeImplementationDuration=" + causeImplementationDuration +
                ", frequencyCauseImplementationUpdates=" + frequencyCauseImplementationUpdates +
                ", totalNumberOfUpdates=" + totalNumberOfUpdates +
                ", NumberOfUpdateProvided=" + NumberOfUpdateProvided +
                ", NumberOfDaysLeftForNextUpdate=" + NumberOfDaysLeftForNextUpdate +
                ", rejectedReason='" + rejectedReason + '\'' +
                ", publishDate=" + publishDate +
                ", causeFiles=" + causeFiles +
                ", numberOfResubmit=" + numberOfResubmit +
                ", numberOfExtended=" + numberOfExtended +
                '}';
    }
}

