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

import java.util.List;

public class CaAdminCauseData {
    private Long noOfCause;
    private Long activeCause;
    private Long noOfCauseThisWeek;

    private List<PerMonthRecord> causePerMonth;
    private List<PerMonthRecord> inactiveCausePerMonth;
    private List<PerMonthRecord> activeCausePerMonth;

    private Long causePending;
    private Long causeCompleted;

    public CaAdminCauseData() {
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

    @Override
    public String toString() {
        return "CaAdminCauseData{" +
                "noOfCause=" + noOfCause +
                ", activeCause=" + activeCause +
                ", noOfCauseThisWeek=" + noOfCauseThisWeek +
                ", causePerMonth=" + causePerMonth +
                ", inactiveCausePerMonth=" + inactiveCausePerMonth +
                ", activeCausePerMonth=" + activeCausePerMonth +
                ", causePending=" + causePending +
                ", causeCompleted=" + causeCompleted +
                '}';
    }
}
