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

public final class AERequest {

    @NotNull
    private String fpx_msgType;
    @NotNull
    private String fpx_msgToken;
    @NotNull
    private String fpx_sellerExId;
    @NotNull
    private String fpx_sellerExOrderNo;
    @NotNull
    private String fpx_sellerTxnTime;
    @NotNull
    private String fpx_sellerOrderNo;
    @NotNull
    private String fpx_sellerId;
    @NotNull
    private String fpx_sellerBankCode;
    @NotNull
    private String fpx_txnCurrency;
    @NotNull
    private String fpx_txnAmount;
    @NotNull
    private String fpx_buyerEmail;
    @NotNull
    private String fpx_buyerName;
    @NotNull
    private String fpx_buyerBankId;
    @NotNull
    private String fpx_buyerBankBranch;
    @NotNull
    private String fpx_buyerAccNo;
    @NotNull
    private String fpx_buyerId;
    @NotNull
    private String fpx_makerName;
    @NotNull
    private String fpx_buyerIban;
    @NotNull
    private String fpx_productDesc;
    @NotNull
    private String fpx_version;
    @NotNull
    private String fpx_checkSum;

    @NotNull
    private String fpx_url;

    public AERequest() {
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

    public String getFpx_sellerExOrderNo() {
        return fpx_sellerExOrderNo;
    }

    public void setFpx_sellerExOrderNo(String fpx_sellerExOrderNo) {
        this.fpx_sellerExOrderNo = fpx_sellerExOrderNo;
    }

    public String getFpx_sellerTxnTime() {
        return fpx_sellerTxnTime;
    }

    public void setFpx_sellerTxnTime(String fpx_sellerTxnTime) {
        this.fpx_sellerTxnTime = fpx_sellerTxnTime;
    }

    public String getFpx_sellerOrderNo() {
        return fpx_sellerOrderNo;
    }

    public void setFpx_sellerOrderNo(String fpx_sellerOrderNo) {
        this.fpx_sellerOrderNo = fpx_sellerOrderNo;
    }

    public String getFpx_sellerId() {
        return fpx_sellerId;
    }

    public void setFpx_sellerId(String fpx_sellerId) {
        this.fpx_sellerId = fpx_sellerId;
    }

    public String getFpx_sellerBankCode() {
        return fpx_sellerBankCode;
    }

    public void setFpx_sellerBankCode(String fpx_sellerBankCode) {
        this.fpx_sellerBankCode = fpx_sellerBankCode;
    }

    public String getFpx_txnCurrency() {
        return fpx_txnCurrency;
    }

    public void setFpx_txnCurrency(String fpx_txnCurrency) {
        this.fpx_txnCurrency = fpx_txnCurrency;
    }

    public String getFpx_txnAmount() {
        return fpx_txnAmount;
    }

    public void setFpx_txnAmount(String fpx_txnAmount) {
        this.fpx_txnAmount = fpx_txnAmount;
    }

    public String getFpx_buyerEmail() {
        return fpx_buyerEmail;
    }

    public void setFpx_buyerEmail(String fpx_buyerEmail) {
        this.fpx_buyerEmail = fpx_buyerEmail;
    }

    public String getFpx_buyerName() {
        return fpx_buyerName;
    }

    public void setFpx_buyerName(String fpx_buyerName) {
        this.fpx_buyerName = fpx_buyerName;
    }

    public String getFpx_buyerBankId() {
        return fpx_buyerBankId;
    }

    public void setFpx_buyerBankId(String fpx_buyerBankId) {
        this.fpx_buyerBankId = fpx_buyerBankId;
    }

    public String getFpx_buyerBankBranch() {
        return fpx_buyerBankBranch;
    }

    public void setFpx_buyerBankBranch(String fpx_buyerBankBranch) {
        this.fpx_buyerBankBranch = fpx_buyerBankBranch;
    }

    public String getFpx_buyerAccNo() {
        return fpx_buyerAccNo;
    }

    public void setFpx_buyerAccNo(String fpx_buyerAccNo) {
        this.fpx_buyerAccNo = fpx_buyerAccNo;
    }

    public String getFpx_buyerId() {
        return fpx_buyerId;
    }

    public void setFpx_buyerId(String fpx_buyerId) {
        this.fpx_buyerId = fpx_buyerId;
    }

    public String getFpx_makerName() {
        return fpx_makerName;
    }

    public void setFpx_makerName(String fpx_makerName) {
        this.fpx_makerName = fpx_makerName;
    }

    public String getFpx_buyerIban() {
        return fpx_buyerIban;
    }

    public void setFpx_buyerIban(String fpx_buyerIban) {
        this.fpx_buyerIban = fpx_buyerIban;
    }

    public String getFpx_productDesc() {
        return fpx_productDesc;
    }

    public void setFpx_productDesc(String fpx_productDesc) {
        this.fpx_productDesc = fpx_productDesc;
    }

    public String getFpx_version() {
        return fpx_version;
    }

    public void setFpx_version(String fpx_version) {
        this.fpx_version = fpx_version;
    }

    public String getFpx_checkSum() {
        return fpx_checkSum;
    }

    public void setFpx_checkSum(String fpx_checkSum) {
        this.fpx_checkSum = fpx_checkSum;
    }

    public String getFpx_url() {
        return fpx_url;
    }

    public void setFpx_url(String fpx_url) {
        this.fpx_url = fpx_url;
    }
}
