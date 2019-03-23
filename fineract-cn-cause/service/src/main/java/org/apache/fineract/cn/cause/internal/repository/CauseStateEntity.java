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

import org.apache.fineract.cn.mariadb.util.LocalDateTimeConverter;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * @author Padma Raju Sattineni
 */

@Entity
@Table(name = "cass_causes_state")
public class CauseStateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cause_id")
    private CauseEntity cause;

    @Column(name = "created_by", nullable = false)
    private String createdBy;


    @Column(name = "new_date", nullable = false)
    private String newDate;

    @Column(name = "type", nullable = false)
    private String type;


    @Column(name = "status", nullable = false)
    private String status;


    @Column(name = "created_on", nullable = false)
    private String createdOn;


    @Column(name = "modified_at", nullable = false)
    private String modifiedAt;


}

