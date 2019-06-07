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
}
