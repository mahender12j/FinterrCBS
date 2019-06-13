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
import org.apache.fineract.cn.cause.api.v1.domain.CauseStatistics;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

public final class CauseStatisticsMapper {

    private CauseStatisticsMapper() {
        super();
    }

    public static CauseStatistics map(final List<JournalEntry> journalEntry) {
        final Map<String, List<JournalEntry>> groupedEntry = journalEntry.stream()
                .peek(entry -> {
                    if (entry.isAnonymous()) {
                        entry.setClerk("Anonymous");
                    }
                })
                .collect(groupingBy(JournalEntry::getClerk, toList()));
        CauseStatistics causeStatistics = new CauseStatistics();

        if (groupedEntry.isEmpty()) {
            causeStatistics.setJournalEntry(Collections.emptyList());
            causeStatistics.setTotalSupporter(0);
            causeStatistics.setTotalRaised(0.0);
        } else {
            List<JournalEntry> causejournalEntry = groupedEntry.entrySet().iterator().next().getValue();
            causeStatistics.setJournalEntry(causejournalEntry);
            causeStatistics.setTotalRaised(journalEntry.stream().mapToDouble(d -> Double.parseDouble(d.getCreditors().stream().findFirst().get().getAmount())).sum());
            causeStatistics.setTotalSupporter(groupedEntry.size());
        }
        return causeStatistics;
    }


}
