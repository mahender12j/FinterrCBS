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

    private int greenContribution;
    private int goldenDonor;
    private int myPrower;
    private int myInfluence;

    public SocialMatrix() {
        super();
    }


    public int getGreenContribution() {
        return greenContribution;
    }

    public void setGreenContribution(int greenContribution) {
        this.greenContribution = greenContribution;
    }

    public int getGoldenDonor() {
        return goldenDonor;
    }

    public void setGoldenDonor(int goldenDonor) {
        this.goldenDonor = goldenDonor;
    }

    public int getMyPrower() {
        return myPrower;
    }

    public void setMyPrower(int myPrower) {
        this.myPrower = myPrower;
    }

    public int getMyInfluence() {
        return myInfluence;
    }

    public void setMyInfluence(int myInfluence) {
        this.myInfluence = myInfluence;
    }

    @Override
    public String toString() {
        return "SocialMatrix{" +
                "greenContribution=" + greenContribution +
                ", goldenDonor=" + goldenDonor +
                ", myPrower=" + myPrower +
                ", myInfluence=" + myInfluence +
                '}';
    }
}
