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
public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {

    @Query("SELECT d FROM CategoryEntity d WHERE d.cause.identifier = :causeIdentifier AND d.identifier = :categoryIdentifier")
    Optional<CategoryEntity> findByCauseIdAndCategoryIdentifier(
            @Param("causeIdentifier") String causeIdentifier, @Param("categoryIdentifier") String categoryIdentifier);

    @Query("SELECT d FROM CategoryEntity d WHERE d.cause.identifier = :causeIdentifier")
    Stream<CategoryEntity> findByCauseId(
            @Param("causeIdentifier") String causeIdentifier);

    List<CategoryEntity> findByCause(final CauseEntity causeEntity);
}

