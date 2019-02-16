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
package org.apache.fineract.cn.rhythm.service.internal.command;

/**
 * @author Myrle Krantz
 */
public class DeleteBeatCommand {

  private final String tenantIdentifier;

  private final String applicationIdentifier;

  private final String identifier;

  public DeleteBeatCommand(final String tenantIdentifier, final String applicationIdentifier, final String identifier) {
    super();
    this.tenantIdentifier = tenantIdentifier;
    this.applicationIdentifier = applicationIdentifier;
    this.identifier = identifier;
  }

  public String getTenantIdentifier() {
    return tenantIdentifier;
  }

  public String getApplicationIdentifier() {
    return this.applicationIdentifier;
  }

  public String getIdentifier() {
    return identifier;
  }

  @Override
  public String toString() {
    return "DeleteBeatCommand{" +
            "tenantIdentifier='" + tenantIdentifier + '\'' +
            ", applicationIdentifier='" + applicationIdentifier + '\'' +
            ", identifier='" + identifier + '\'' +
            '}';
  }
}
