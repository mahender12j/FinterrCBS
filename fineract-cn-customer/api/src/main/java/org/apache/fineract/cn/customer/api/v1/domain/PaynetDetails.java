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

import javax.validation.constraints.NotNull;

public final class PaynetDetails {

    @NotNull
    private String final_checkSum;
    @NotNull
    private String fpx_msgType;
    @NotNull
    private String fpx_msgToken;
    @NotNull
    private String fpx_sellerExId;
    @NotNull
    private String fpx_version;

    @NotNull
    private String fpx_url;

    public PaynetDetails() {
    }

    public String getFinal_checkSum() {
        return final_checkSum;
    }

    public void setFinal_checkSum(String final_checkSum) {
        this.final_checkSum = final_checkSum;
    }

    public String getFpx_msgType() {
        return fpx_msgType;
    }

    public void setFpx_msgType(String fpx_msgType) {
        this.fpx_msgType = fpx_msgType;
    }

    public String getFpx_msgToken() {
        return fpx_msgToken;
    }

    public void setFpx_msgToken(String fpx_msgToken) {
        this.fpx_msgToken = fpx_msgToken;
    }

    public String getFpx_sellerExId() {
        return fpx_sellerExId;
    }

    public void setFpx_sellerExId(String fpx_sellerExId) {
        this.fpx_sellerExId = fpx_sellerExId;
    }

    public String getFpx_version() {
        return fpx_version;
    }

    public void setFpx_version(String fpx_version) {
        this.fpx_version = fpx_version;
    }

    public String getFpx_url() {
        return fpx_url;
    }

    public void setFpx_url(String fpx_url) {
        this.fpx_url = fpx_url;
    }
}
