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
package org.apache.fineract.cn.customer.api.v1.domain;

import java.util.List;

public final class SocialMatrix {

    private Double greenContribution; // leave 1-5
    private int totalTrees; // 1 - n number
    private Double goldenDonor; // 1-5
    private Double goldenDonorPercentage; // percentage donar
    private Double myPower; // 1-5
    private Double myPowerPercentage; // percentage
    private int myInfluence;

    //    added for corporate
    private int myInfluenceEmployee;
    private int myInfluenceEmployeeThisMonth;
    private int myInfluenceNgo;
    private int myInfluenceNgoThisMonth;
//    added for corporate

    private List<PerMonthRecord> myPowerPerMonth; // per month power
    private List<PerMonthRecord> myPowerPerMonthCorporate; // per month power corporate

    public SocialMatrix() {
    }


    public Double getGreenContribution() {
        return greenContribution;
    }

    public void setGreenContribution(Double greenContribution) {
        this.greenContribution = greenContribution;
    }


    public Double getGoldenDonor() {
        return goldenDonor;
    }

    public void setGoldenDonor(Double goldenDonor) {
        this.goldenDonor = goldenDonor;
    }

    public Double getGoldenDonorPercentage() {
        return goldenDonorPercentage;
    }

    public void setGoldenDonorPercentage(Double goldenDonorPercentage) {
        this.goldenDonorPercentage = goldenDonorPercentage;
    }

    public Double getMyPower() {
        return myPower;
    }

    public void setMyPower(Double myPower) {
        this.myPower = myPower;
    }

    public Double getMyPowerPercentage() {
        return myPowerPercentage;
    }

    public void setMyPowerPercentage(Double myPowerPercentage) {
        this.myPowerPercentage = myPowerPercentage;
    }

    public int getMyInfluence() {
        return myInfluence;
    }

    public void setMyInfluence(int myInfluence) {
        this.myInfluence = myInfluence;
    }

    public int getTotalTrees() {
        return totalTrees;
    }

    public void setTotalTrees(int totalTrees) {
        this.totalTrees = totalTrees;
    }

    public List<PerMonthRecord> getMyPowerPerMonth() {
        return myPowerPerMonth;
    }

    public void setMyPowerPerMonth(List<PerMonthRecord> myPowerPerMonth) {
        this.myPowerPerMonth = myPowerPerMonth;
    }

    public List<PerMonthRecord> getMyPowerPerMonthCorporate() {
        return myPowerPerMonthCorporate;
    }

    public void setMyPowerPerMonthCorporate(List<PerMonthRecord> myPowerPerMonthCorporate) {
        this.myPowerPerMonthCorporate = myPowerPerMonthCorporate;
    }

    public int getMyInfluenceEmployee() {
        return myInfluenceEmployee;
    }

    public void setMyInfluenceEmployee(int myInfluenceEmployee) {
        this.myInfluenceEmployee = myInfluenceEmployee;
    }

    public int getMyInfluenceEmployeeThisMonth() {
        return myInfluenceEmployeeThisMonth;
    }

    public void setMyInfluenceEmployeeThisMonth(int myInfluenceEmployeeThisMonth) {
        this.myInfluenceEmployeeThisMonth = myInfluenceEmployeeThisMonth;
    }

    public int getMyInfluenceNgo() {
        return myInfluenceNgo;
    }

    public void setMyInfluenceNgo(int myInfluenceNgo) {
        this.myInfluenceNgo = myInfluenceNgo;
    }

    public int getMyInfluenceNgoThisMonth() {
        return myInfluenceNgoThisMonth;
    }

    public void setMyInfluenceNgoThisMonth(int myInfluenceNgoThisMonth) {
        this.myInfluenceNgoThisMonth = myInfluenceNgoThisMonth;
    }

    @Override
    public String toString() {
        return "SocialMatrix{" +
                "greenContribution=" + greenContribution +
                ", totalTrees=" + totalTrees +
                ", goldenDonor=" + goldenDonor +
                ", goldenDonorPercentage=" + goldenDonorPercentage +
                ", myPower=" + myPower +
                ", myPowerPercentage=" + myPowerPercentage +
                ", myInfluence=" + myInfluence +
                '}';
    }
}
