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

public final class UserVerification {
    //    used for front-end support
    private boolean profileComplete;
    private boolean emailVerified;
    private boolean mobileVerified;
    private String verifiedMobileNumber;
    private String verifiedEmailAddress;

    public UserVerification(boolean profileComplete, boolean emailVerified, boolean mobileVerified, String verifiedMobileNumber, String verifiedEmailAddress) {
        this.profileComplete = profileComplete;
        this.emailVerified = emailVerified;
        this.mobileVerified = mobileVerified;
        this.verifiedMobileNumber = verifiedMobileNumber;
        this.verifiedEmailAddress = verifiedEmailAddress;
    }

    public UserVerification() {
    }

    public boolean isProfileComplete() {
        return profileComplete;
    }

    public void setProfileComplete(boolean profileComplete) {
        this.profileComplete = profileComplete;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public boolean isMobileVerified() {
        return mobileVerified;
    }

    public void setMobileVerified(boolean mobileVerified) {
        this.mobileVerified = mobileVerified;
    }

    public String getVerifiedMobileNumber() {
        return verifiedMobileNumber;
    }

    public void setVerifiedMobileNumber(String verifiedMobileNumber) {
        this.verifiedMobileNumber = verifiedMobileNumber;
    }

    public String getVerifiedEmailAddress() {
        return verifiedEmailAddress;
    }

    public void setVerifiedEmailAddress(String verifiedEmailAddress) {
        this.verifiedEmailAddress = verifiedEmailAddress;
    }

    @Override
    public String toString() {
        return "UserVerification{" +
                "profileComplete=" + profileComplete +
                ", emailVerified=" + emailVerified +
                ", mobileVerified=" + mobileVerified +
                ", verifiedMobileNumber='" + verifiedMobileNumber + '\'' +
                ", verifiedEmailAddress='" + verifiedEmailAddress + '\'' +
                '}';
    }
}
