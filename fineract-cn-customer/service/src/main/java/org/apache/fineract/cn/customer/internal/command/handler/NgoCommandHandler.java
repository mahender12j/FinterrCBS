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
package org.apache.fineract.cn.customer.internal.command.handler;

import org.apache.fineract.cn.command.annotation.Aggregate;
import org.apache.fineract.cn.command.annotation.CommandHandler;
import org.apache.fineract.cn.command.annotation.EventEmitter;
import org.apache.fineract.cn.customer.api.v1.CustomerEventConstants;
import org.apache.fineract.cn.customer.api.v1.domain.NgoFile;
import org.apache.fineract.cn.customer.api.v1.domain.NgoProfile;
import org.apache.fineract.cn.customer.internal.command.CreateNGOCommand;
import org.apache.fineract.cn.customer.internal.command.UpdateNGOCommand;
import org.apache.fineract.cn.customer.internal.mapper.NgoProfileMapper;
import org.apache.fineract.cn.customer.internal.repository.*;
import org.apache.fineract.cn.lang.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author MD ROBIUL HASSAN
 */
@Aggregate
public class NgoCommandHandler {

    private final NgoProfileRepository profileRepository;
    private final NgoFileRepository ngoFileRepository;
    private final CustomerRepository customerRepository;

    @Autowired
    public NgoCommandHandler(final NgoProfileRepository profileRepository,
                             final NgoFileRepository ngoFileRepository,
                             final CustomerRepository customerRepository) {

        this.profileRepository = profileRepository;
        this.ngoFileRepository = ngoFileRepository;
        this.customerRepository = customerRepository;
    }


    @Transactional
    @CommandHandler
    @EventEmitter(selectorName = CustomerEventConstants.SELECTOR_NAME, selectorValue = CustomerEventConstants.POST_NGO_PROFILE)
    public String PostNGOProfile(final CreateNGOCommand ngoCommand) {
        CustomerEntity customerEntity = this.customerRepository.findByIdentifier(ngoCommand.getIdentifier()).orElseThrow(() -> ServiceException.notFound("NGO not found"));
        NgoProfileEntity profileEntity = NgoProfileMapper.map(ngoCommand.getNgoProfile(), customerEntity);
        this.profileRepository.save(profileEntity);

        Map<String, List<NgoFile>> ngoFileMap = ngoCommand.getNgoProfile().getNgoFiles();

        List<NgoFileEntity> entities = new ArrayList<>();
        ngoFileMap.forEach((k, v) -> v.stream().map(ngoFile -> NgoProfileMapper.map(ngoFile, profileEntity)).forEach(entities::add));
        this.ngoFileRepository.save(entities);
        return ngoCommand.toString();
    }


    @Transactional
    @CommandHandler
    @EventEmitter(selectorName = CustomerEventConstants.SELECTOR_NAME, selectorValue = CustomerEventConstants.POST_NGO_PROFILE)
    public String PutNGOProfile(final UpdateNGOCommand updateNGOCommand) {
        CustomerEntity customerEntity = this.customerRepository.findByIdentifier(updateNGOCommand.getIdentifier()).orElseThrow(() -> ServiceException.notFound("NGO not found"));
        NgoProfileEntity profileEntity = this.profileRepository.findByCustomer(customerEntity).orElseThrow(() -> ServiceException.notFound("NGO not found"));

        NgoProfile ngoProfile = updateNGOCommand.getNgoProfile();

        profileEntity.setTwitterUrl(ngoProfile.getTwitterUrl());
        profileEntity.setPrintrestUrl(ngoProfile.getPinterestUrl());
        profileEntity.setLinkedinUrl(ngoProfile.getLinkedinUrl());
        profileEntity.setInstagramUrl(ngoProfile.getInstagramUrl());
        profileEntity.setFacebookUrl(ngoProfile.getFacebookUrl());
        profileEntity.setBannerImage(ngoProfile.getBannerImage());
        profileEntity.setAbout(ngoProfile.getAbout());
        profileEntity.setCategory(ngoProfile.getCategory());
        this.profileRepository.save(profileEntity);


        List<NgoFileEntity> fileEntities = this.ngoFileRepository.findAllByProfileEntity(profileEntity);
        this.ngoFileRepository.delete(fileEntities);

        Map<String, List<NgoFile>> ngoFileMap = ngoProfile.getNgoFiles();
        List<NgoFileEntity> entities = new ArrayList<>();
        ngoFileMap.forEach((k, v) -> v.stream().map(ngoFile -> NgoProfileMapper.map(ngoFile, profileEntity)).forEach(entities::add));
        this.ngoFileRepository.save(entities);
        return updateNGOCommand.toString();
    }
}
