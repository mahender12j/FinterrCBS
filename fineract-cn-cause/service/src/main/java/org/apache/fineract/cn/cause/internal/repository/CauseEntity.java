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

import org.apache.fineract.cn.mariadb.util.LocalDateConverter;
import org.apache.fineract.cn.mariadb.util.LocalDateTimeConverter;
import javax.persistence.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author Padma Raju Sattineni
 */

@Entity
@Table(name = "cass_causes")
public class CauseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "identifier")
    private String identifier;
    @Column(name = "title")
    private String title;
    @Column(name = "description")
    private String description;
    @Column(name = "start_date")
    @Convert(converter = LocalDateTimeConverter.class)
    private LocalDateTime startDate;
    @Column(name = "end_date")
    @Convert(converter = LocalDateTimeConverter.class)
    private LocalDateTime endDate;
    @Column(name = "completed_date")
    @Convert(converter = LocalDateTimeConverter.class)
    private LocalDateTime completedDate;
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id")
    private AddressEntity address;

    @Column(name = "current_state")
    private String currentState;
    @Column(name = "soft_target")
    private Double softTarget;
    @Column(name = "hard_target")
    private Double hardTarget;

    @Column(name = "is_tax_examption", nullable = false)
    private Boolean isTaxExamption;
    @Column(name = "actual_raised_fiat")
    private Double actualRaisedFiat;
    @Column(name = "actual_raised_fin")
    private Double actualRaisedFin;
    @Column(name = "min_amount")
    private Double minAmount;
    @Column(name = "max_amount")
    private Double maxAmount;
    @Column(name = "accepted_denomination_amounts")
    private String acceptedDenominationAmounts;
    @Column(name = "management_fee")
    private Double managementFee;
    @Column(name = "fin_coll_limit")
    private Double finCollLimit;
    @Column(name = "fin_rate")
    private String finRate;

    @Column(name = "approved_by")
    private String approvedBy;
    @Column(name = "approved_on")
    @Convert(converter = LocalDateTimeConverter.class)
    private LocalDateTime approvedOn;
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

    @Column(name = "tax_exemption_percentage")
    private String taxExemptionPercentage;
    @Column(name = "website_url")
    private String websiteUrl;
    @Column(name = "s_media_links")
    private String sMediaLinks;
    @Column(name = "video_urls")
	private String videoUrls;

    public CauseEntity() {
        super();
    }

    public Long getId() {
        return this.id;
    }

    public void setId(final Long id) {
        this.id = id;
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

	public LocalDateTime getStartDate() {
		return startDate;
	}

	public void setStartDate(LocalDateTime startDate) {
		this.startDate = startDate;
	}

	public LocalDateTime getEndDate() {
		return endDate;
	}

	public void setEndDate(LocalDateTime endDate) {
		this.endDate = endDate;
	}

	public LocalDateTime getCompletedDate() {
		return completedDate;
	}

	public void setCompletedDate(LocalDateTime completedDate) {
		this.completedDate = completedDate;
    }
    
    public AddressEntity getAddress() {
        return this.address;
    }

    public void setAddress(final AddressEntity address) {
        this.address = address;
    }

    public String getCurrentState() {
        return this.currentState;
    }

    public void setCurrentState(final String currentState) {
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
        return this.approvedBy;
    }

    public void setApprovedBy(final String approvedBy) {
        this.approvedBy = approvedBy;
    }

    public LocalDateTime getApprovedOn() {
        return this.approvedOn;
    }

    public void setApprovedOn(final LocalDateTime approvedOn) {
        this.approvedOn = approvedOn;
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

	public String getSMediaLinks() {
		return sMediaLinks;
	}

	public void setSMediaLinks(String sMediaLinks) {
		this.sMediaLinks = sMediaLinks;
	}

	public String getVideoUrls() {
		return videoUrls;
	}

	public void setVideoUrls(String videoUrls) {
		this.videoUrls = videoUrls;
	}

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CauseEntity that = (CauseEntity) o;

        return identifier.equals(that.identifier);

    }

    @Override
    public int hashCode() {
        return identifier.hashCode();
    }
}

