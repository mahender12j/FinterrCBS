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
package org.apache.fineract.cn.cause.api.v1.domain;

import org.hibernate.validator.constraints.NotBlank;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Padma Raju Sattineni
 */

public class CauseApprove {

    private Double finRate;
    private Double successFees;

    public CauseApprove() {
    }

    public Double getFinRate() {
        return finRate;
    }

    public void setFinRate(Double finRate) {
        this.finRate = finRate;
    }

    public Double getSuccessFees() {
        return successFees;
    }

    public void setSuccessFees(Double successFees) {
        this.successFees = successFees;
    }

    @Override
    public String toString() {
        return "CauseApprove{" +
                "finRate=" + finRate +
                ", successFees=" + successFees +
                '}';
    }
}

