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

public class CAdminPage {

    private Long noOfNGO;
    private Long activeNGO;
    private Long noOfNGOThisWeek;
    private List<PerMonthRecord> ngoPerMonth;
    private List<PerMonthRecord> inactiveNgoPerMonth;
    private List<PerMonthRecord> activeNgoPerMonth;

    private Long NoOfMember;
    private Long ActiveMember;
    private Long NoOfMemberThisWeek;
    private List<PerMonthRecord> memberPerMonth;
    private List<PerMonthRecord> inactiveMemberPerMonth;
    private List<PerMonthRecord> activeMemberPerMonth;


    private Long NoOfTrust;
    private Long ActiveTrust;
    private Long NoOfTrustThisWeek;
    private List<PerMonthRecord> trustPerMonth;
    private List<PerMonthRecord> inactiveTrustPerMonth;
    private List<PerMonthRecord> activeTrustPerMonth;


    private Long NoOfCorporate;
    private Long ActiveCorporate;
    private Long NoOfCorporateThisWeek;
    private List<PerMonthRecord> corporatePerMonth;
    private List<PerMonthRecord> inactiveCorporatePerMonth;
    private List<PerMonthRecord> activeCorporatePerMonth;


    private Long NoOfFundManager;
    private Long ActiveFundManager;
    private Long NoOfFundManagerThisWeek;
    private List<PerMonthRecord> fundManagerPerMonth;
    private List<PerMonthRecord> inactiveFundManagerPerMonth;
    private List<PerMonthRecord> activeFundManagerPerMonth;


    private Long noOfCause;
    private Long activeCause;
    private Long noOfCauseThisWeek;
    private List<PerMonthRecord> causePerMonth;
    private List<PerMonthRecord> inactiveCausePerMonth;
    private List<PerMonthRecord> activeCausePerMonth;

    private Long kycApproved;
    private Long kycPending;
    private Long kycRejected;
    private Long kycNotUploaded;

    private Long causePending;
    private Long causeCompleted;

    private Long activePromoCode;
    private Long totalPromoCode;

    public CAdminPage() {
    }

    public Long getNoOfNGO() {
        return noOfNGO;
    }

    public void setNoOfNGO(Long noOfNGO) {
        this.noOfNGO = noOfNGO;
    }

    public Long getActiveNGO() {
        return activeNGO;
    }

    public void setActiveNGO(Long activeNGO) {
        this.activeNGO = activeNGO;
    }

    public Long getNoOfNGOThisWeek() {
        return noOfNGOThisWeek;
    }

    public void setNoOfNGOThisWeek(Long noOfNGOThisWeek) {
        this.noOfNGOThisWeek = noOfNGOThisWeek;
    }

    public List<PerMonthRecord> getNgoPerMonth() {
        return ngoPerMonth;
    }

    public void setNgoPerMonth(List<PerMonthRecord> ngoPerMonth) {
        this.ngoPerMonth = ngoPerMonth;
    }

    public List<PerMonthRecord> getInactiveNgoPerMonth() {
        return inactiveNgoPerMonth;
    }

    public void setInactiveNgoPerMonth(List<PerMonthRecord> inactiveNgoPerMonth) {
        this.inactiveNgoPerMonth = inactiveNgoPerMonth;
    }

    public List<PerMonthRecord> getActiveNgoPerMonth() {
        return activeNgoPerMonth;
    }

    public void setActiveNgoPerMonth(List<PerMonthRecord> activeNgoPerMonth) {
        this.activeNgoPerMonth = activeNgoPerMonth;
    }

    public Long getNoOfMember() {
        return NoOfMember;
    }

    public void setNoOfMember(Long noOfMember) {
        NoOfMember = noOfMember;
    }

    public Long getActiveMember() {
        return ActiveMember;
    }

    public void setActiveMember(Long activeMember) {
        ActiveMember = activeMember;
    }

    public Long getNoOfMemberThisWeek() {
        return NoOfMemberThisWeek;
    }

    public void setNoOfMemberThisWeek(Long noOfMemberThisWeek) {
        NoOfMemberThisWeek = noOfMemberThisWeek;
    }

    public List<PerMonthRecord> getMemberPerMonth() {
        return memberPerMonth;
    }

    public void setMemberPerMonth(List<PerMonthRecord> memberPerMonth) {
        this.memberPerMonth = memberPerMonth;
    }

    public List<PerMonthRecord> getInactiveMemberPerMonth() {
        return inactiveMemberPerMonth;
    }

    public void setInactiveMemberPerMonth(List<PerMonthRecord> inactiveMemberPerMonth) {
        this.inactiveMemberPerMonth = inactiveMemberPerMonth;
    }

    public List<PerMonthRecord> getActiveMemberPerMonth() {
        return activeMemberPerMonth;
    }

    public void setActiveMemberPerMonth(List<PerMonthRecord> activeMemberPerMonth) {
        this.activeMemberPerMonth = activeMemberPerMonth;
    }

    public Long getNoOfCause() {
        return noOfCause;
    }

