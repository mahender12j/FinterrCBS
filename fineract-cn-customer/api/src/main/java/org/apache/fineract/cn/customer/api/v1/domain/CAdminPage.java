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

    private Long NoOfNGO;
    private Long ActiveNGO;
    private Long NoOfNGOThisWeek;
    private List<PerMonthRecord> ngoPerMonth;
    private List<PerMonthRecord> inactiveNgoPerMonth;
    private List<PerMonthRecord> activeNgoPerMonth;

    private Long NoOfMember;
    private Long ActiveMember;
    private Long NoOfMemberThisWeek;
    private List<PerMonthRecord> memberPerMonth;
    private List<PerMonthRecord> inactiveMemberPerMonth;
    private List<PerMonthRecord> activeMemberPerMonth;

    private Long NoOfCause;
    private Long ActiveCause;
    private Long NoOfCauseThisWeek;
    private List<PerMonthRecord> causePerMonth;
    private List<PerMonthRecord> inactiveCausePerMonth;
    private List<PerMonthRecord> activeCausePerMonth;

    private Long KycPending;
    private Long KycSubmitted;

    private Long causePending;
    private Long causeSubmitted;

    private Long activePromoCode;
    private Long totalPromoCode;

    public CAdminPage() {
    }

    public Long getNoOfNGO() {
        return NoOfNGO;
    }

    public void setNoOfNGO(Long noOfNGO) {
        NoOfNGO = noOfNGO;
    }

    public Long getActiveNGO() {
        return ActiveNGO;
    }

    public void setActiveNGO(Long activeNGO) {
        ActiveNGO = activeNGO;
    }

    public Long getNoOfNGOThisWeek() {
        return NoOfNGOThisWeek;
    }

    public void setNoOfNGOThisWeek(Long noOfNGOThisWeek) {
        NoOfNGOThisWeek = noOfNGOThisWeek;
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

    public Long getNoOfCause() {
        return NoOfCause;
    }

    public void setNoOfCause(Long noOfCause) {
        NoOfCause = noOfCause;
    }

    public Long getActiveCause() {
        return ActiveCause;
    }

    public void setActiveCause(Long activeCause) {
        ActiveCause = activeCause;
    }

    public Long getNoOfCauseThisWeek() {
        return NoOfCauseThisWeek;
    }

    public void setNoOfCauseThisWeek(Long noOfCauseThisWeek) {
        NoOfCauseThisWeek = noOfCauseThisWeek;
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

    public Long getKycPending() {
        return KycPending;
    }

    public void setKycPending(Long kycPending) {
        KycPending = kycPending;
    }

    public Long getKycSubmitted() {
        return KycSubmitted;
    }

    public void setKycSubmitted(Long kycSubmitted) {
        KycSubmitted = kycSubmitted;
    }

    public Long getCausePending() {
        return causePending;
    }

    public void setCausePending(Long causePending) {
        this.causePending = causePending;
    }

    public Long getCauseSubmitted() {
        return causeSubmitted;
    }

    public void setCauseSubmitted(Long causeSubmitted) {
        this.causeSubmitted = causeSubmitted;
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

    public List<PerMonthRecord> getActiveNgoPerMonth() {
        return activeNgoPerMonth;
    }

    public void setActiveNgoPerMonth(List<PerMonthRecord> activeNgoPerMonth) {
        this.activeNgoPerMonth = activeNgoPerMonth;
    }

    public List<PerMonthRecord> getActiveMemberPerMonth() {
        return activeMemberPerMonth;
    }

    public void setActiveMemberPerMonth(List<PerMonthRecord> activeMemberPerMonth) {
        this.activeMemberPerMonth = activeMemberPerMonth;
    }

    public List<PerMonthRecord> getActiveCausePerMonth() {
        return activeCausePerMonth;
    }

    public void setActiveCausePerMonth(List<PerMonthRecord> activeCausePerMonth) {
        this.activeCausePerMonth = activeCausePerMonth;
    }
}
