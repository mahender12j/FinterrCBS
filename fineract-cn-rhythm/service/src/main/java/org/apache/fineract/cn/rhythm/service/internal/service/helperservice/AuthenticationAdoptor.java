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

/*
 ebenezergraham created on 8/4/18
*/

import org.apache.fineract.cn.api.util.UserContextHolder;
import org.apache.fineract.cn.identity.api.v1.client.IdentityManager;
import org.apache.fineract.cn.identity.api.v1.domain.Authentication;
import org.apache.fineract.cn.lang.TenantContextHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationAdoptor {

    @Value("${rhythm.password}")
    private String USER_PASSWORD;
    @Value("${rhythm.user}")
    private String USER_IDENTIFIER;

    private IdentityManager identityManager;

    @Autowired
    public AuthenticationAdoptor(IdentityManager identityManager) {
        this.identityManager = identityManager;
    }

    String authenticate(String tenant) {
        System.out.println("username: " + USER_IDENTIFIER);
        System.out.println("password: " + USER_PASSWORD);
        TenantContextHolder.clear();
        TenantContextHolder.setIdentifier(tenant);
        final Authentication authentication = this.identityManager.login(USER_IDENTIFIER, USER_PASSWORD);
        UserContextHolder.clear();
        UserContextHolder.setAccessToken(USER_IDENTIFIER, authentication.getAccessToken());
        System.out.println("User context: " + UserContextHolder.checkedGetUser());
        return authentication.getAccessToken();
    }
}