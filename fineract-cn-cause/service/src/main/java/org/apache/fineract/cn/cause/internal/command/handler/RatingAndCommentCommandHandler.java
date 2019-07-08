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
import org.apache.fineract.cn.cause.api.v1.domain.CauseComment;
import org.apache.fineract.cn.cause.api.v1.domain.CauseRating;
import org.apache.fineract.cn.cause.internal.command.CreateCommentCommand;
import org.apache.fineract.cn.cause.internal.command.CreateRatingCommand;
import org.apache.fineract.cn.cause.internal.mapper.RatingMapper;
import org.apache.fineract.cn.cause.internal.repository.*;
import org.apache.fineract.cn.command.annotation.Aggregate;
import org.apache.fineract.cn.command.annotation.CommandHandler;
import org.apache.fineract.cn.command.annotation.EventEmitter;
import org.apache.fineract.cn.lang.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Padma Raju Sattineni
 */
@Aggregate
public class RatingAndCommentCommandHandler {
    private final RatingRepository ratingRepository;
    private final CauseRepository causeRepository;

    @Autowired
    public RatingAndCommentCommandHandler(
            final RatingRepository ratingRepository,
            final CauseRepository causeRepository) {

        this.ratingRepository = ratingRepository;
        this.causeRepository = causeRepository;
    }


    private CauseEntity findCauseEntityOrThrow(String identifier) {
        return this.causeRepository.findByIdentifier(identifier).orElseThrow(() -> ServiceException.notFound("Cause ''{0}'' not found", identifier));
    }


    @Transactional
    @CommandHandler
    @EventEmitter(selectorName = CauseEventConstants.SELECTOR_NAME, selectorValue = CauseEventConstants.POST_RATING)
    public CauseRating createRating(final CreateRatingCommand createRatingCommand) {
        final CauseEntity causeEntity = findCauseEntityOrThrow(createRatingCommand.getCauseIdentifier());
        CauseRating causeRating = createRatingCommand.getRating();
        RatingEntity ratingEntity = RatingMapper.map(causeRating, causeEntity);
        RatingEntity entity = this.ratingRepository.save(ratingEntity);
        return new CauseRating(entity.getId(),
                entity.getRating(),
                entity.getComment(),
                entity.getRef(),
                entity.getActive(),
                entity.getCreatedBy(),
                entity.getCreatedOn().toString());
    }


}

