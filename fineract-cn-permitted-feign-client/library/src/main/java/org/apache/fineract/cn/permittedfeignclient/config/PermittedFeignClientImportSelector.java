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
package org.apache.fineract.cn.permittedfeignclient.config;

import org.apache.fineract.cn.permittedfeignclient.controller.ApplicationPermissionRequirementsRestController;
import org.apache.fineract.cn.permittedfeignclient.service.ApplicationAccessTokenService;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Myrle Krantz
 */
class PermittedFeignClientImportSelector implements ImportSelector {
  PermittedFeignClientImportSelector() { }

  @Override public String[] selectImports(AnnotationMetadata importingClassMetadata) {
    final Set<Class> classesToImport = new HashSet<>();
    classesToImport.add(ApplicationPermissionRequirementsRestController.class);
    classesToImport.add(ApplicationAccessTokenService.class);

    return classesToImport.stream().map(Class::getCanonicalName).toArray(String[]::new);
  }
}
