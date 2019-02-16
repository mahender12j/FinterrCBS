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
package org.apache.fineract.cn.portfolio.api.v1.domain;

import java.util.List;
import java.util.Objects;

/**
 * @author Myrle Krantz
 */
public class CasePage {
  private List<Case> elements;
  private Integer totalPages;
  private Long totalElements;

  public CasePage(List<Case> elements, Integer totalPages, Long totalElements) {
    this.elements = elements;
    this.totalPages = totalPages;
    this.totalElements = totalElements;
  }

  public List<Case> getElements() {
    return elements;
  }

  public void setElements(List<Case> elements) {
    this.elements = elements;
  }

  public Integer getTotalPages() {
    return totalPages;
  }

  public void setTotalPages(Integer totalPages) {
    this.totalPages = totalPages;
  }

  public Long getTotalElements() {
    return totalElements;
  }

  public void setTotalElements(Long totalElements) {
    this.totalElements = totalElements;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    CasePage casePage = (CasePage) o;
    return Objects.equals(elements, casePage.elements) &&
            Objects.equals(totalPages, casePage.totalPages) &&
            Objects.equals(totalElements, casePage.totalElements);
  }

  @Override
  public int hashCode() {
    return Objects.hash(elements, totalPages, totalElements);
  }

  @Override
  public String toString() {
    return "CasePage{" +
            "elements=" + elements +
            ", totalPages=" + totalPages +
            ", totalElements=" + totalElements +
            '}';
  }
}
