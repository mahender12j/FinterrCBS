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

import org.apache.fineract.cn.lang.DateOfBirth;
import org.hibernate.validator.constraints.NotBlank;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author Padma Raju Sattineni
 */

public final class Cause {

    public enum State {
        PENDING,
        ACTIVE,
        LOCKED,
        REJECTED,
        CLOSED
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

    public Cause() {
        super();
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
				+ lastModifiedOn + ", toString()=" + super.toString() + "]";
	}

}

