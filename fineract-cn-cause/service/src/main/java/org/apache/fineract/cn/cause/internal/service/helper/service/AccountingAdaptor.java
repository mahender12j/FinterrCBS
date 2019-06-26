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
package org.apache.fineract.cn.cause.internal.service.helper.service;

import org.apache.fineract.cn.accounting.api.v1.client.JournalManager;
import org.apache.fineract.cn.cause.ServiceConstants;
import org.apache.fineract.cn.cause.api.v1.domain.CauseJournalEntry;
import org.apache.fineract.cn.cause.internal.mapper.CauseJournalEntryMapper;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AccountingAdaptor {

    private final Logger logger;
    private final JournalManager journalManager;

    @Autowired
    public AccountingAdaptor(@Qualifier(ServiceConstants.LOGGER_NAME) final Logger logger,
                             final JournalManager journalManager) {
        super();
        this.logger = logger;
        this.journalManager = journalManager;
    }

    public List<CauseJournalEntry> fetchJournalEntriesJournalEntries(final String creditorsAccountNumber) {
        return this.journalManager.fetchJournalEntriesForStatistics(creditorsAccountNumber)
                .stream()
                .map(CauseJournalEntryMapper::map).collect(Collectors.toList());
    }
}

