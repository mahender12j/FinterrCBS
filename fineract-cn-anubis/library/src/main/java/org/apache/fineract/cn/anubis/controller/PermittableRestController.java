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
package org.apache.fineract.cn.anubis.controller;

import org.apache.fineract.cn.anubis.annotation.AcceptedTokenType;
import org.apache.fineract.cn.anubis.annotation.Permittable;
import org.apache.fineract.cn.anubis.api.v1.domain.PermittableEndpoint;
import org.apache.fineract.cn.anubis.service.PermittableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping("/permittables")
public class PermittableRestController {

  private final PermittableService service;

  @Autowired
  public PermittableRestController(final PermittableService service) {
    this.service = service;
  }

  @Permittable(AcceptedTokenType.GUEST)
  @RequestMapping(
      method = RequestMethod.GET,
      consumes = MediaType.ALL_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  public
  @ResponseBody
  ResponseEntity<List<PermittableEndpoint>> fetchPermittableEndpoints() {
    final Set<PermittableEndpoint> permittableEndpoints = service.getPermittableEndpoints(
        Collections.singletonList(AcceptedTokenType.TENANT));

    return ResponseEntity.ok(new ArrayList<>(permittableEndpoints));
  }
}
