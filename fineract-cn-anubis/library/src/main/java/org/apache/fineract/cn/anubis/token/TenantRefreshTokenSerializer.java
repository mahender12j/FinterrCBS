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
package org.apache.fineract.cn.anubis.token;

import io.jsonwebtoken.*;
import org.apache.fineract.cn.anubis.api.v1.TokenConstants;
import org.apache.fineract.cn.anubis.provider.InvalidKeyTimestampException;
import org.apache.fineract.cn.anubis.security.AmitAuthenticationException;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.security.Key;
import java.security.PrivateKey;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * @author Myrle Krantz
 */
@SuppressWarnings("WeakerAccess")
@Component
public class TenantRefreshTokenSerializer {
  @SuppressWarnings("WeakerAccess")
  public static class Specification {
    private String keyTimestamp;
    private PrivateKey privateKey;
    private String user;
    private long secondsToLive;
    private String sourceApplication;
    private String endpointSet = null; //Optional

    public Specification setKeyTimestamp(final String keyTimestamp) {
      this.keyTimestamp = keyTimestamp;
      return this;
    }

    public Specification setPrivateKey(final PrivateKey privateKey) {
      this.privateKey = privateKey;
      return this;
    }

    public Specification setUser(final String user) {
      this.user = user;
      return this;
    }

    public Specification setSecondsToLive(final long secondsToLive) {
      this.secondsToLive = secondsToLive;
      return this;
    }

    public Specification setSourceApplication(final String sourceApplication) {
      this.sourceApplication = sourceApplication;
      return this;
    }

    public Specification setEndpointSet(String endpointSet) {
      this.endpointSet = endpointSet;
      return this;
    }
  }

  public TokenSerializationResult build(final Specification specification)
  {
    final long issued = System.currentTimeMillis();

    if (specification.keyTimestamp == null) {
      throw new IllegalArgumentException("token signature timestamp must not be null.");
    }
    if (specification.privateKey == null) {
      throw new IllegalArgumentException("token signature privateKey must not be null.");
    }
    if (specification.sourceApplication == null) {
      throw new IllegalArgumentException("token source application must not be null.");
    }
    if (specification.secondsToLive <= 0) {
      throw new IllegalArgumentException("token secondsToLive must be positive.");
    }

    final Date expiration = new Date(issued + TimeUnit.SECONDS.toMillis(specification.secondsToLive));

    final JwtBuilder jwtBuilder =
            Jwts.builder()
                    .setIssuer(specification.sourceApplication)
                    .setSubject(specification.user)
                    .claim(TokenConstants.JWT_SIGNATURE_TIMESTAMP_CLAIM, specification.keyTimestamp)
                    .setIssuedAt(new Date(issued))
                    .signWith(SignatureAlgorithm.RS512, specification.privateKey)
                    .setExpiration(expiration);
    if (specification.endpointSet != null)
      jwtBuilder.claim(TokenConstants.JWT_ENDPOINT_SET_CLAIM, specification.endpointSet);

    return new TokenSerializationResult(TokenConstants.PREFIX + jwtBuilder.compact(), expiration);
  }

  public TokenDeserializationResult deserialize(final TenantApplicationRsaKeyProvider tenantRsaKeyProvider, final String refreshToken)
  {
    final Optional<String> tokenString = getJwtTokenString(refreshToken);

    final String token = tokenString.orElseThrow(AmitAuthenticationException::invalidToken);


    try {
      final JwtParser parser = Jwts.parser().setSigningKeyResolver(new SigningKeyResolver() {
        @Override public Key resolveSigningKey(final JwsHeader header, final Claims claims) {
          final String keyTimestamp = getKeyTimestampFromClaims(claims);
          final String issuingApplication = getIssuingApplicationFromClaims(claims);

          try {
            return tenantRsaKeyProvider.getApplicationPublicKey(issuingApplication, keyTimestamp);
          }
          catch (final IllegalArgumentException e)
          {
            throw AmitAuthenticationException.missingTenant();
          }
          catch (final InvalidKeyTimestampException e)
          {
            throw AmitAuthenticationException.invalidTokenKeyTimestamp(TokenType.TENANT.getIssuer(), keyTimestamp);
          }
        }

        @Override public Key resolveSigningKey(final JwsHeader header, final String plaintext) {
          return null;
        }
      });

      @SuppressWarnings("unchecked") Jwt<Header, Claims> jwt = parser.parse(token);

      return new TokenDeserializationResult(
              jwt.getBody().getSubject(),
              jwt.getBody().getExpiration(),
              jwt.getBody().getIssuer(),
              jwt.getBody().get(TokenConstants.JWT_ENDPOINT_SET_CLAIM, String.class));
    }
    catch (final JwtException e) {
      throw AmitAuthenticationException.invalidToken();
    }
  }

  private static Optional<String> getJwtTokenString(final String refreshToken) {
    if ((refreshToken == null) || refreshToken.equals(
            TokenConstants.NO_AUTHENTICATION)){
      return Optional.empty();
    }

    if (!refreshToken.startsWith(TokenConstants.PREFIX)) {
      throw AmitAuthenticationException.invalidToken();
    }
    return Optional.of(refreshToken.substring(TokenConstants.PREFIX.length()).trim());
  }

  private @Nonnull
  String getKeyTimestampFromClaims(final Claims claims) {
    return claims.get(TokenConstants.JWT_SIGNATURE_TIMESTAMP_CLAIM, String.class);
  }

  private @Nonnull
  String getIssuingApplicationFromClaims(final Claims claims) {
    return claims.getIssuer();
  }
}
