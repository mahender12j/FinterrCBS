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

import org.apache.fineract.cn.api.util.UserContextHolder;
import org.apache.fineract.cn.cause.api.v1.CauseEventConstants;
import org.apache.fineract.cn.cause.api.v1.events.CategoryEvent;
import org.apache.fineract.cn.cause.internal.command.*;
import org.apache.fineract.cn.cause.internal.mapper.CategoryMapper;
import org.apache.fineract.cn.cause.internal.repository.*;
import org.apache.fineract.cn.command.annotation.Aggregate;
import org.apache.fineract.cn.command.annotation.CommandHandler;
import org.apache.fineract.cn.command.annotation.EventEmitter;
import org.apache.fineract.cn.lang.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.Clock;
import java.time.LocalDateTime;

/**
 * @author Padma Raju Sattineni
 */
@Aggregate
public class CategoryCommandHandler {
    private final CategoryRepository categoryRepository;
    private final CauseRepository causeRepository;

    @Autowired
    public CategoryCommandHandler(
            final CategoryRepository categoryRepository,
            final CauseRepository causeRepository) {

        this.categoryRepository = categoryRepository;
        this.causeRepository = causeRepository;
    }

    @Transactional
    @CommandHandler
    @EventEmitter(selectorName = CauseEventConstants.SELECTOR_NAME, selectorValue = CauseEventConstants.POST_CATEGORY)
    public CategoryEvent process(final CreateCategoryCommand command) {
        causeRepository.findByIdentifier(command.getCauseIdentifier())
                .map(causeEntity -> CategoryMapper.map(command.getCauseCategory(), causeEntity))
                .ifPresent(categoryRepository::save);

        return new CategoryEvent(command.getCauseIdentifier(), command.getCauseCategory().getIdentifier());
    }

/*    @Transactional
    @CommandHandler
    @EventEmitter(selectorName = CauseEventConstants.SELECTOR_NAME, selectorValue = CauseEventConstants.PUT_CATEGORY)
    public CategoryEvent process(final ChangeCategoryCommand command) throws IOException {
        final CategoryEntity existingCategory = categoryRepository.findByCauseIdAndCategoryIdentifier(
                command.getCauseIdentifier(), command.getCauseCategory().getRating())
                .orElseThrow(() ->
                        ServiceException.notFound("Category ''{0}'' for cause ''{1}'' not found",
                                command.getCauseCategory().getRating(), command.getCauseIdentifier()));

        causeRepository.findByIdentifier(command.getCauseIdentifier())
                .map(causeEntity -> CategoryMapper.map(command.getCauseCategory(), causeEntity))
                .ifPresent(categoryEntity -> {
                    categoryEntity.setId(existingCategory.getId());
                    categoryRepository.save(categoryEntity);
                });

        return new CategoryEvent(command.getCauseIdentifier(), command.getCauseCategory().getRating());
    }

    @Transactional
    @CommandHandler
    @EventEmitter(selectorName = CauseEventConstants.SELECTOR_NAME, selectorValue = CauseEventConstants.DELETE_CATEGORY)
    public CategoryEvent process(final DeleteCategoryCommand command) throws IOException {
        final CategoryEntity existingCategory = categoryRepository.findByCauseIdAndCategoryIdentifier(
                command.getCauseIdentifier(), command.getCategoryIdentifier())
                .orElseThrow(() ->
                        ServiceException.notFound("Category ''{0}'' for cause ''{1}'' not found",
                                command.getCategoryIdentifier(), command.getCauseIdentifier()));
        categoryRepository.delete(existingCategory);

        return new CategoryEvent(command.getCauseIdentifier(), command.getCategoryIdentifier());
    }

    @Transactional
    @CommandHandler
    @EventEmitter(selectorName = CauseEventConstants.SELECTOR_NAME, selectorValue = CauseEventConstants.POST_CATEGORY_COMPLETE)
    public CategoryEvent process(final CompleteCategoryCommand command) throws IOException {
        final CategoryEntity categoryEntity = categoryRepository.findByCauseIdAndCategoryIdentifier(
                command.getCauseIdentifier(),
                command.getCategoryIdentifier())
                .orElseThrow(() -> ServiceException.badRequest("Category not found"));

        categoryEntity.setCreatedOn(LocalDateTime.now(Clock.systemUTC()));
        categoryEntity.setCreatedBy(UserContextHolder.checkedGetUser());
        categoryEntity.setCompleted(true);
        categoryRepository.save(categoryEntity);
        return new CategoryEvent(command.getCauseIdentifier(), command.getCategoryIdentifier());
    }*/    
}

