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

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * @author Padma Raju Sattineni
 */

@Repository
public interface CauseRepository extends JpaRepository<CauseEntity, Long> {

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN 'true' ELSE 'false' END FROM CauseEntity c WHERE c.identifier = :identifier")
    Boolean existsByIdentifier(@Param("identifier") final String identifier);

    Optional<CauseEntity> findByIdentifier(final String identifier);

//    Page<CauseEntity> findByCurrentStateNotAndStartDateLessThanEqual(final String state, final LocalDateTime dateTime, final Pageable pageable);

//    List<CauseEntity> findByCurrentStateNotAndStartDateLessThanEqual(final String state, final LocalDateTime dateTime);

    Page<CauseEntity> findByCurrentStateAndStartDateLessThanEqual(final String state, final LocalDateTime dateTime, final Pageable pageable);

    List<CauseEntity> findByCurrentStateAndStartDateLessThanEqual(final String state, final LocalDateTime dateTime);

    List<CauseEntity> findByCreatedByAndCurrentStateNot(final String createdBy, final String state);

    Page<CauseEntity> findByCreatedByAndCurrentStateNot(final String createdBy, final String state, final Pageable pageable);

    Page<CauseEntity> findByCurrentState(final String state, final Pageable pageable);

    Page<CauseEntity> findByCreatedByAndCurrentState(final String createdBy, final String state, final Pageable pageable);

    List<CauseEntity> findByCreatedByAndCurrentState(final String createdBy, final String state);

    Page<CauseEntity> findByCategoryAndCurrentStateAndStartDateLessThanEqual(CategoryEntity entity, final String state, final LocalDateTime localDateTime, Pageable pageable);

    List<CauseEntity> findByCategoryAndCurrentStateAndStartDateLessThanEqual(CategoryEntity entity, final String state, final LocalDateTime localDateTime);

    Page<CauseEntity> findAll(Pageable pageable);

    @Query("select c from CauseEntity c where c.endDate <= current_date and (c.currentState = :active or c.currentState = :extended)")
    List<CauseEntity> findByEndDateAndCurrentState(@Param("active") final String active, @Param("extended") final String extended);

    @Query("select c from CauseEntity c where c.approvedOn <= :calculated_date and c.currentState = :approve")
    List<CauseEntity> findByApproveDate(@Param("calculated_date") final LocalDateTime calculated_date, @Param("approve") String approve);

//    select * from cass_causes where DATE_ADD(approved_on, interval 12 DAY) <= now()
}

