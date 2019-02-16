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
package org.apache.fineract.cn.customer.catalog.internal.mapper;

import org.apache.fineract.cn.customer.catalog.api.v1.domain.Catalog;
import org.apache.fineract.cn.customer.catalog.internal.repository.CatalogEntity;
import java.time.Clock;
import java.time.LocalDateTime;
import org.apache.fineract.cn.api.util.UserContextHolder;
import org.apache.fineract.cn.lang.DateConverter;

public class CatalogMapper {

  private CatalogMapper() {
    super();
  }

  public static CatalogEntity map(final Catalog catalog) {
    final CatalogEntity catalogEntity = new CatalogEntity();
    catalogEntity.setIdentifier(catalog.getIdentifier());
    catalogEntity.setName(catalog.getName());
    catalogEntity.setDescription(catalog.getDescription());
    catalogEntity.setCreatedBy(UserContextHolder.checkedGetUser());
    catalogEntity.setCreatedOn(LocalDateTime.now(Clock.systemUTC()));
    return catalogEntity;
  }

  public static Catalog map(final CatalogEntity catalogEntity) {
    final Catalog catalog = new Catalog();
    catalog.setIdentifier(catalogEntity.getIdentifier());
    catalog.setName(catalogEntity.getName());
    catalog.setDescription(catalogEntity.getDescription());
    catalog.setCreatedBy(catalogEntity.getCreatedBy());
    catalog.setCreatedOn(DateConverter.toIsoString(catalogEntity.getCreatedOn()));
    if (catalogEntity.getLastModifiedBy() != null) {
      catalog.setLastModifiedBy(catalogEntity.getLastModifiedBy());
      catalog.setLastModifiedOn(DateConverter.toIsoString(catalogEntity.getLastModifiedOn()));
    }
    return catalog;
  }
}
