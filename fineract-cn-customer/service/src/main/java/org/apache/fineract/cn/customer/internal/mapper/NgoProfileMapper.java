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
package org.apache.fineract.cn.customer.internal.mapper;

import org.apache.fineract.cn.api.util.UserContextHolder;
import org.apache.fineract.cn.customer.api.v1.domain.NgoFile;
import org.apache.fineract.cn.customer.api.v1.domain.NgoProfile;
import org.apache.fineract.cn.customer.internal.repository.CustomerEntity;
import org.apache.fineract.cn.customer.internal.repository.NgoFileEntity;
import org.apache.fineract.cn.customer.internal.repository.NgoProfileEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public final class NgoProfileMapper {

    private NgoProfileMapper() {
        super();
    }

    public static NgoProfile map(final NgoProfileEntity profileEntity) {
        NgoProfile profile = new NgoProfile();
        profile.setId(profileEntity.getId());
        profile.setAbout(profileEntity.getAbout());
        profile.setBannerImage(profileEntity.getBannerImage());
        profile.setCreatedOn(profileEntity.getCreatedOn().toString());
        profile.setFacebookUrl(profileEntity.getFacebookUrl());
        profile.setInstagramUrl(profileEntity.getInstagramUrl());
        profile.setLinkedinUrl(profileEntity.getLinkedinUrl());
        profile.setPrintrestUrl(profileEntity.getPrintrestUrl());
        profile.setTwitterUrl(profileEntity.getTwitterUrl());
        profile.setCategory(profileEntity.getCategory());
        profile.setNgoIdentifier(UserContextHolder.checkedGetUser());
        return profile;
    }

    public static NgoProfileEntity map(final NgoProfile profile, final CustomerEntity entity) {
        NgoProfileEntity profileEntity = new NgoProfileEntity();
        profileEntity.setAbout(profile.getAbout());
        profileEntity.setBannerImage(profile.getBannerImage());
        profileEntity.setCreatedOn(LocalDateTime.now());
        profileEntity.setCustomer(entity);
        profileEntity.setFacebookUrl(profile.getFacebookUrl());
        profileEntity.setInstagramUrl(profile.getInstagramUrl());
        profileEntity.setLinkedinUrl(profile.getLinkedinUrl());
        profileEntity.setPrintrestUrl(profile.getPrintrestUrl());
        profileEntity.setTwitterUrl(profile.getTwitterUrl());
        profileEntity.setCategory(profile.getCategory());
        return profileEntity;
    }

    public static NgoFileEntity map(final NgoFile ngoFile, final NgoProfileEntity profileEntity) {
        NgoFileEntity entity = new NgoFileEntity();
        entity.setCreatedOn(LocalDateTime.now());
        entity.setProfileEntity(profileEntity);
        entity.setRef(ngoFile.getRef());
        entity.setTitle(ngoFile.getTitle());
        entity.setType(ngoFile.getType());
        return entity;
    }

    public static NgoFile map(final NgoFileEntity entity) {
        NgoFile ngoFile = new NgoFile();
        ngoFile.setCreatedOn(entity.getCreatedOn().toString());
        ngoFile.setId(entity.getId());
        ngoFile.setRef(entity.getRef());
        ngoFile.setType(entity.getType());
        ngoFile.setTitle(entity.getTitle());
        return ngoFile;
    }
}
