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

import java.util.Objects;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;

import org.apache.fineract.cn.lang.validation.constraints.ValidIdentifier;

public final class CauseDebtor {
    @ValidIdentifier(
            maxLength = 34
    )
    private String accountNumber;
    @NotNull
    @DecimalMin(
            value = "0.00",
            inclusive = false
    )
    private String amount;

    public CauseDebtor() {
    }

    public CauseDebtor(String accountNumber, String amount) {
        this.accountNumber = accountNumber;
        this.amount = amount;
    }

    public String getAccountNumber() {
        return this.accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getAmount() {
        return this.amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o != null && this.getClass() == o.getClass()) {
            CauseDebtor debtor = (CauseDebtor) o;
            return Objects.equals(this.accountNumber, debtor.accountNumber) && Objects.equals(this.amount, debtor.amount);
        } else {
            return false;
        }
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.accountNumber, this.amount});
    }

    public String toString() {
        return "Debtor{accountNumber='" + this.accountNumber + '\'' + ", amount='" + this.amount + '\'' + '}';
    }
}

