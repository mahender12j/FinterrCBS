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

import org.apache.fineract.cn.lang.validation.constraints.ValidIdentifier;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Set;

public final class CauseJournalEntry {
    @ValidIdentifier(
            maxLength = 2200
    )
    private String transactionIdentifier;
    @NotNull
    private String transactionDate;
    @ValidIdentifier
    private String transactionType;
    @NotEmpty
    private String clerk;
    private String note;
    @NotNull
    @Valid
    private Set<CauseDebtor> debtors;
    @NotNull
    @Valid
    private Set<CauseCreditor> creditors;
    @Length(
            max = 2048
    )
    private String message;
    @NotNull
    @Valid
    private boolean anonymous;

    public CauseJournalEntry() {
    }

    public String getTransactionIdentifier() {
        return this.transactionIdentifier;
    }

    public void setTransactionIdentifier(String transactionIdentifier) {
        this.transactionIdentifier = transactionIdentifier;
    }

    public String getTransactionDate() {
        return this.transactionDate;
    }

    public void setTransactionDate(String transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getTransactionType() {
        return this.transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public String getClerk() {
        return this.clerk;
    }

    public void setClerk(String clerk) {
        this.clerk = clerk;
    }

    public String getNote() {
        return this.note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Set<CauseDebtor> getDebtors() {
        return this.debtors;
    }

    public void setDebtors(Set<CauseDebtor> debtors) {
        this.debtors = debtors;
    }

    public Set<CauseCreditor> getCreditors() {
        return this.creditors;
    }

    public void setCreditors(Set<CauseCreditor> creditors) {
        this.creditors = creditors;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isAnonymous() {
        return this.anonymous;
    }

    public void setAnonymous(boolean anonymous) {
        this.anonymous = anonymous;
    }


    public static enum State {
        PENDING,
        PROCESSED;

        private State() {
        }
    }
}
