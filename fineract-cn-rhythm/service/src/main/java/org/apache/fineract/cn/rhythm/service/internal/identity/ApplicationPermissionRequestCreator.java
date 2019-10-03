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
package org.apache.fineract.cn.rhythm.service.internal.identity;

import org.apache.fineract.cn.anubis.annotation.Permittable;
import org.apache.fineract.cn.api.annotation.ThrowsException;
import org.apache.fineract.cn.identity.api.v1.PermittableGroupIds;
import org.apache.fineract.cn.identity.api.v1.client.ApplicationPermissionAlreadyExistsException;
import org.apache.fineract.cn.identity.api.v1.domain.Permission;
import org.apache.fineract.cn.permittedfeignclient.annotation.EndpointSet;
import org.apache.fineract.cn.permittedfeignclient.annotation.PermittedFeignClientsConfiguration;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;

/**
 * @author Myrle Krantz
 */
@EndpointSet(identifier = "rhythm__v1__identity__v1")
@FeignClient(name = "identity-v1", path = "/identity-v1", configuration = PermittedFeignClientsConfiguration.class)
public interface ApplicationPermissionRequestCreator {

    @RequestMapping(value = "/applications/{applicationidentifier}/permissions", method = RequestMethod.POST,
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.ALL_VALUE})
    @ThrowsException(status = HttpStatus.CONFLICT, exception = ApplicationPermissionAlreadyExistsException.class)
    @Permittable(groupId = PermittableGroupIds.APPLICATION_SELF_MANAGEMENT)
    void createApplicationPermission(@PathVariable("applicationidentifier") String applicationIdentifier,
                                     @RequestBody @Valid Permission permission);
}
