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
import org.apache.fineract.cn.cause.api.v1.domain.*;
import org.apache.fineract.cn.cause.internal.repository.*;
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
 * @author Md ROBIUL HASSAN
 */

public final class CauseUpdateMapper {

    private CauseUpdateMapper() {
        super();
    }


    public static CauseUpdateEntity map(CauseUpdate causeUpdate, CauseEntity causeEntity) {
        CauseUpdateEntity causeUpdateEntity = new CauseUpdateEntity();
        causeUpdateEntity.setTitle(causeUpdate.getTitle());
        causeUpdateEntity.setDescription(causeUpdate.getDescription());
        causeUpdateEntity.setAmountSpend(causeUpdate.getAmountSpend());
        causeUpdateEntity.setCauseEntity(causeEntity);
        causeUpdateEntity.setCreatedBy(UserContextHolder.checkedGetUser());
        causeUpdateEntity.setUpdatedAt(LocalDateTime.now());
        causeUpdateEntity.setCreatedOn(LocalDateTime.now());
        return causeUpdateEntity;
    }


    public static CauseUpdate map(CauseUpdateEntity updateEntity) {
        CauseUpdate causeUpdate = new CauseUpdate();
        causeUpdate.setId(updateEntity.getId());
        causeUpdate.setTitle(updateEntity.getTitle());
        causeUpdate.setDescription(updateEntity.getDescription());
        causeUpdate.setAmountSpend(updateEntity.getAmountSpend());
        causeUpdate.setUpdatedAt(updateEntity.getUpdatedAt().toString());
        causeUpdate.setCreateOn(updateEntity.getCreatedOn().toString());
        return causeUpdate;
    }


    public static CauseUpdatePageEntity map(CauseUpdatePage causeUpdatePage, CauseUpdateEntity updateEntity) {
        CauseUpdatePageEntity causeUpdatePageEntity = new CauseUpdatePageEntity();
        causeUpdatePageEntity.setDocName(causeUpdatePage.getDocName());
        causeUpdatePageEntity.setDocRef(causeUpdatePage.getDocRef());
        causeUpdatePageEntity.setType(causeUpdatePage.getType());
        causeUpdatePageEntity.setCreatedOn(LocalDateTime.now());
        causeUpdatePageEntity.setUpdatedAt(LocalDateTime.now());
        causeUpdatePageEntity.setUpdateEntity(updateEntity);
        return causeUpdatePageEntity;
    }

    public static CauseUpdatePage map(CauseUpdatePageEntity updatePageEntity) {
        CauseUpdatePage causeUpdatePage = new CauseUpdatePage();
        causeUpdatePage.setId(updatePageEntity.getId());
        causeUpdatePage.setDocName(updatePageEntity.getDocName());
        causeUpdatePage.setDocRef(updatePageEntity.getDocRef());
        causeUpdatePage.setType(updatePageEntity.getType());
        causeUpdatePage.setCreateOn(updatePageEntity.getCreatedOn().toString());
        causeUpdatePage.setUpdatedAt(updatePageEntity.getUpdatedAt().toString());
        return causeUpdatePage;
    }


    public static CauseUpdateInfo map(CauseUpdateInfoEntity causeUpdateInfoEntity) {
        CauseUpdateInfo causeUpdateInfo = new CauseUpdateInfo();
        causeUpdateInfo.setId(causeUpdateInfoEntity.getId());
        causeUpdateInfo.setDescription(causeUpdateInfoEntity.getDescription());
        causeUpdateInfo.setDocRef(causeUpdateInfoEntity.getDocRef());
        causeUpdateInfo.setCreateOn(causeUpdateInfoEntity.getCreatedOn().toString());
        causeUpdateInfo.setUpdatedAt(causeUpdateInfoEntity.getUpdatedAt().toString());
        return causeUpdateInfo;
    }


    public static CauseUpdateInfoEntity map(CauseUpdateInfo causeUpdateInfo, CauseEntity causeEntity) {
        CauseUpdateInfoEntity updateInfoEntity = new CauseUpdateInfoEntity();
        updateInfoEntity.setCauseEntity(causeEntity);
        updateInfoEntity.setDocRef(causeUpdateInfo.getDocRef());
        updateInfoEntity.setDescription(causeUpdateInfo.getDescription());
        updateInfoEntity.setCreatedOn(LocalDateTime.now());
        updateInfoEntity.setUpdatedAt(LocalDateTime.now());
        return updateInfoEntity;
    }
}

