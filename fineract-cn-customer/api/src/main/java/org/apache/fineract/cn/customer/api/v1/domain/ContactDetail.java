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

import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public final class ContactDetail {

    public enum Type {
        EMAIL, MOBILE
    }

    public enum Group {
        PERSON, BUSINESS, CADMIN, SADMIN, CORPORATE, TRUST, FUNDMANAGER, WADMIN, GUEST
    }

    @NotNull
    private Type type;
    @NotNull
    private Group group;
    @NotBlank
    private String value;
    @Min(1)
    @Max(127)
    private Integer preferenceLevel;

    private Boolean validated;

    public ContactDetail() {
        super();
        this.validated = false;
    }

    public String getType() {
        return this.type.name();
    }

    public void setType(final String type) {
        this.type = Type.valueOf(type.toUpperCase());
    }

    public String getValue() {
        return this.value;
    }

    public String getGroup() {
        return this.group.name();
    }

    public void setGroup(final String group) {
        this.group = Group.valueOf(group);
    }

    public void setValue(final String value) {
        this.value = value;
    }

    public Boolean getValidated() {
        return this.validated;
    }

    public void setValidated(final Boolean validated) {
        this.validated = validated;
    }

    public Integer getPreferenceLevel() {
        return this.preferenceLevel;
    }

    public void setPreferenceLevel(final Integer preferenceLevel) {
        this.preferenceLevel = preferenceLevel;
    }

    @Override
    public String toString() {
        return "ContactDetail{" + "type=" + type + ", group=" + group + ", value='" + value + '\''
                + ", preferenceLevel=" + preferenceLevel + ", validated=" + validated + '}';
    }
}
