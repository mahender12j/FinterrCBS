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

import org.apache.fineract.cn.accounting.api.v1.domain.JournalEntry;

import java.util.List;

/**
 * @author Padma Raju Sattineni
 */
public class CauseStatistics {

    private Double totalRaised;
    private int totalSupporter;
    private List<JournalEntry> journalEntry;

    public CauseStatistics() {
        super();
    }

    public Double getTotalRaised() {
        return totalRaised;
    }

    public void setTotalRaised(Double totalRaised) {
        this.totalRaised = totalRaised;
    }

    public int getTotalSupporter() {
        return totalSupporter;
    }

    public void setTotalSupporter(int totalSupporter) {
        this.totalSupporter = totalSupporter;
    }

    public List<JournalEntry> getJournalEntry() {
        return journalEntry;
    }

    public void setJournalEntry(List<JournalEntry> journalEntry) {
        this.journalEntry = journalEntry;
    }


    @Override
    public String toString() {
        return "CauseStatistics{" +
                "totalRaised=" + totalRaised +
                ", totalSupporter=" + totalSupporter +
                ", journalEntry=" + journalEntry +
                '}';
    }
}