    public void setNoOfCause(Long noOfCause) {
        this.noOfCause = noOfCause;
    }

    public Long getActiveCause() {
        return activeCause;
    }

    public void setActiveCause(Long activeCause) {
        this.activeCause = activeCause;
    }

    public Long getNoOfCauseThisWeek() {
        return noOfCauseThisWeek;
    }

    public void setNoOfCauseThisWeek(Long noOfCauseThisWeek) {
        this.noOfCauseThisWeek = noOfCauseThisWeek;
    }

    public List<PerMonthRecord> getCausePerMonth() {
        return causePerMonth;
    }

    public void setCausePerMonth(List<PerMonthRecord> causePerMonth) {
        this.causePerMonth = causePerMonth;
    }

    public List<PerMonthRecord> getInactiveCausePerMonth() {
        return inactiveCausePerMonth;
    }

    public void setInactiveCausePerMonth(List<PerMonthRecord> inactiveCausePerMonth) {
        this.inactiveCausePerMonth = inactiveCausePerMonth;
    }

    public List<PerMonthRecord> getActiveCausePerMonth() {
        return activeCausePerMonth;
    }

    public void setActiveCausePerMonth(List<PerMonthRecord> activeCausePerMonth) {
        this.activeCausePerMonth = activeCausePerMonth;
    }

    public Long getKycApproved() {
        return kycApproved;
    }

    public void setKycApproved(Long kycApproved) {
        this.kycApproved = kycApproved;
    }

    public Long getKycPending() {
        return kycPending;
    }

    public void setKycPending(Long kycPending) {
        this.kycPending = kycPending;
    }

    public Long getKycRejected() {
        return kycRejected;
    }

    public void setKycRejected(Long kycRejected) {
        this.kycRejected = kycRejected;
    }

    public Long getKycNotUploaded() {
        return kycNotUploaded;
    }

    public void setKycNotUploaded(Long kycNotUploaded) {
        this.kycNotUploaded = kycNotUploaded;
    }

    public Long getCausePending() {
        return causePending;
    }

    public void setCausePending(Long causePending) {
        this.causePending = causePending;
    }

    public Long getCauseCompleted() {
        return causeCompleted;
    }

    public void setCauseCompleted(Long causeCompleted) {
        this.causeCompleted = causeCompleted;
    }

    public Long getActivePromoCode() {
        return activePromoCode;
    }

    public void setActivePromoCode(Long activePromoCode) {
        this.activePromoCode = activePromoCode;
    }

    public Long getTotalPromoCode() {
        return totalPromoCode;
    }

    public void setTotalPromoCode(Long totalPromoCode) {
        this.totalPromoCode = totalPromoCode;
    }

    public Long getNoOfTrust() {
        return NoOfTrust;
    }

    public void setNoOfTrust(Long noOfTrust) {
        NoOfTrust = noOfTrust;
    }

    public Long getActiveTrust() {
        return ActiveTrust;
    }

    public void setActiveTrust(Long activeTrust) {
        ActiveTrust = activeTrust;
    }

    public Long getNoOfTrustThisWeek() {
        return NoOfTrustThisWeek;
    }

    public void setNoOfTrustThisWeek(Long noOfTrustThisWeek) {
        NoOfTrustThisWeek = noOfTrustThisWeek;
    }

    public List<PerMonthRecord> getTrustPerMonth() {
        return trustPerMonth;
    }

    public void setTrustPerMonth(List<PerMonthRecord> trustPerMonth) {
        this.trustPerMonth = trustPerMonth;
    }

    public List<PerMonthRecord> getInactiveTrustPerMonth() {
        return inactiveTrustPerMonth;
    }

    public void setInactiveTrustPerMonth(List<PerMonthRecord> inactiveTrustPerMonth) {
        this.inactiveTrustPerMonth = inactiveTrustPerMonth;
    }

    public List<PerMonthRecord> getActiveTrustPerMonth() {
        return activeTrustPerMonth;
    }

    public void setActiveTrustPerMonth(List<PerMonthRecord> activeTrustPerMonth) {
        this.activeTrustPerMonth = activeTrustPerMonth;
    }

    public Long getNoOfCorporate() {
        return NoOfCorporate;
    }

    public void setNoOfCorporate(Long noOfCorporate) {
        NoOfCorporate = noOfCorporate;
    }

    public Long getActiveCorporate() {
        return ActiveCorporate;
    }

    public void setActiveCorporate(Long activeCorporate) {
        ActiveCorporate = activeCorporate;
    }

    public Long getNoOfCorporateThisWeek() {
        return NoOfCorporateThisWeek;
    }

    public void setNoOfCorporateThisWeek(Long noOfCorporateThisWeek) {
        NoOfCorporateThisWeek = noOfCorporateThisWeek;
    }

    public List<PerMonthRecord> getCorporatePerMonth() {
        return corporatePerMonth;
    }

    public void setCorporatePerMonth(List<PerMonthRecord> corporatePerMonth) {
        this.corporatePerMonth = corporatePerMonth;
    }

