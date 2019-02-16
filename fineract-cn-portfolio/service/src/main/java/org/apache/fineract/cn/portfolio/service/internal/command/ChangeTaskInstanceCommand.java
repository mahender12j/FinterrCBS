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
package org.apache.fineract.cn.portfolio.service.internal.command;

import org.apache.fineract.cn.portfolio.api.v1.domain.TaskInstance;

/**
 * @author Myrle Krantz
 */
public class ChangeTaskInstanceCommand {
  private final String productIdentifier;
  private final String caseIdentifier;
  private final TaskInstance instance;

  public ChangeTaskInstanceCommand(String productIdentifier, String caseIdentifier, TaskInstance instance) {
    this.productIdentifier = productIdentifier;
    this.caseIdentifier = caseIdentifier;
    this.instance = instance;
  }

  public String getProductIdentifier() {
    return productIdentifier;
  }

  public String getCaseIdentifier() {
    return caseIdentifier;
  }

  public TaskInstance getInstance() {
    return instance;
  }

  @Override
  public String toString() {
    return "ChangeTaskInstanceCommand{" +
        "productIdentifier='" + productIdentifier + '\'' +
        ", caseIdentifier='" + caseIdentifier + '\'' +
        ", taskIdentifier=" + instance.getTaskIdentifier() +
        '}';
  }
}
