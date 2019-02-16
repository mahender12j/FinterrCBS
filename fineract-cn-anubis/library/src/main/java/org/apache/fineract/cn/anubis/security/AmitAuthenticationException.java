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
package org.apache.fineract.cn.anubis.security;

import org.springframework.security.core.AuthenticationException;

/**
 * @author Markus Geiß, Myrle Krantz
 */
@SuppressWarnings("WeakerAccess")
public class AmitAuthenticationException extends AuthenticationException {

  private AmitAuthenticationException(final String message) { super(message); }

  public static AmitAuthenticationException internalError(final String message) {
    return new AmitAuthenticationException(message);
  }

  public static AmitAuthenticationException invalidTokenAlgorithm(final String algorithm) {
    return new AmitAuthenticationException("Unsupported algorithm: " + algorithm);
  }

  public static AmitAuthenticationException invalidTokenIssuer(final String issuer) {
    return new AmitAuthenticationException("Unsupported token issuer: " + issuer);
  }

  public static AmitAuthenticationException invalidToken() {
    return new AmitAuthenticationException("Token is invalid.");
  }

  public static AmitAuthenticationException invalidHeader() {
    return new AmitAuthenticationException("Invalid authorization header.");
  }

  public static AmitAuthenticationException invalidTokenKeyTimestamp(final String issuer, final String version) {
    return new AmitAuthenticationException(
        "Token version " + version + " not accepted for issuer " + issuer + ".");
  }

  public static AmitAuthenticationException missingTenant() {
    return new AmitAuthenticationException("Tenant not set.");
  }

  public static AmitAuthenticationException missingTokenContent() {
    return new AmitAuthenticationException("Token does not contain content.  Perhaps you submitted a refresh token instead of the access token?");
  }

  @SuppressWarnings("unused") //used in identity
  public static AmitAuthenticationException userPasswordCombinationNotFound() {
    return new AmitAuthenticationException("A user with the given useridentifier and password doesn't exist.");
  }

  @SuppressWarnings("unused") //used in identity
  public static AmitAuthenticationException passwordExpired() {
    return new AmitAuthenticationException("Users password has expired.");
  }

  @SuppressWarnings("unused") //used in identity
  public static AmitAuthenticationException applicationMissingPermissions(final String user, final String application) {
    return new AmitAuthenticationException("User '" + user + "' has not given application '"
            + application + "' permission to perform all the requested actions in their name.");
  }
}
