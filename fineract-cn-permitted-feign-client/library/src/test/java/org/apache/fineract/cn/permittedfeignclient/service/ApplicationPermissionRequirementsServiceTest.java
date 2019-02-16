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
package org.apache.fineract.cn.permittedfeignclient.service;

import org.apache.fineract.cn.permittedfeignclient.annotation.EndpointSet;
import org.apache.finearct.cn.permittedfeignclient.api.v1.domain.ApplicationPermission;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.fineract.cn.anubis.annotation.Permittable;
import org.apache.fineract.cn.anubis.api.v1.domain.AllowedOperation;
import org.apache.fineract.cn.identity.api.v1.domain.Permission;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author Myrle Krantz
 */
public class ApplicationPermissionRequirementsServiceTest {
  @FeignClient
  @EndpointSet(identifier = "z")
  interface SampleWithPermissionRequiredFor {
    @RequestMapping(method = RequestMethod.GET)
    @Permittable(groupId = "x")
    void getFoo();

    @RequestMapping(method = RequestMethod.PUT)
    @Permittable(groupId = "x")
    @Permittable(groupId = "y")
    void getBar();

    @RequestMapping(method = RequestMethod.HEAD)
    @Permittable(groupId = "m")
    void headBar();

    @RequestMapping(method = RequestMethod.DELETE)
    @Permittable(groupId = "n")
    void deleteBar();

    @RequestMapping(method = RequestMethod.POST)
    @Permittable(groupId = "o")
    void postBar();

    @RequestMapping(method = RequestMethod.PATCH)
    @Permittable(groupId = "p")
    void patchBar();
  }

  @Test
  public void shouldReturnApplicationPermissionWithRequiredForPermittableGroup() throws Exception {
    final ApplicationPermissionRequirementsService testSubject = new ApplicationPermissionRequirementsService(new Class[]{ SampleWithPermissionRequiredFor.class} );
    final Set<ApplicationPermission> applicationPermissions =
            testSubject.getRequiredPermissions().stream().collect(Collectors.toSet());

    Assert.assertTrue(applicationPermissions.contains(new ApplicationPermission("z",
            new Permission("x", Stream.of(AllowedOperation.READ, AllowedOperation.CHANGE).collect(Collectors.toSet())))));
    Assert.assertTrue(applicationPermissions.contains(new ApplicationPermission("z",
            new Permission("y", Collections.singleton(AllowedOperation.CHANGE)))));
    Assert.assertTrue(applicationPermissions.contains(new ApplicationPermission("z",
            new Permission("m", Collections.singleton(AllowedOperation.READ)))));
    Assert.assertTrue(applicationPermissions.contains(new ApplicationPermission("z",
            new Permission("n", Collections.singleton(AllowedOperation.DELETE)))));
    Assert.assertTrue(applicationPermissions.contains(new ApplicationPermission("z",
            new Permission("o", Collections.singleton(AllowedOperation.CHANGE)))));
    Assert.assertTrue(applicationPermissions.contains(new ApplicationPermission("z",
            new Permission("p", Collections.singleton(AllowedOperation.CHANGE)))));
  }

  @FeignClient
  interface SampleWithoutPermissionRequiredFor {
    @RequestMapping(method = RequestMethod.GET)
    @Permittable(groupId = "x")
    void getFoo();
  }

  @Test
  public void shouldReturnApplicationPermissionWithoutRequiredForPermittableGroup() throws Exception {
    final Set<ApplicationPermission> applicationPermissions =
            ApplicationPermissionRequirementsService.getApplicationPermissionsFromInterface(SampleWithoutPermissionRequiredFor.class)
                    .collect(Collectors.toSet());

    Assert.assertTrue(applicationPermissions.contains(new ApplicationPermission(null,
            new Permission("x", Collections.singleton(AllowedOperation.READ)))));
  }

}