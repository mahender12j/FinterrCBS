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
package org.apache.fineract.cn.accounting.api.v1.client;

import org.apache.fineract.cn.accounting.api.v1.domain.JournalEntry;
import org.apache.fineract.cn.api.util.CustomFeignClientsConfiguration;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;

@SuppressWarnings("unused")
@FeignClient(value = "accounting-v1", path = "/accounting-v1", configuration = CustomFeignClientsConfiguration.class)
public interface JournalManager {

    @RequestMapping(
            value = "/journal",
            method = RequestMethod.GET,
            produces = {MediaType.ALL_VALUE},
            consumes = {MediaType.APPLICATION_JSON_VALUE}
    )
    List<JournalEntry> fetchJournalEntries(@RequestParam(value = "dateRange", required = false) final String dateRange,
                                           @RequestParam(value = "account", required = false) final String accountNumber,
                                           @RequestParam(value = "amount", required = false) final BigDecimal amount);


    @RequestMapping(
            value = "/journal/statistics/{creditorsAccountNumber}",
            method = RequestMethod.GET,
            produces = {MediaType.ALL_VALUE},
            consumes = {MediaType.APPLICATION_JSON_VALUE}
    )
    List<JournalEntry> fetchJournalEntriesForStatistics(@PathVariable(value = "creditorsAccountNumber") final String creditorsAccountNumber);
}
