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

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;

/**
 * @author Md Robiul Hassan
 */
public class CauseComment {

    private Long id;
    @NotNull
    private String comment;
    private boolean active;
    private Long ref;
    private String createdBy;
    private String createdOn;
    private String type;
    //    ignore to post
    private List<CauseComment> childComments;

    public CauseComment() {
    }

    public CauseComment(Long id, String comment, boolean active, Long ref, String createdBy, String createdOn) {
        this.id = id;
        this.comment = comment;
        this.active = active;
        this.ref = ref;
        this.createdBy = createdBy;
        this.createdOn = createdOn;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
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

    public Long getRef() {
        return ref;
    }

    public void setRef(Long ref) {
        this.ref = ref;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<CauseComment> getChildComments() {
        return childComments;
    }

    public void setChildComments(List<CauseComment> childComments) {
        this.childComments = childComments;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CauseComment that = (CauseComment) o;
        return active == that.active &&
                Objects.equals(comment, that.comment);
    }

    @Override
    public int hashCode() {
        return Objects.hash(comment, active);
    }

    @Override
    public String toString() {
        return "CauseRating{" +
                ", comment='" + comment + '\'' +
                ", active=" + active +
                ", createdBy='" + createdBy + '\'' +
                ", createdOn='" + createdOn + '\'' +
                '}';
    }
}

