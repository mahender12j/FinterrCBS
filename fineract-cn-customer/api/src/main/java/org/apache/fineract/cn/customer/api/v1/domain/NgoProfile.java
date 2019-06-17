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
package org.apache.fineract.cn.customer.api.v1.domain;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * @author Md Robiul Hassan
 */
public class NgoProfile {

    private Long id;
    @Valid
    @NotNull
    private String bannerImage;
    @Valid
    @NotNull
    private String about;
    @Valid
    @NotNull
    private List<String> category;
    private String facebookUrl;
    private String twitterUrl;
    private String instagramUrl;
    private String linkedinUrl;
    private String printrestUrl;
    private String createdOn;
    @Valid
    @NotNull
    private String ngoIdentifier;
    @Valid
    @NotNull
    private Map<String, List<NgoFile>> ngoFiles;
    private Customer ngoDetails;

    public NgoProfile() {
        super();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBannerImage() {
        return bannerImage;
    }

    public void setBannerImage(String bannerImage) {
        this.bannerImage = bannerImage;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getFacebookUrl() {
        return facebookUrl;
    }

    public void setFacebookUrl(String facebookUrl) {
        this.facebookUrl = facebookUrl;
    }

    public String getTwitterUrl() {
        return twitterUrl;
    }

    public void setTwitterUrl(String twitterUrl) {
        this.twitterUrl = twitterUrl;
    }

    public String getInstagramUrl() {
        return instagramUrl;
    }

    public void setInstagramUrl(String instagramUrl) {
        this.instagramUrl = instagramUrl;
    }

    public String getLinkedinUrl() {
        return linkedinUrl;
    }

    public void setLinkedinUrl(String linkedinUrl) {
        this.linkedinUrl = linkedinUrl;
    }

    public String getPrintrestUrl() {
        return printrestUrl;
    }

    public void setPrintrestUrl(String printrestUrl) {
        this.printrestUrl = printrestUrl;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    public Map<String, List<NgoFile>> getNgoFiles() {
        return ngoFiles;
    }

    public void setNgoFiles(Map<String, List<NgoFile>> ngoFiles) {
        this.ngoFiles = ngoFiles;
    }

    public String getNgoIdentifier() {
        return ngoIdentifier;
    }

    public void setNgoIdentifier(String ngoIdentifier) {
        this.ngoIdentifier = ngoIdentifier;
    }

    public Customer getNgoDetails() {
        return ngoDetails;
    }

    public List<String> getCategory() {
        return category;
    }

    public void setCategory(List<String> category) {
        this.category = category;
    }

    public void setNgoDetails(Customer ngoDetails) {
        this.ngoDetails = ngoDetails;
    }

    @Override
    public String toString() {
        return "NgoProfileEntity{" +
                "id=" + id +
                ", bannerImage='" + bannerImage + '\'' +
                ", about='" + about + '\'' +
                ", facebookUrl='" + facebookUrl + '\'' +
                ", twitterUrl=" + twitterUrl +
                ", instagramUrl=" + instagramUrl +
                ", linkedinUrl=" + linkedinUrl +
                ", printrestUrl=" + printrestUrl +
                ", createdOn=" + createdOn +
                '}';
    }
}

