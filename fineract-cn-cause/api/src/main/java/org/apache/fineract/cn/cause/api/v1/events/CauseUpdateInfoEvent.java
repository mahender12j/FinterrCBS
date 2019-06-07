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
package org.apache.fineract.cn.cause.api.v1.events;

import org.apache.fineract.cn.cause.api.v1.domain.CauseUpdate;
import org.apache.fineract.cn.cause.api.v1.domain.CauseUpdateInfo;

/**
 * @author MD ROBIUL HASSAN
 */
public class CauseUpdateInfoEvent {

    private String causeIdentifier;
    private CauseUpdateInfo causeUpdateInfo;


    public CauseUpdateInfoEvent(String causeIdentifier, CauseUpdateInfo causeUpdateInfo) {
        this.causeIdentifier = causeIdentifier;
        this.causeUpdateInfo = causeUpdateInfo;
    }


    public String getCauseIdentifier() {
        return causeIdentifier;
    }

    public void setCauseIdentifier(String causeIdentifier) {
        this.causeIdentifier = causeIdentifier;
    }

    public CauseUpdateInfo getCauseUpdateInfo() {
        return causeUpdateInfo;
    }

    public void setCauseUpdateInfo(CauseUpdateInfo causeUpdateInfo) {
        this.causeUpdateInfo = causeUpdateInfo;
    }

    @Override
    public String toString() {
        return "CauseUpdateInfoEvent{" +
                "causeIdentifier='" + causeIdentifier + '\'' +
                ", causeUpdateInfo=" + causeUpdateInfo +
                '}';
    }
}
