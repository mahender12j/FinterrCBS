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
package org.apache.fineract.cn.cause.internal.service;

import org.apache.fineract.cn.cause.api.v1.domain.CauseUpdate;
import org.apache.fineract.cn.cause.api.v1.domain.CauseUpdateInfo;
import org.apache.fineract.cn.cause.api.v1.domain.CauseUpdatePage;
import org.apache.fineract.cn.cause.internal.mapper.CauseUpdateMapper;
import org.apache.fineract.cn.cause.internal.repository.*;
import org.apache.fineract.cn.lang.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Service
public class CauseUpdateService {

    private final CauseRepository causeRepository;
    private final CauseUpdateRepository causeUpdateRepository;
    private final CauseUpdatePageRepository causeUpdatePageRepository;
    private final CauseUpdateInfoRepository causeUpdateInfoRepository;

    @Autowired
    public CauseUpdateService(final CauseRepository causeRepository,
                              final CauseUpdateRepository causeUpdateRepository,
                              final CauseUpdatePageRepository causeUpdatePageRepository,
                              final CauseUpdateInfoRepository causeUpdateInfoRepository) {
        super();
        this.causeRepository = causeRepository;
        this.causeUpdateRepository = causeUpdateRepository;
        this.causeUpdatePageRepository = causeUpdatePageRepository;
        this.causeUpdateInfoRepository = causeUpdateInfoRepository;
    }

    public CauseUpdateInfo causeUpdateList(final String identefier) {
        CauseEntity causeEntity = causeRepository.findByIdentifier(identefier).orElseThrow(() -> ServiceException.notFound("Cause Not found"));
        CauseUpdateInfoEntity updateInfoEntity = causeUpdateInfoRepository.findByCauseEntity(causeEntity).orElseThrow(() -> ServiceException.notFound("Info Not found"));


        CauseUpdateInfo updateInfo = CauseUpdateMapper.map(updateInfoEntity);

        List<CauseUpdate> causeUpdates = causeUpdateRepository.findByCauseEntity(causeEntity)
                .stream()
                .map(updateEntity -> {
                    CauseUpdate causeUpdate = CauseUpdateMapper.map(updateEntity);
                    List<CauseUpdatePageEntity> pageEntities = causeUpdatePageRepository.findByUpdateEntity(updateEntity);
                    List<CauseUpdatePage> causeUpdatePage = pageEntities
                            .stream()
                            .map(CauseUpdateMapper::map)
                            .collect(Collectors.toList());

                    Map<String, List<CauseUpdatePage>> causeUpdatePageMap = pageEntities
                            .stream()
                            .map(CauseUpdateMapper::map)
                            .collect(Collectors.groupingBy(CauseUpdatePage::getType, Collectors.toList()));

                    causeUpdate.setCauseUpdatePages(causeUpdatePage);
                    causeUpdate.setCauseUpdatePagesMap(causeUpdatePageMap);
                    return causeUpdate;

                })
                .collect(Collectors.toList());

        updateInfo.setCauseUpdateList(causeUpdates);

        return updateInfo;

    }


    public Optional<CauseUpdateInfoEntity> causeInfoExists(final CauseEntity causeEntity) {
        return this.causeUpdateInfoRepository.findByCauseEntity(causeEntity);
    }


}

