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
package org.apache.fineract.cn.rhythm.service.internal.service.helperservice;

import org.apache.fineract.cn.cause.api.v1.client.CauseManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CauseAdaptor {

    private final CauseManager causeManager;
    private final AuthenticationAdoptor authenticationAdoptor;

    @Autowired
    public CauseAdaptor(final CauseManager causeManager, AuthenticationAdoptor authenticationAdoptor) {
        super();
        this.causeManager = causeManager;
        this.authenticationAdoptor = authenticationAdoptor;
    }

    public void CompleteOnHardCapReach(final String tanent) {
        try {
            String accessToken = this.authenticationAdoptor.authenticate(tanent);
//            System.out.println("access token: " + accessToken);
            this.causeManager.causeCompleteOnHardCapReach();
//            System.out.println("after the Hard cash reach api called");
        } catch (final Exception ex) {
            System.out.println("Something went wrong on CompleteOnHardCapReach: " + ex.getMessage());
        }
    }
}
