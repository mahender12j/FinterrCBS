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

import org.apache.fineract.cn.api.util.UserContextHolder;
import org.apache.fineract.cn.cause.api.v1.domain.Cause;
import org.apache.fineract.cn.cause.api.v1.domain.PerMonthRecord;
import org.apache.fineract.cn.cause.internal.repository.CauseEntity;
import org.apache.fineract.cn.cause.internal.repository.CauseStateEntity;
import org.apache.fineract.cn.lang.DateConverter;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Padma Raju Sattineni
 */

public final class CauseMapper {

    private CauseMapper() {
        super();
    }

    public static CauseEntity map(final Cause cause) {
        final CauseEntity causeEntity = new CauseEntity();
        causeEntity.setIdentifier(cause.getIdentifier());
        causeEntity.setTitle(cause.getTitle());
        causeEntity.setDescription(cause.getDescription());
        if (cause.getStartDate() != null) {
            causeEntity.setStartDate(LocalDateTime.parse(cause.getStartDate()));
        }
        if (cause.getEndDate() != null) {
            causeEntity.setEndDate(LocalDateTime.parse(cause.getEndDate()));
        }
        if (cause.getCompletedDate() != null) {
            causeEntity.setCompletedDate(LocalDateTime.parse(cause.getCompletedDate()));
        }

        causeEntity.setCurrentState(cause.getCurrentState());
        causeEntity.setSoftTarget(cause.getSoftTarget());
        causeEntity.setHardTarget(cause.getHardTarget());
        causeEntity.setIsTaxExamption(cause.getIsTaxExamption());
        causeEntity.setActualRaisedFiat(cause.getActualRaisedFiat());
        causeEntity.setActualRaisedFin(cause.getActualRaisedFin());
        causeEntity.setMinAmount(cause.getMinAmount());
        causeEntity.setMaxAmount(cause.getMaxAmount());
        causeEntity.setAcceptedDenominationAmounts(cause.getAcceptedDenominationAmounts());
        causeEntity.setManagementFee(cause.getSuccessFees());
        causeEntity.setFinCollLimit(cause.getFinCollLimit());
        causeEntity.setFinRate(cause.getFinRate());
        causeEntity.setApprovedBy(cause.getApprovedBy());
        causeEntity.setFeeRevisionRequired(cause.isFeeRevisionRequired());

        if (cause.getApprovedOn() != null) {
            causeEntity.setApprovedOn(LocalDateTime.parse(cause.getApprovedOn()));
        }
        causeEntity.setCreatedBy(UserContextHolder.checkedGetUser());
        causeEntity.setCreatedOn(LocalDateTime.now(Clock.systemUTC()));

        causeEntity.setTaxExemptionPercentage(cause.getTaxExemptionPercentage());
        causeEntity.setWebsiteUrl(cause.getWebsiteUrl());
        causeEntity.setSmediaLinks(cause.getSmediaLinks());
        causeEntity.setVideoUrls(cause.getVideoUrls());
        causeEntity.setCauseTxHash(cause.getCauseTxHash());
        causeEntity.setAccountNumber(cause.getAccountNumber());
        causeEntity.setEthAddress(cause.getEthAddress());
        causeEntity.setExtended(cause.getExtended());
        causeEntity.setResubmited(cause.getResubmited());
        causeEntity.setCauseImplementationDuration(cause.getFrequencyCauseImplementationUpdates());
        causeEntity.setFrequencyCauseImplementationUpdates(cause.getFrequencyCauseImplementationUpdates());

        return causeEntity;
    }


