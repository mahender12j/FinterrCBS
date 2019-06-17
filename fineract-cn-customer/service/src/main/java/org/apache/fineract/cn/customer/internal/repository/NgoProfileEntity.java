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
package org.apache.fineract.cn.customer.internal.repository;

import org.apache.fineract.cn.mariadb.util.LocalDateTimeConverter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "maat_ngo_profile")
public class NgoProfileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ngo_id")
    private CustomerEntity customer;
    @Column(name = "banner_image")
    private String bannerImage;
    @Column(name = "about")
    private String about;
    @Column(name = "facebook_url")
    private String facebookUrl;
    @Column(name = "twitter_url")
    private String twitterUrl;
    @Column(name = "instagram_url")
    private String instagramUrl;
    @Column(name = "linkedin_url")
    private String linkedinUrl;
    @Column(name = "printrest_url")
    private String printrestUrl;

    @Column(name = "created_on")
    @Convert(converter = LocalDateTimeConverter.class)
    private LocalDateTime createdOn;

    public NgoProfileEntity() {
        super();
        this.createdOn = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CustomerEntity getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerEntity customer) {
        this.customer = customer;
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

    public LocalDateTime getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(LocalDateTime createdOn) {
        this.createdOn = createdOn;
    }

    @Override
    public String toString() {
        return "NgoProfileEntity{" +
                "id=" + id +
                ", customer=" + customer +
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

