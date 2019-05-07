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
package org.apache.fineract.cn.customer.internal.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<CustomerEntity, Long> {

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN 'true' ELSE 'false' END FROM CustomerEntity c WHERE c.identifier = :identifier")
    Boolean existsByIdentifier(@Param("identifier") final String identifier);


//    Page<CustomerEntity> findAllByCurrentState(final String type, final Pageable pageable);

    @Query("select COUNT(c) from  CustomerEntity c where c.referenceCustomer=:identifier")
    Integer findAllByRefferalCodeIdentifier(@Param("identifier") final String identifier);

    Page<CustomerEntity> findByIdentifierContainingOrGivenNameContainingOrSurnameContaining(
            final String identifier, final String givenName, final String surname, final Pageable pageable);

    Page<CustomerEntity> findByReferenceCustomerAndIdentifierContainingOrGivenNameContainingOrSurnameContaining(
            final String refferalCodeIdentifier, final String identifier, final String givenName, final String surname, final Pageable pageable);

    Page<CustomerEntity> findAllByReferenceCustomer(final String refferalCodeIdentifier, final Pageable pageable);

    Optional<CustomerEntity> findByIdentifier(final String identifier);

    Optional<CustomerEntity> findByIdentifierAndType(final String identifier, final String userType);

    Optional<CustomerEntity> findByRefferalCodeIdentifier(final String referralCode);

    Page<CustomerEntity> findByCurrentStateNot(final String state, final Pageable pageable);

    Page<CustomerEntity> findByReferenceCustomerAndCurrentStateNot(final String refferalCodeIdentifier, final String state, final Pageable pageable);

    Page<CustomerEntity> findByCurrentStateNotAndIdentifierContainingOrGivenNameContainingOrSurnameContaining(
            final String state, final String identifier, final String givenName, final String surname, final Pageable pageable);

    Page<CustomerEntity> findByReferenceCustomerAndCurrentStateNotAndIdentifierContainingOrGivenNameContainingOrSurnameContaining(
            final String refferalCodeIdentifier, final String state, final String identifier, final String givenName, final String surname, final Pageable pageable);
}
