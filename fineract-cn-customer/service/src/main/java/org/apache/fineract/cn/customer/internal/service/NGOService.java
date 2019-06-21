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
package org.apache.fineract.cn.customer.internal.service;

import org.apache.fineract.cn.customer.api.v1.domain.*;
import org.apache.fineract.cn.customer.internal.mapper.NgoProfileMapper;
import org.apache.fineract.cn.customer.internal.repository.*;
import org.apache.fineract.cn.lang.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@Service
public class NGOService {

    private final CustomerService customerService;
    private final NgoProfileRepository profileRepository;
    private final NgoFileRepository ngoFileRepository;


    @Autowired
    public NGOService(final CustomerService customerService,
                      final NgoProfileRepository profileRepository,
                      final NgoFileRepository ngoFileRepository) {
        super();
        this.customerService = customerService;
        this.profileRepository = profileRepository;
        this.ngoFileRepository = ngoFileRepository;
    }


    public boolean ngoProfileExist(final String identifier) {
        return this.profileRepository.existsByCustomerIdentifier(identifier);
    }

    public NgoProfile getNgoProfile(final String identifier) {
        Customer customer = this.customerService.findCustomer(identifier).orElseThrow(() -> ServiceException.notFound("NGO NOT FOUND"));
        NgoProfileEntity profileEntity = this.profileRepository.findByCustomerId(customer.getId()).orElseThrow(() -> ServiceException.notFound("NGO PROFILE NOT FOUND"));
        NgoProfile profile = NgoProfileMapper.map(profileEntity);
        profile.setNgoDetails(customer);

        Map<String, List<NgoFile>> ngoFileMap = this.ngoFileRepository.findAllByProfileEntity(profileEntity)
                .stream()
                .map(NgoProfileMapper::map)
                .collect(Collectors.groupingBy(NgoFile::getType, toList()));

        profile.setNgoFiles(ngoFileMap);
        return profile;
    }


}