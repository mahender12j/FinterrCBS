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
package org.apache.fineract.cn.customer.internal.mapper;

import org.apache.fineract.cn.customer.api.v1.domain.Customer;
import org.apache.fineract.cn.customer.api.v1.domain.PerMonthRecord;
import org.apache.fineract.cn.customer.internal.repository.CustomerEntity;

import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public final class CadminMapper {

    private CadminMapper() {
        super();
    }

    public static List<PerMonthRecord> map(List<CustomerEntity> customerEntities, Customer.Type type) {
        final LocalDateTime oneYearBack = LocalDateTime.now().minusYears(1);
        Map<String, Long> byMonth = customerEntities.stream()
                .filter(customerEntity -> customerEntity.getCreatedOn().isAfter(oneYearBack) && customerEntity.getType().equals(type.name()))
                .collect(Collectors.groupingBy(d -> d.getCreatedOn().getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH), Collectors.counting()));

        List<PerMonthRecord> perMonthRecords = byMonth.entrySet().stream().sorted(Comparator.comparing(Map.Entry::getKey))
                .map(e -> new PerMonthRecord(e.getKey(), e.getValue())).collect(Collectors.toList());
        return perMonthRecords;
    }


    public static List<PerMonthRecord> mapByStatus(List<CustomerEntity> customerEntities, Customer.Type type, boolean customerStatus) {
        final LocalDateTime oneYearBack = LocalDateTime.now().minusYears(1);
        Map<String, Long> byMonth = customerEntities.stream()
                .filter(customerEntity -> customerEntity.getCreatedOn().isAfter(oneYearBack) && customerEntity.getType().equals(type.name()) && customerEntity.getIsDeposited() == customerStatus)
                .collect(Collectors.groupingBy(d -> d.getCreatedOn().getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH), Collectors.counting()));

        List<PerMonthRecord> perMonthRecords = byMonth.entrySet().stream().sorted(Comparator.comparing(Map.Entry::getKey))
                .map(e -> new PerMonthRecord(e.getKey(), e.getValue())).collect(Collectors.toList());
        return perMonthRecords;
    }
}
