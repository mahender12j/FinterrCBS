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
package org.apache.fineract.cn.permittedfeignclient.service;

import org.apache.fineract.cn.permittedfeignclient.annotation.EndpointSet;
import org.apache.finearct.cn.permittedfeignclient.api.v1.domain.ApplicationPermission;
import org.apache.fineract.cn.anubis.annotation.Permittable;
import org.apache.fineract.cn.anubis.annotation.Permittables;
import org.apache.fineract.cn.anubis.api.v1.domain.AllowedOperation;
import org.apache.fineract.cn.identity.api.v1.domain.Permission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Myrle Krantz
 */
@Component
public class ApplicationPermissionRequirementsService{
  private final Class[] classes;

  @Autowired
  public ApplicationPermissionRequirementsService(final Class[] classes) {
    this.classes = classes;
  }

  public List<ApplicationPermission> getRequiredPermissions() {
    return Stream.of(classes)
            .flatMap(ApplicationPermissionRequirementsService::getApplicationPermissionsFromInterface)
            .collect(Collectors.toList());
  }

  static Stream<ApplicationPermission> getApplicationPermissionsFromInterface(
          final Class permissionRequestedFeignInterface) {
    final Map<String, List<Permission>> grouped = getPermissionsFromInterface(permissionRequestedFeignInterface)
            .collect(Collectors.groupingBy(Permission::getPermittableEndpointGroupIdentifier));
    return grouped.entrySet().stream().map(x -> getApplicationPermissionfromEntry(permissionRequestedFeignInterface, x));
  }

  private static <T>  ApplicationPermission getApplicationPermissionfromEntry(final Class<T> permissionRequestedFeignInterface,
                                                                              final Map.Entry<String, List<Permission>> entry) {
    final Optional<EndpointSet> permissionRequiredForAnnotation
            = Optional.ofNullable(permissionRequestedFeignInterface.getAnnotation(EndpointSet.class));

    final String permittableGroupId = permissionRequiredForAnnotation.map(EndpointSet::identifier)
            .orElse(null);
    final Permission permission = getPermissionfromEntry(entry);
    return new ApplicationPermission(permittableGroupId, permission);
  }

  private static Permission getPermissionfromEntry(final Map.Entry<String, List<Permission>> entry) {
    final Set<AllowedOperation> allowedOperations = entry.getValue().stream().flatMap(x -> x.getAllowedOperations().stream()).collect(Collectors.toSet());
    return new Permission(entry.getKey(), allowedOperations);
  }

  private static Stream<Permission> getPermissionsFromInterface(final Class permissionRequestedFeignInterface) {
    final Method[] methods = permissionRequestedFeignInterface.getMethods();
    return Stream.of(methods)
            .filter((method) -> method.isAnnotationPresent(Permittables.class) || method.isAnnotationPresent(Permittable.class))
            .flatMap(ApplicationPermissionRequirementsService::extractPermissionsFromMethod);
  }

  static private Stream<Permission> extractPermissionsFromMethod(final Method method) {
    final Permittables permittablesAnnotation = method.getAnnotation(Permittables.class);
    final Permittable[] permittables;
    if (permittablesAnnotation != null)
      permittables = permittablesAnnotation.value();
    else {
      final Permittable permittableAnnotation = method.getAnnotation(Permittable.class);
      permittables = new Permittable[]{permittableAnnotation};
    }
    return Stream.of(permittables)
            .map(x -> mapPermittableToPermission(x, method));
  }

  static private Permission mapPermittableToPermission(final Permittable permittable,
                                                                             final Method method) {
    final RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
    final RequestMethod[] httpMethods = requestMapping.method();

    return new Permission(permittable.groupId(), mapMethodsToAllowedOperations(httpMethods));
  }

  static private Set<AllowedOperation> mapMethodsToAllowedOperations(final RequestMethod[] httpMethods) {
    return Stream.of(httpMethods)
            .map(ApplicationPermissionRequirementsService::mapRequestMethodToAllowedOperation)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(Collectors.toSet());
  }

  static private Optional<AllowedOperation> mapRequestMethodToAllowedOperation(final RequestMethod requestMethod) {
    switch (requestMethod) {
      case GET:
        return Optional.of(AllowedOperation.READ);
      case HEAD:
        return Optional.of(AllowedOperation.READ);
      case POST:
        return Optional.of(AllowedOperation.CHANGE);
      case PUT:
        return Optional.of(AllowedOperation.CHANGE);
      case PATCH:
        return Optional.of(AllowedOperation.CHANGE);
      case DELETE:
        return Optional.of(AllowedOperation.DELETE);
      default:
      case OPTIONS:
      case TRACE:
        return Optional.empty();
    }
  }
}