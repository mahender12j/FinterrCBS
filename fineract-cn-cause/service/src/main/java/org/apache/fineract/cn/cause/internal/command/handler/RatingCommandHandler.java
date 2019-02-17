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
import org.apache.fineract.cn.cause.api.v1.events.RatingEvent;
import org.apache.fineract.cn.cause.internal.command.*;
import org.apache.fineract.cn.cause.internal.mapper.RatingMapper;
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
public class RatingCommandHandler {
    private final RatingRepository ratingRepository;
    private final CauseRepository causeRepository;

    @Autowired
    public RatingCommandHandler(
            final RatingRepository ratingRepository,
            final CauseRepository causeRepository) {

        this.ratingRepository = ratingRepository;
        this.causeRepository = causeRepository;
    }

    @Transactional
    @CommandHandler
    @EventEmitter(selectorName = CauseEventConstants.SELECTOR_NAME, selectorValue = CauseEventConstants.POST_RATING)
    public RatingEvent process(final CreateRatingCommand command) throws IOException {
        causeRepository.findByIdentifier(command.getCauseIdentifier())
                .map(causeEntity -> RatingMapper.map(command.getCauseRating(), causeEntity))
                .ifPresent(ratingRepository::save);

        return new RatingEvent(command.getCauseIdentifier(), command.getCauseRating().getIdentifier());
    }

/*    @Transactional
    @CommandHandler
    @EventEmitter(selectorName = CauseEventConstants.SELECTOR_NAME, selectorValue = CauseEventConstants.PUT_RATING)
    public RatingEvent process(final ChangeRatingCommand command) throws IOException {
        final RatingEntity existingRating = ratingRepository.findByCauseIdAndRatingIdentifier(
                command.getCauseIdentifier(), command.getCauseRating().getIdentifier())
                .orElseThrow(() ->
                        ServiceException.notFound("Rating ''{0}'' for cause ''{1}'' not found",
                                command.getCauseRating().getIdentifier(), command.getCauseIdentifier()));

        causeRepository.findByIdentifier(command.getCauseIdentifier())
                .map(causeEntity -> RatingMapper.map(command.getCauseRating(), causeEntity))
                .ifPresent(ratingEntity -> {
                    ratingEntity.setId(existingRating.getId());
                    ratingRepository.save(ratingEntity);
                });

        return new RatingEvent(command.getCauseIdentifier(), command.getCauseRating().getIdentifier());
    }

    @Transactional
    @CommandHandler
    @EventEmitter(selectorName = CauseEventConstants.SELECTOR_NAME, selectorValue = CauseEventConstants.DELETE_RATING)
    public RatingEvent process(final DeleteRatingCommand command) throws IOException {
        final RatingEntity existingRating = ratingRepository.findByCauseIdAndRatingIdentifier(
                command.getCauseIdentifier(), command.getRatingIdentifier())
                .orElseThrow(() ->
                        ServiceException.notFound("Rating ''{0}'' for cause ''{1}'' not found",
                                command.getRatingIdentifier(), command.getCauseIdentifier()));
        ratingRepository.delete(existingRating);

        return new RatingEvent(command.getCauseIdentifier(), command.getRatingIdentifier());
    }

    @Transactional
    @CommandHandler
    @EventEmitter(selectorName = CauseEventConstants.SELECTOR_NAME, selectorValue = CauseEventConstants.POST_RATING_COMPLETE)
    public RatingEvent process(final CompleteRatingCommand command) throws IOException {
        final RatingEntity ratingEntity = ratingRepository.findByCauseIdAndRatingIdentifier(
                command.getCauseIdentifier(),
                command.getRatingIdentifier())
                .orElseThrow(() -> ServiceException.badRequest("Rating not found"));

        ratingEntity.setCreatedOn(LocalDateTime.now(Clock.systemUTC()));
        ratingEntity.setCreatedBy(UserContextHolder.checkedGetUser());
        ratingEntity.setCompleted(true);
        ratingRepository.save(ratingEntity);
        return new RatingEvent(command.getCauseIdentifier(), command.getRatingIdentifier());
    }*/    
}

