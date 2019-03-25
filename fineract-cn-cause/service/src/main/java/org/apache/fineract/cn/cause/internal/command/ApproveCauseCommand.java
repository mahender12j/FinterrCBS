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
package org.apache.fineract.cn.cause.internal.command;

import java.time.LocalDateTime;

public class ApproveCauseCommand {


    private final String identifier;
    private final Double finRate;
    private final Double successFees;
    private final LocalDateTime publishDate;


    public ApproveCauseCommand(final String identifier,
                               final Double finRate,
                               final Double successFees,
                               final LocalDateTime publishDate) {
        super();
        this.identifier = identifier;
        this.finRate = finRate;
        this.successFees = successFees;
        this.publishDate = publishDate;
    }

    public String getIdentifier() {
        return identifier;
    }

    public Double getFinRate() {
        return finRate;
    }

    public Double getSuccessFees() {
        return successFees;
    }

    public LocalDateTime getPublishDate() {
        return publishDate;
    }
}
