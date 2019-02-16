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
package org.apache.fineract.cn.identity.internal.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import org.apache.fineract.cn.anubis.api.v1.domain.Signature;
import org.apache.fineract.cn.identity.api.v1.domain.CallEndpointSet;
import org.apache.fineract.cn.identity.api.v1.domain.Permission;
import org.apache.fineract.cn.identity.internal.mapper.ApplicationCallEndpointSetMapper;
import org.apache.fineract.cn.identity.internal.mapper.PermissionMapper;
import org.apache.fineract.cn.identity.internal.mapper.SignatureMapper;
import org.apache.fineract.cn.identity.internal.repository.ApplicationCallEndpointSets;
import org.apache.fineract.cn.identity.internal.repository.ApplicationPermissionUsers;
import org.apache.fineract.cn.identity.internal.repository.ApplicationPermissions;
import org.apache.fineract.cn.identity.internal.repository.ApplicationSignatureEntity;
import org.apache.fineract.cn.identity.internal.repository.ApplicationSignatures;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Myrle Krantz
 */
@Service
public class ApplicationService {

  private final ApplicationSignatures applicationSignaturesRepository;
  private final ApplicationPermissions applicationPermissionsRepository;
  private final ApplicationPermissionUsers applicationPermissionsUserRepository;
  private final ApplicationCallEndpointSets applicationCallEndpointSets;

  @Autowired
  public ApplicationService(final ApplicationSignatures applicationSignaturesRepository,
                            final ApplicationPermissions applicationPermissionsRepository,
                            final ApplicationPermissionUsers applicationPermissionsUserRepository,
                            final ApplicationCallEndpointSets applicationCallEndpointSets) {
    this.applicationSignaturesRepository = applicationSignaturesRepository;
    this.applicationPermissionsRepository = applicationPermissionsRepository;
    this.applicationPermissionsUserRepository = applicationPermissionsUserRepository;
    this.applicationCallEndpointSets = applicationCallEndpointSets;
  }

  public List<String> getAllApplications() {
    return applicationSignaturesRepository.getAll().stream()
            .map(ApplicationSignatureEntity::getApplicationIdentifier)
            .collect(Collectors.toList());
  }

  public List<Permission> getAllPermissionsForApplication(final String applicationIdentifier) {
    return applicationPermissionsRepository.getAllPermissionsForApplication(applicationIdentifier).stream()
            .map(PermissionMapper::mapToPermission)
            .collect(Collectors.toList());
  }

  public Optional<Permission> getPermissionForApplication(
          final String applicationIdentifier,
          final String permittableEndpointGroupIdentifier) {
    return applicationPermissionsRepository.getPermissionForApplication(applicationIdentifier, permittableEndpointGroupIdentifier)
            .map(PermissionMapper::mapToPermission);
  }

  public Optional<Signature> getSignatureForApplication(final String applicationIdentifier, final String timestamp) {
    return applicationSignaturesRepository.get(applicationIdentifier, timestamp)
            .map(SignatureMapper::mapToSignature);
  }

  public boolean applicationExists(final String applicationIdentifier) {
    return applicationSignaturesRepository.signaturesExistForApplication(applicationIdentifier);
  }

  public boolean applicationPermissionExists(final @Nonnull String applicationIdentifier,
                                             final @Nonnull String permittableGroupIdentifier) {
    return applicationPermissionsRepository.exists(applicationIdentifier, permittableGroupIdentifier);
  }

  public boolean applicationPermissionEnabledForUser(final String applicationIdentifier,
                                                     final String permittableEndpointGroupIdentifier,
                                                     final String userIdentifier) {
    return applicationPermissionsUserRepository.enabled(applicationIdentifier, permittableEndpointGroupIdentifier, userIdentifier);
  }

  public List<CallEndpointSet> getAllCallEndpointSetsForApplication(final String applicationIdentifier) {
    return applicationCallEndpointSets.getAllForApplication(applicationIdentifier).stream()
            .map(ApplicationCallEndpointSetMapper::map)
            .collect(Collectors.toList());
  }

  public Optional<CallEndpointSet> getCallEndpointSetForApplication(
          final String applicationIdentifier,
          final String callEndpointSetIdentifier) {
    return applicationCallEndpointSets.get(applicationIdentifier, callEndpointSetIdentifier)
            .map(ApplicationCallEndpointSetMapper::map);
  }

  public boolean applicationCallEndpointSetExists(String applicationIdentifier, String callEndpointSetIdentifier) {
    return applicationCallEndpointSets.get(applicationIdentifier, callEndpointSetIdentifier).isPresent();
  }
}
