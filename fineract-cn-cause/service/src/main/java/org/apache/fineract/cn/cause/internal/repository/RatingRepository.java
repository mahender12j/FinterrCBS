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
package org.apache.fineract.cn.cause.internal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.stream.Stream;
import java.util.List;

/**
 * @author Padma Raju Sattineni
 */
@Repository
public interface RatingRepository extends JpaRepository<RatingEntity, Long> {

    @Query("SELECT d FROM RatingEntity d WHERE d.cause.identifier = :causeIdentifier AND d.identifier = :ratingIdentifier")
    Optional<RatingEntity> findByCauseIdAndRatingIdentifier(
            @Param("causeIdentifier") String causeIdentifier, @Param("ratingIdentifier") String ratingIdentifier);

    @Query("SELECT d FROM RatingEntity d WHERE d.cause.identifier = :causeIdentifier")
    Stream<RatingEntity> findByCauseId(
            @Param("causeIdentifier") String causeIdentifier);

        @Query("SELECT avg(d.identifier) FROM RatingEntity d WHERE d.cause.identifier = :causeIdentifier")
        Double findAvgRatingByCauseId(
                @Param("causeIdentifier") String causeIdentifier);

        Stream<RatingEntity> findByCause(final CauseEntity causeEntity);

        Stream<RatingEntity> findByCauseAndActive(final CauseEntity causeEntity, Boolean active);

        @Query("SELECT CASE WHEN COUNT(r) > 0 THEN 'true' ELSE 'false' END FROM RatingEntity r WHERE r.createdBy = :createdBy")
        Boolean existsByCreatedBy(@Param("createdBy") final String createdBy);
}