    public List<PerMonthRecord> getInactiveCorporatePerMonth() {
        return inactiveCorporatePerMonth;
    }

    public void setInactiveCorporatePerMonth(List<PerMonthRecord> inactiveCorporatePerMonth) {
        this.inactiveCorporatePerMonth = inactiveCorporatePerMonth;
    }

    public List<PerMonthRecord> getActiveCorporatePerMonth() {
        return activeCorporatePerMonth;
    }

    public void setActiveCorporatePerMonth(List<PerMonthRecord> activeCorporatePerMonth) {
        this.activeCorporatePerMonth = activeCorporatePerMonth;
    }

    public Long getNoOfFundManager() {
        return NoOfFundManager;
    }

    public void setNoOfFundManager(Long noOfFundManager) {
        NoOfFundManager = noOfFundManager;
    }

    public Long getActiveFundManager() {
        return ActiveFundManager;
    }

    public void setActiveFundManager(Long activeFundManager) {
        ActiveFundManager = activeFundManager;
    }

    public Long getNoOfFundManagerThisWeek() {
        return NoOfFundManagerThisWeek;
    }

    public void setNoOfFundManagerThisWeek(Long noOfFundManagerThisWeek) {
        NoOfFundManagerThisWeek = noOfFundManagerThisWeek;
    }

    public List<PerMonthRecord> getFundManagerPerMonth() {
        return fundManagerPerMonth;
    }

    public void setFundManagerPerMonth(List<PerMonthRecord> fundManagerPerMonth) {
        this.fundManagerPerMonth = fundManagerPerMonth;
    }

    public List<PerMonthRecord> getInactiveFundManagerPerMonth() {
        return inactiveFundManagerPerMonth;
    }

    public void setInactiveFundManagerPerMonth(List<PerMonthRecord> inactiveFundManagerPerMonth) {
        this.inactiveFundManagerPerMonth = inactiveFundManagerPerMonth;
    }

    public List<PerMonthRecord> getActiveFundManagerPerMonth() {
        return activeFundManagerPerMonth;
    }

    public void setActiveFundManagerPerMonth(List<PerMonthRecord> activeFundManagerPerMonth) {
        this.activeFundManagerPerMonth = activeFundManagerPerMonth;
    }

    @Override
    public String toString() {
        return "CAdminPage{" +
                "noOfNGO=" + noOfNGO +
                ", activeNGO=" + activeNGO +
                ", noOfNGOThisWeek=" + noOfNGOThisWeek +
                ", ngoPerMonth=" + ngoPerMonth +
                ", inactiveNgoPerMonth=" + inactiveNgoPerMonth +
                ", activeNgoPerMonth=" + activeNgoPerMonth +
                ", NoOfMember=" + NoOfMember +
                ", ActiveMember=" + ActiveMember +
                ", NoOfMemberThisWeek=" + NoOfMemberThisWeek +
                ", memberPerMonth=" + memberPerMonth +
                ", inactiveMemberPerMonth=" + inactiveMemberPerMonth +
                ", activeMemberPerMonth=" + activeMemberPerMonth +
                ", NoOfTrust=" + NoOfTrust +
                ", ActiveTrust=" + ActiveTrust +
                ", NoOfTrustThisWeek=" + NoOfTrustThisWeek +
                ", trustPerMonth=" + trustPerMonth +
                ", inactiveTrustPerMonth=" + inactiveTrustPerMonth +
                ", activeTrustPerMonth=" + activeTrustPerMonth +
                ", NoOfCorporate=" + NoOfCorporate +
                ", ActiveCorporate=" + ActiveCorporate +
                ", NoOfCorporateThisWeek=" + NoOfCorporateThisWeek +
                ", corporatePerMonth=" + corporatePerMonth +
                ", inactiveCorporatePerMonth=" + inactiveCorporatePerMonth +
                ", activeCorporatePerMonth=" + activeCorporatePerMonth +
                ", NoOfFundManager=" + NoOfFundManager +
                ", ActiveFundManager=" + ActiveFundManager +
                ", NoOfFundManagerThisWeek=" + NoOfFundManagerThisWeek +
                ", fundManagerPerMonth=" + fundManagerPerMonth +
                ", inactiveFundManagerPerMonth=" + inactiveFundManagerPerMonth +
                ", activeFundManagerPerMonth=" + activeFundManagerPerMonth +
                ", noOfCause=" + noOfCause +
                ", activeCause=" + activeCause +
                ", noOfCauseThisWeek=" + noOfCauseThisWeek +
                ", causePerMonth=" + causePerMonth +
                ", inactiveCausePerMonth=" + inactiveCausePerMonth +
                ", activeCausePerMonth=" + activeCausePerMonth +
                ", kycApproved=" + kycApproved +
                ", kycPending=" + kycPending +
                ", kycRejected=" + kycRejected +
                ", kycNotUploaded=" + kycNotUploaded +
                ", causePending=" + causePending +
                ", causeCompleted=" + causeCompleted +
                ", activePromoCode=" + activePromoCode +
                ", totalPromoCode=" + totalPromoCode +
                '}';
    }
}
