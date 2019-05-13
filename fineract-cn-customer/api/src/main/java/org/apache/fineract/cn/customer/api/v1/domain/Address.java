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

public final class Address {

  private String street;
  private String state;
  private String city;
  private String region;
  private String postalCode;
  @NotBlank
  private String countryCode;
  @NotBlank
  private String country;
  private String nationalityCode;
  private String nationality;

  public Address() {
    super();
  }

  public String getStreet() {
    return this.street;
  }

  public void setStreet(final String street) {
    this.street = street;
  }

  public String getCity() {
    return this.city;
  }

  public void setCity(final String city) {
    this.city = city;
  }

  public String getRegion() {
    return this.region;
  }

  public void setRegion(final String region) {
    this.region = region;
  }

  public String getPostalCode() {
    return this.postalCode;
  }

  public void setPostalCode(final String postalCode) {
    this.postalCode = postalCode;
  }

  public String getCountryCode() {
    return this.countryCode;
  }

  public void setCountryCode(final String countryCode) {
    this.countryCode = countryCode;
  }

  public String getCountry() {
    return this.country;
  }

  public void setCountry(final String country) {
    this.country = country;
  }

  public String getNationalityCode() {
    return this.nationalityCode;
  }

  public void setNationalityCode(final String nationalityCode) {
    this.nationalityCode = nationalityCode;
  }

  public String getNationality() {
    return this.nationality;
  }

  public void setNationality(final String nationality) {
    this.nationality = nationality;
  }

  public String getState() {
    return state;
  }

  public void setState(String state) {
    this.state = state;
  }

  @Override
	public String toString() {
		return "Address [street=" + street + ", city=" + city + ", region=" + region + ", postalCode=" + postalCode
				+ ", countryCode=" + countryCode + ", country=" + country + ", nationalityCode=" + nationalityCode
				+ ", nationality=" + nationality + ", toString()=" + super.toString() + "]";
	}
}