    public static Cause map(final CauseEntity causeEntity) {
        final Cause cause = new Cause();

        cause.setIdentifier(causeEntity.getIdentifier());
        cause.setTitle(causeEntity.getTitle());
        cause.setDescription(causeEntity.getDescription());
        if (causeEntity.getStartDate() != null) {
            cause.setStartDate(DateConverter.toIsoString(causeEntity.getStartDate()));
        }
        if (causeEntity.getEndDate() != null) {
            cause.setEndDate(DateConverter.toIsoString(causeEntity.getEndDate()));
        }
        if (causeEntity.getCompletedDate() != null) {
            cause.setCompletedDate(DateConverter.toIsoString(causeEntity.getCompletedDate()));
        }
        cause.setCurrentState(causeEntity.getCurrentState());
        cause.setSoftTarget(causeEntity.getSoftTarget());
        cause.setHardTarget(causeEntity.getHardTarget());
        cause.setIsTaxExamption(causeEntity.getIsTaxExamption());
        cause.setActualRaisedFiat(causeEntity.getActualRaisedFiat());
        cause.setActualRaisedFin(causeEntity.getActualRaisedFin());
        cause.setMinAmount(causeEntity.getMinAmount());
        cause.setMaxAmount(causeEntity.getMaxAmount());
        cause.setAcceptedDenominationAmounts(causeEntity.getAcceptedDenominationAmounts());
        cause.setSuccessFees(causeEntity.getManagementFee());
        cause.setFinCollLimit(causeEntity.getFinCollLimit());
        cause.setFinRate(causeEntity.getFinRate());
        cause.setApprovedBy(causeEntity.getApprovedBy());
        cause.setRejectedReason(causeEntity.getRejectedReason());
        cause.setFeeRevisionRequired(causeEntity.isFeeRevisionRequired());

        if (causeEntity.getApprovedOn() != null) {
            cause.setApprovedOn(DateConverter.toIsoString(causeEntity.getApprovedOn()));
        }

        cause.setCreatedBy(causeEntity.getCreatedBy());
        cause.setCreatedOn(DateConverter.toIsoString(causeEntity.getCreatedOn()));

        if (causeEntity.getLastModifiedBy() != null) {
            cause.setLastModifiedBy(causeEntity.getLastModifiedBy());
            cause.setLastModifiedOn(DateConverter.toIsoString(causeEntity.getLastModifiedOn()));
        }

        cause.setTaxExemptionPercentage(causeEntity.getTaxExemptionPercentage());
        cause.setWebsiteUrl(causeEntity.getWebsiteUrl());
        cause.setSmediaLinks(causeEntity.getSmediaLinks());
        cause.setVideoUrls(causeEntity.getVideoUrls());
        cause.setCauseTxHash(causeEntity.getCauseTxHash());
        cause.setAccountNumber(causeEntity.getAccountNumber());
        cause.setEthAddress(causeEntity.getEthAddress());
        cause.setExtended(causeEntity.getExtended());
        cause.setResubmited(causeEntity.getResubmited());
        cause.setFrequencyCauseImplementationUpdates(cause.getFrequencyCauseImplementationUpdates());
        cause.setCauseImplementationDuration(cause.getCauseImplementationDuration());
        return cause;
    }


    public static CauseStateEntity map(CauseEntity causeEntity, LocalDateTime extended_date, String type) {
        CauseStateEntity stateEntity = new CauseStateEntity();
        stateEntity.setCause(causeEntity);
        stateEntity.setCreatedBy(UserContextHolder.checkedGetUser());
        stateEntity.setCreatedOn(LocalDateTime.now(Clock.systemUTC()));
        stateEntity.setModifiedAt(LocalDateTime.now(Clock.systemUTC()));
        stateEntity.setNewDate(extended_date);
        stateEntity.setType(type);
        stateEntity.setStatus(Cause.State.PENDING.name());
        stateEntity.setComment("NO COMMENT");
        return stateEntity;
    }


    public static CauseStateEntity mapComment(CauseEntity causeEntity, String comment, String type) {
        CauseStateEntity stateEntity = new CauseStateEntity();
        stateEntity.setCause(causeEntity);
        stateEntity.setCreatedBy(UserContextHolder.checkedGetUser());
        stateEntity.setCreatedOn(LocalDateTime.now(Clock.systemUTC()));
        stateEntity.setModifiedAt(LocalDateTime.now(Clock.systemUTC()));
        stateEntity.setType(type);
        stateEntity.setStatus(Cause.State.PENDING.name());
        stateEntity.setComment(comment);
        return stateEntity;
    }


    public static List<PerMonthRecord> map(List<CauseEntity> causeEntities, Cause.State state) {
        final LocalDateTime oneYearBack = LocalDateTime.now().minusYears(1);
        Map<String, Long> byMonth = causeEntities.stream()
                .filter(causeEntity -> causeEntity.getCreatedOn().isAfter(oneYearBack) && causeEntity.getCurrentState().equals(state.name()))
                .collect(Collectors.groupingBy(d -> d.getCreatedOn().getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH), Collectors.counting()));

        return getPerMonthRecords(byMonth);
    }

    private static List<PerMonthRecord> getPerMonthRecords(Map<String, Long> byMonth) {
        return byMonth.entrySet().stream().sorted(Comparator.comparing(Map.Entry::getKey))
                .map(e -> new PerMonthRecord(e.getKey(), Month.valueOf(e.getKey().toUpperCase()).getValue(), e.getValue())).collect(Collectors.toList());
    }

    public static List<PerMonthRecord> mapAll(List<CauseEntity> causeEntities) {
        final LocalDateTime oneYearBack = LocalDateTime.now().minusYears(1);
        Map<String, Long> byMonth = causeEntities.stream()
                .filter(causeEntity -> causeEntity.getCreatedOn().isAfter(oneYearBack))
                .collect(Collectors.groupingBy(d -> d.getCreatedOn().getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH), Collectors.counting()));

        return getPerMonthRecords(byMonth);
    }
}

