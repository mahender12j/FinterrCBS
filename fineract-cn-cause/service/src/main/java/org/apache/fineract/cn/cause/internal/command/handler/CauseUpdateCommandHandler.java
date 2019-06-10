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
package org.apache.fineract.cn.cause.internal.command.handler;

import org.apache.fineract.cn.cause.api.v1.CauseEventConstants;
import org.apache.fineract.cn.cause.api.v1.domain.CauseUpdate;
import org.apache.fineract.cn.cause.api.v1.domain.CauseUpdateInfo;
import org.apache.fineract.cn.cause.api.v1.domain.CauseUpdatePage;
import org.apache.fineract.cn.cause.api.v1.events.CauseUpdateEvent;
import org.apache.fineract.cn.cause.api.v1.events.CauseUpdateInfoEvent;
import org.apache.fineract.cn.cause.api.v1.events.CauseUpdatePatchEvent;
import org.apache.fineract.cn.cause.internal.command.*;
import org.apache.fineract.cn.cause.internal.mapper.CauseUpdateMapper;
import org.apache.fineract.cn.cause.internal.mapper.DocumentMapper;
import org.apache.fineract.cn.cause.internal.repository.*;
import org.apache.fineract.cn.command.annotation.Aggregate;
import org.apache.fineract.cn.command.annotation.CommandHandler;
import org.apache.fineract.cn.command.annotation.EventEmitter;
import org.apache.fineract.cn.lang.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Md Robiul hassan
 */

@Aggregate
public class CauseUpdateCommandHandler {
    private final CauseRepository causeRepository;
    private final CauseUpdateRepository causeUpdateRepository;
    private final CauseUpdatePageRepository causeUpdatePageRepository;
    private final CauseUpdateInfoRepository causeUpdateInfoRepository;
    private final DocumentStorageRepository documentStorageRepository;

    @Autowired
    public CauseUpdateCommandHandler(final CauseRepository causeRepository,
                                     final CauseUpdateRepository causeUpdateRepository,
                                     final CauseUpdatePageRepository causeUpdatePageRepository,
                                     final CauseUpdateInfoRepository causeUpdateInfoRepository,
                                     final DocumentStorageRepository documentStorageRepository) {
        super();
        this.causeRepository = causeRepository;
        this.causeUpdateRepository = causeUpdateRepository;
        this.causeUpdatePageRepository = causeUpdatePageRepository;
        this.causeUpdateInfoRepository = causeUpdateInfoRepository;
        this.documentStorageRepository = documentStorageRepository;
    }

    @Transactional
    @CommandHandler
    @EventEmitter(selectorName = CauseEventConstants.SELECTOR_NAME, selectorValue = CauseEventConstants.POST_CAUSE_UPDATE)
    public CauseUpdateEvent process(final CreateCauseUpdateCommand createCauseUpdateCommand) {

        CauseUpdate causeUpdate = createCauseUpdateCommand.getCauseUpdate();
        List<CauseUpdatePage> causeUpdatePage = createCauseUpdateCommand.getCauseUpdate().getCauseUpdatePages();


        CauseEntity causeEntity = this.findCauseEntityOrThrow(createCauseUpdateCommand.getIdentifier());
        CauseUpdateEntity causeUpdateEntity = CauseUpdateMapper.map(causeUpdate, causeEntity);
        this.causeUpdateRepository.save(causeUpdateEntity);

        List<CauseUpdatePageEntity> pageEntities = causeUpdatePage.stream().map(page -> CauseUpdateMapper.map(page, causeUpdateEntity)).collect(Collectors.toList());
        this.causeUpdatePageRepository.save(pageEntities);

        return new CauseUpdateEvent(createCauseUpdateCommand.getIdentifier(), createCauseUpdateCommand.getCauseUpdate());
    }


    @Transactional
    @CommandHandler
    @EventEmitter(selectorName = CauseEventConstants.SELECTOR_NAME, selectorValue = CauseEventConstants.POST_CAUSE_UPDATE)
    public CauseUpdateInfoEvent process(final CreateCauseUpdateInfoCommand createCauseUpdateInfoCommand) {

        CauseUpdateInfo causeUpdateInfo = createCauseUpdateInfoCommand.getCauseUpdateInfo();
        CauseEntity causeEntity = this.findCauseEntityOrThrow(createCauseUpdateInfoCommand.getIdentifier());
        CauseUpdateInfoEntity updateInfoEntity = CauseUpdateMapper.map(causeUpdateInfo, causeEntity);
        causeUpdateInfoRepository.save(updateInfoEntity);
        return new CauseUpdateInfoEvent(createCauseUpdateInfoCommand.getIdentifier(), causeUpdateInfo);
    }


    @Transactional
    @CommandHandler
    @EventEmitter(selectorName = CauseEventConstants.SELECTOR_NAME, selectorValue = CauseEventConstants.PUT_CAUSE_UPDATE)
    public CauseUpdatePatchEvent process(final UpdateCauseUpdateCommand updateCauseUpdateCommand) {
        CauseUpdate causeUpdate = updateCauseUpdateCommand.getCauseUpdate();
        CauseEntity entity = this.findCauseEntityOrThrow(updateCauseUpdateCommand.getIdentifier());

        CauseUpdateEntity updateEntity = this.causeUpdateRepository.findByCauseEntity(entity)
                .stream().filter(e -> e.getId().equals(causeUpdate.getId())).findFirst().orElseThrow(() -> ServiceException.notFound("Cause Update not found"));

        updateEntity.setTitle(causeUpdate.getTitle());
        updateEntity.setDescription(causeUpdate.getDescription());
        updateEntity.setAmountSpend(causeUpdate.getAmountSpend());
        updateEntity.setUpdatedAt(LocalDateTime.now());
        causeUpdateRepository.save(updateEntity);


        List<CauseUpdatePageEntity> pageEntity = causeUpdatePageRepository.findByUpdateEntity(updateEntity);
        causeUpdatePageRepository.delete(pageEntity);

        List<CauseUpdatePageEntity> pageEntities = causeUpdate.getCauseUpdatePages()
                .stream().map(causeUpdatePage -> CauseUpdateMapper.map(causeUpdatePage, updateEntity)).collect(Collectors.toList());
        causeUpdatePageRepository.save(pageEntities);
        return new CauseUpdatePatchEvent(updateCauseUpdateCommand.getIdentifier(), causeUpdate);
    }


    @Transactional
    @CommandHandler
    @EventEmitter(selectorName = CauseEventConstants.SELECTOR_NAME, selectorValue = CauseEventConstants.POST_CAUSE_FILE)
    public CauseUpdateFile process(final CreateCauseUpdateFileCommand fileCommand) throws IOException {

        DocumentStorageEntity entity = this.documentStorageRepository.save(DocumentMapper.map(fileCommand));
        return new CauseUpdateFile(entity.getId(), entity.getUuid(), entity.getCreatedBy(),
                entity.getDocumentName(), entity.getContentType(), entity.getSize(), entity.getDocType());
    }


    private CauseEntity findCauseEntityOrThrow(String identifier) {
        return this.causeRepository.findByIdentifier(identifier).orElseThrow(() -> ServiceException.notFound("Cause ''{0}'' not found", identifier));
    }
}

