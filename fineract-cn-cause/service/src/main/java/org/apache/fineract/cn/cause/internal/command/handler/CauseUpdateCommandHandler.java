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
import org.apache.fineract.cn.cause.api.v1.domain.CauseUpdatePage;
import org.apache.fineract.cn.cause.api.v1.events.CauseUpdateEvent;
import org.apache.fineract.cn.cause.internal.command.CreateCauseUpdateCommand;
import org.apache.fineract.cn.cause.internal.mapper.CauseUpdateMapper;
import org.apache.fineract.cn.cause.internal.repository.*;
import org.apache.fineract.cn.command.annotation.Aggregate;
import org.apache.fineract.cn.command.annotation.CommandHandler;
import org.apache.fineract.cn.command.annotation.EventEmitter;
import org.apache.fineract.cn.lang.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Md Robiul hassan
 */

@Aggregate
public class CauseUpdateCommandHandler {
    private final CauseRepository causeRepository;
    private final CauseUpdateRepository causeUpdateRepository;
    private final CauseUpdatePageRepository causeUpdatePageRepository;

    @Autowired
    public CauseUpdateCommandHandler(final CauseRepository causeRepository,
                                     final CauseUpdateRepository causeUpdateRepository,
                                     final CauseUpdatePageRepository causeUpdatePageRepository) {
        super();
        this.causeRepository = causeRepository;
        this.causeUpdateRepository = causeUpdateRepository;
        this.causeUpdatePageRepository = causeUpdatePageRepository;
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


    private CauseEntity findCauseEntityOrThrow(String identifier) {
        return this.causeRepository.findByIdentifier(identifier).orElseThrow(() -> ServiceException.notFound("Cause ''{0}'' not found", identifier));
    }
}

