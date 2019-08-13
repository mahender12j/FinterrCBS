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
package org.apache.fineract.cn.cause.api.v1.domain;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author Md Robiul Hassan
 */
public class CauseRating {
    private Long id;
    @Max(value = 5, message = "rating range must be between 0-5")
    @Min(value = -1, message = "rating range must be between 0-5")
    @NotNull
    private int rating;
    private String comment;
    @NotNull
    private Long ref; //ref is -1 when root and child will have 1-infinity, 0 is when child has no ref
    private boolean active = true;
    private String createdBy;
    private String createdByUrl;
    private String createdOn;
    private String updatedOn;
    private List<CauseRating> causeRatings;

    public CauseRating() {
    }

    public CauseRating(Long id, int rating, String comment, Long ref, boolean active, String createdBy, String createdOn) {
        this.id = id;
        this.rating = rating;
        this.comment = comment;
        this.ref = ref;
        this.active = active;
        this.createdBy = createdBy;
        this.createdOn = createdOn;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Long getRef() {
        return ref;
    }

    public void setRef(Long ref) {
        this.ref = ref;
    }

    public List<CauseRating> getCauseRatings() {
        return causeRatings;
    }

    public void setCauseRatings(List<CauseRating> causeRatings) {
        this.causeRatings = causeRatings;
    }

    public String getCreatedByUrl() {
        return createdByUrl;
    }

    public void setCreatedByUrl(String createdByUrl) {
        this.createdByUrl = createdByUrl;
    }

    public String getUpdatedOn() {
        return updatedOn;
    }

    public void setUpdatedOn(String updatedOn) {
        this.updatedOn = updatedOn;
    }

    @Override
    public String toString() {
        return "CauseRating{" +
                "id=" + id +
                ", rating=" + rating +
                ", comment='" + comment + '\'' +
                ", ref=" + ref +
                ", active=" + active +
                ", createdBy='" + createdBy + '\'' +
                ", createdByUrl='" + createdByUrl + '\'' +
                ", createdOn='" + createdOn + '\'' +
                ", causeRatings=" + causeRatings +
                '}';
    }
}

