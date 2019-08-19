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
package org.apache.fineract.cn.cause.internal.mapper;

import org.apache.fineract.cn.accounting.api.v1.domain.JournalEntry;
import org.apache.fineract.cn.cause.api.v1.domain.CauseCreditor;
import org.apache.fineract.cn.cause.api.v1.domain.CauseDebtor;
import org.apache.fineract.cn.cause.api.v1.domain.CauseJournalEntry;

import java.util.stream.Collectors;

public class CauseJournalEntryMapper {

    private CauseJournalEntryMapper() {
        super();
    }

    public static CauseJournalEntry map(final JournalEntry entry) {
        final CauseJournalEntry journalEntry = new CauseJournalEntry();
        journalEntry.setTransactionIdentifier(entry.getTransactionIdentifier());
        journalEntry.setTransactionDate(entry.getTransactionDate());
        journalEntry.setTransactionType(entry.getTransactionType());
        journalEntry.setClerk(entry.getClerk());
        journalEntry.setNote(entry.getNote());
        journalEntry.setDebtors(
                entry.getDebtors()
                        .stream()
                        .map(debtorType -> {
                            final CauseDebtor debtor = new CauseDebtor();
                            debtor.setAccountNumber(debtorType.getAccountNumber());
                            debtor.setAmount(debtorType.getAmount());
                            return debtor;
                        })
                        .collect(Collectors.toSet())
        );
        journalEntry.setCreditors(
                entry.getCreditors()
                        .stream()
                        .map(creditorType -> {
                            final CauseCreditor creditor = new CauseCreditor();
                            creditor.setAccountNumber(creditorType.getAccountNumber());
                            creditor.setAmount(creditorType.getAmount());
                            return creditor;
                        })
                        .collect(Collectors.toSet())
        );
        journalEntry.setAnonymous(entry.isAnonymous());
        journalEntry.setMessage(entry.getMessage());
        return journalEntry;
    }
}
