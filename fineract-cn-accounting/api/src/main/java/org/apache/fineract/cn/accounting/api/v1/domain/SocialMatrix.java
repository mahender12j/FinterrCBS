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
package org.apache.fineract.cn.accounting.api.v1.domain;

import org.apache.fineract.cn.lang.validation.constraints.ValidIdentifier;

import java.util.Objects;

@SuppressWarnings({"unused"})
public final class SocialMatrix {

    @ValidIdentifier(maxLength = 34)
    private String identifier;
    private Double numberOfTrees;
    private Double goldenDonor;
    private int batteryPower;

    public SocialMatrix() {
        super();
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public Double getNumberOfTrees() {
        return numberOfTrees;
    }

    public void setNumberOfTrees(Double numberOfTrees) {
        this.numberOfTrees = numberOfTrees;
    }

    public Double getGoldenDonor() {
        return goldenDonor;
    }

    public void setGoldenDonor(Double goldenDonor) {
        this.goldenDonor = goldenDonor;
    }

    public int getBatteryPower() {
        return batteryPower;
    }

    public void setBatteryPower(int batteryPower) {
        this.batteryPower = batteryPower;
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier, numberOfTrees, goldenDonor, batteryPower);
    }

    @Override
    public String toString() {
        return "Account{" +
                ", identifier='" + identifier + '\'' +
                ", balance=" + numberOfTrees + '\'' +
                ", totalDonation=" + goldenDonor + '\'' +
                ", numberOfDonation=" + batteryPower +
                '}';
    }
}
