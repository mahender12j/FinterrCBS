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

import java.util.Optional;

/**
 * @author Padma Raju Sattineni
 */

@Repository
public interface CauseRepository extends JpaRepository<CauseEntity, Long> {

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN 'true' ELSE 'false' END FROM CauseEntity c WHERE c.identifier = :identifier")
    Boolean existsByIdentifier(@Param("identifier") final String identifier);

    Page<CauseEntity> findByIdentifierContainingOrTitleContainingOrDescriptionContaining(
            final String identifier, final String title, final String description, final Pageable pageable);

    Optional<CauseEntity> findByIdentifier(final String identifier);

    Optional<CauseEntity> findByIdentifierAndCurrentState(final String identifier, final String state);

    Page<CauseEntity> findByCurrentStateNot(final String state, final Pageable pageable);

    Page<CauseEntity> findByCurrentState(final String state, final Pageable pageable);

    Page<CauseEntity> findByCreatedBy(final String createdBy, final Pageable pageable);

    Page<CauseEntity> findByCreatedByAndCurrentStateNot(final String createdBy, final String state, final Pageable pageable);

    Page<CauseEntity> findByCreatedByAndIdentifierContainingOrTitleContainingOrDescriptionContaining(
        final String createdBy, final String identifier, final String title, final String description, final Pageable pageable);

    Page<CauseEntity> findByCreatedByAndIdentifierContainingOrTitleContainingOrDescriptionContainingAndCurrentStateNot(
        final String createdBy, final String identifier, final String title, final String description, final String state, final Pageable pageable);

    Page<CauseEntity> findByCurrentStateNotAndIdentifierContainingOrTitleContainingOrDescriptionContaining(
            final String state, final String identifier, final String title, final String description, final Pageable pageable);

    Page<CauseEntity> findByCurrentStateAndIdentifierContainingOrTitleContainingOrDescriptionContaining(
        final String state, final String identifier, final String title, final String description, final Pageable pageable);
}

