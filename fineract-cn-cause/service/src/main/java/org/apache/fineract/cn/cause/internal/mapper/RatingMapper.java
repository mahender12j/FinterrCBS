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
import org.apache.fineract.cn.cause.api.v1.domain.CauseRating;
import org.apache.fineract.cn.cause.internal.repository.RatingEntity;
import org.apache.fineract.cn.cause.internal.repository.CauseEntity;
import org.apache.fineract.cn.lang.DateConverter;

import java.io.IOException;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * @author Padma Raju Sattineni
 */
public class RatingMapper {
    private RatingMapper() {
        super();
    }

    public static CauseRating map(final RatingEntity ratingEntity) {
        final CauseRating ret = new CauseRating();
        ret.setActive(ratingEntity.getActive());
        ret.setCreatedBy(ratingEntity.getCreatedBy());
        ret.setCreatedOn(DateConverter.toIsoString(ratingEntity.getCreatedOn()));
        ret.setIdentifier(ratingEntity.getIdentifier());
        ret.setDescription(ratingEntity.getDescription());
        return ret;
    }

    public static RatingEntity map(final CauseRating rating) {
        final RatingEntity ret = new RatingEntity();
        ret.setActive(true);
        ret.setCreatedBy(UserContextHolder.checkedGetUser());
        ret.setCreatedOn(LocalDateTime.now(Clock.systemUTC()));
        ret.setIdentifier(rating.getIdentifier());
        ret.setDescription(rating.getDescription());
        return ret;
    }

    public static RatingEntity map(final CauseRating rating, final CauseEntity causeEntity) {
        final RatingEntity ret = new RatingEntity();
        ret.setCause(causeEntity);
        ret.setActive(true);
        ret.setCreatedBy(UserContextHolder.checkedGetUser());
        ret.setCreatedOn(LocalDateTime.now(Clock.systemUTC()));
        ret.setIdentifier(rating.getIdentifier());
        ret.setDescription(rating.getDescription());
        return ret;
    }

    public static List<CauseRating> map(Stream<RatingEntity> byCause) {
        List<CauseRating> causeRatings = new ArrayList<>();
        byCause.forEach(d -> {
            causeRatings.add(map(d));
        });
        return causeRatings;
    }
}

