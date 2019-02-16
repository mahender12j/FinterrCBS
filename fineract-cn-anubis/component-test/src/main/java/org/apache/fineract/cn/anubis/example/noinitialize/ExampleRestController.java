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
package org.apache.fineract.cn.anubis.example.noinitialize;

import org.apache.fineract.cn.anubis.annotation.AcceptedTokenType;
import org.apache.fineract.cn.anubis.annotation.Permittable;
import org.apache.fineract.cn.api.util.UserContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Myrle Krantz
 */
@RestController
public class ExampleRestController {
  @RequestMapping(
      value = "/dummy",
      method = RequestMethod.GET
  )
  @Permittable
  public @ResponseBody ResponseEntity<Void> readPermittableResource() {
    return ResponseEntity.accepted().build();
  }

  @RequestMapping(
      value = "/dummy",
      method = RequestMethod.POST
  )
  @Permittable
  public @ResponseBody ResponseEntity<Void> changePermittableResource() {
    return ResponseEntity.accepted().build();
  }

  @RequestMapping(
      value = "/dummy",
      method = RequestMethod.DELETE
  )
  @Permittable
  public @ResponseBody ResponseEntity<Void> deletePermittableResource() {
    return ResponseEntity.accepted().build();
  }

  @RequestMapping(
      value = "/usercontext",
      method = RequestMethod.GET
  )
  @Permittable
  public @ResponseBody ResponseEntity<UserContext> getUserContext() {
    //noinspection OptionalGetWithoutIsPresent
    return UserContextHolder.getUserContext()
        .map(x -> ResponseEntity.ok(new UserContext(x.getUser(), x.getAccessToken())))
        .orElse(ResponseEntity.badRequest().body(new UserContext()));
  }

  @RequestMapping(
      value = "/systemendpoint",
      method = RequestMethod.POST
  )
  public @ResponseBody ResponseEntity<Void> callSystemEndpoint() {
    return ResponseEntity.ok().build();
  }

  @RequestMapping(
          value = "/parameterized/{endpoint}/with/{multiple}/parameters",
          method = RequestMethod.GET
  )
  @Permittable(value = AcceptedTokenType.TENANT, groupId = "endpointGroup")
  @Permittable(value = AcceptedTokenType.TENANT, groupId = "endpointGroupWithParameters", permittedEndpoint = "/parameterized/{useridentifier}/with/*/parameters")
  public @ResponseBody ResponseEntity<String> callParameterizedEndpoint(
          @PathVariable("endpoint") final String userIdentifier,
          @PathVariable("multiple") final String otherParameter) {
    return ResponseEntity.ok(userIdentifier + otherParameter + 42);
  }
}
