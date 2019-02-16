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
import org.apache.fineract.cn.cause.api.v1.domain.CauseCategory;
import org.apache.fineract.cn.cause.internal.repository.CategoryEntity;
import org.apache.fineract.cn.cause.internal.repository.CauseEntity;
import org.apache.fineract.cn.lang.DateConverter;


import java.io.IOException;
import java.time.Clock;
import java.time.LocalDateTime;

/**
 * @author Padma Raju Sattineni
 */
public class CategoryMapper {
    private CategoryMapper() {
        super();
    }

    public static CauseCategory map(final CategoryEntity categoryEntity) {
        final CauseCategory ret = new CauseCategory();
        ret.setActive(categoryEntity.getActive());
        ret.setCreatedBy(categoryEntity.getCreatedBy());
        ret.setCreatedOn(DateConverter.toIsoString(categoryEntity.getCreatedOn()));
        ret.setIdentifier(categoryEntity.getIdentifier());
        ret.setDescription(categoryEntity.getDescription());
        return ret;
    }

    public static CategoryEntity map(final CauseCategory category) {
        final CategoryEntity ret = new CategoryEntity();
        ret.setActive(true);
        ret.setCreatedBy(UserContextHolder.checkedGetUser());
        ret.setCreatedOn(LocalDateTime.now(Clock.systemUTC()));
        ret.setIdentifier(category.getIdentifier());
        ret.setDescription(category.getDescription());
        return ret;
    }

    public static CategoryEntity map(final CauseCategory category, final CauseEntity causeEntity) {
        final CategoryEntity ret = new CategoryEntity();
        ret.setCause(causeEntity);
        ret.setActive(true);
        ret.setCreatedBy(UserContextHolder.checkedGetUser());
        ret.setCreatedOn(LocalDateTime.now(Clock.systemUTC()));
        ret.setIdentifier(category.getIdentifier());
        ret.setDescription(category.getDescription());
        return ret;
    }
}

