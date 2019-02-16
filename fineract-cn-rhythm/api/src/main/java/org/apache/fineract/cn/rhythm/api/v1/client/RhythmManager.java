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
package org.apache.fineract.cn.rhythm.api.v1.client;

import org.apache.fineract.cn.rhythm.api.v1.domain.Beat;
import org.apache.fineract.cn.rhythm.api.v1.domain.ClockOffset;
import java.util.List;
import org.apache.fineract.cn.api.util.CustomFeignClientsConfiguration;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author Myrle Krantz
 */
@SuppressWarnings("unused")
@FeignClient(value="rhythm-v1", path="/rhythm/v1", configuration = CustomFeignClientsConfiguration.class)
public interface RhythmManager {
  @RequestMapping(
      value = "/clockoffset",
      method = RequestMethod.PUT,
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE
  )
  void setClockOffset(ClockOffset clockOffset);

  @RequestMapping(
      value = "/clockoffset",
      method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE
  )
  ClockOffset getClockOffset();

  @RequestMapping(
          value = "/applications/{applicationidentifier}",
          method = RequestMethod.DELETE,
          produces = MediaType.ALL_VALUE,
          consumes = MediaType.ALL_VALUE
  )
  void deleteApplication(@PathVariable("applicationidentifier") final String applicationIdentifier);

  @RequestMapping(
          value = "/applications/{applicationidentifier}/beats",
          method = RequestMethod.GET,
          produces = MediaType.ALL_VALUE,
          consumes = MediaType.APPLICATION_JSON_VALUE
  )
  List<Beat> getAllBeatsForApplication(@PathVariable("applicationidentifier") final String applicationIdentifier);

  @RequestMapping(
          value = "/applications/{applicationidentifier}/beats",
          method = RequestMethod.POST,
          produces = MediaType.APPLICATION_JSON_VALUE,
          consumes = MediaType.APPLICATION_JSON_VALUE
  )
  void createBeat(@PathVariable("applicationidentifier") final String applicationIdentifier, final Beat beat);

  @RequestMapping(
          value = "/applications/{applicationidentifier}/beats/{beatidentifier}",
          method = RequestMethod.GET,
          produces = MediaType.APPLICATION_JSON_VALUE,
          consumes = MediaType.APPLICATION_JSON_VALUE
  )
  Beat getBeat(
          @PathVariable("applicationidentifier") final String applicationIdentifier,
          @PathVariable("beatidentifier") final String beatIdentifier);

  @RequestMapping(
          value = "/applications/{applicationidentifier}/beats/{beatidentifier}",
          method = RequestMethod.DELETE,
          produces = MediaType.ALL_VALUE,
          consumes = MediaType.ALL_VALUE
  )
  void deleteBeat(@PathVariable("applicationidentifier") final String applicationIdentifier, @PathVariable("beatidentifier") final String beatIdentifier);
}
