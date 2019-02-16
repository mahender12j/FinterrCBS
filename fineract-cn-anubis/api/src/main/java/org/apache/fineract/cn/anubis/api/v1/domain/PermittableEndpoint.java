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
package org.apache.fineract.cn.anubis.api.v1.domain;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.ScriptAssert;

import javax.annotation.Nullable;
import java.util.Objects;

@SuppressWarnings({"WeakerAccess", "unused"})
@ScriptAssert(lang = "javascript", script = "_this.method === \"GET\" || _this.method === \"POST\" || _this.method === \"HEAD\" || _this.method === \"POST\" || _this.method === \"PUT\" || _this.method === \"DELETE\"" )
public class PermittableEndpoint {

  @NotBlank
  private String path;

  @NotBlank
  @Length(min = 3)
  private String method;

  @Nullable
  private String groupId;

  private boolean acceptTokenIntendedForForeignApplication;

  public PermittableEndpoint() {
    super();
  }

  public PermittableEndpoint(final String path, final String method) {
    this.path = path;
    this.method = method;
    this.groupId = "";
  }

  public PermittableEndpoint(final String path, final String method, final String groupId) {
    this.path = path;
    this.method = method;
    this.groupId = groupId;
  }

  public PermittableEndpoint(String path, String method, String groupId, boolean acceptTokenIntendedForForeignApplication) {
    this.path = path;
    this.method = method;
    this.groupId = groupId;
    this.acceptTokenIntendedForForeignApplication = acceptTokenIntendedForForeignApplication;
  }

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public String getMethod() {
    return method;
  }

  public void setMethod(String method) {
    this.method = method;
  }

  public String getGroupId() {
    return groupId;
  }

  public void setGroupId(String groupId) {
    this.groupId = groupId;
  }

  public boolean isAcceptTokenIntendedForForeignApplication() {
    return acceptTokenIntendedForForeignApplication;
  }

  public void setAcceptTokenIntendedForForeignApplication(boolean acceptTokenIntendedForForeignApplication) {
    this.acceptTokenIntendedForForeignApplication = acceptTokenIntendedForForeignApplication;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    PermittableEndpoint that = (PermittableEndpoint) o;
    return acceptTokenIntendedForForeignApplication == that.acceptTokenIntendedForForeignApplication &&
            Objects.equals(path, that.path) &&
            Objects.equals(method, that.method) &&
            Objects.equals(groupId, that.groupId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(path, method, groupId, acceptTokenIntendedForForeignApplication);
  }

  @Override
  public String toString() {
    return "PermittableEndpoint{" +
            "path='" + path + '\'' +
            ", method='" + method + '\'' +
            ", groupId='" + groupId + '\'' +
            ", acceptTokenIntendedForForeignApplication=" + acceptTokenIntendedForForeignApplication +
            '}';
  }
}
