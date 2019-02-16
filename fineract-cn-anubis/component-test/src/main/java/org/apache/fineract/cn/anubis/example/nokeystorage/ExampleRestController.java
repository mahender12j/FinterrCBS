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
package org.apache.fineract.cn.anubis.example.nokeystorage;

import org.apache.fineract.cn.anubis.annotation.AcceptedTokenType;
import org.apache.fineract.cn.anubis.annotation.Permittable;
import org.apache.fineract.cn.anubis.api.v1.domain.ApplicationSignatureSet;
import org.apache.fineract.cn.anubis.api.v1.domain.Signature;
import org.apache.fineract.cn.lang.security.RsaKeyPairFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Myrle Krantz
 */
@SuppressWarnings("unused")
@RestController
@RequestMapping()
public class ExampleRestController {
  private boolean initialized = false;
  private final SpecialTenantSignatureRepository specialTenantSignatureRepository;

  @Autowired
  public ExampleRestController(final SpecialTenantSignatureRepository specialTenantSignatureRepository) {
    this.specialTenantSignatureRepository = specialTenantSignatureRepository;
  }

  @RequestMapping(value = "/initialize", method = RequestMethod.POST)
  @Permittable(AcceptedTokenType.SYSTEM)
  public ResponseEntity<Void> initialize()
  {
    final RsaKeyPairFactory.KeyPairHolder applicationKeyPair = RsaKeyPairFactory.createKeyPair();
    final RsaKeyPairFactory.KeyPairHolder identityManagerKeyPair = RsaKeyPairFactory.createKeyPair();
    final Signature applicationSignature = new Signature(applicationKeyPair.getPublicKeyMod(), applicationKeyPair.getPublicKeyExp());
    final Signature identityManagerSignature = new Signature(identityManagerKeyPair.getPublicKeyMod(), identityManagerKeyPair.getPublicKeyExp());

    final ApplicationSignatureSet applicationSignatureSet = new ApplicationSignatureSet(identityManagerKeyPair.getTimestamp(), applicationSignature, identityManagerSignature);

    this.specialTenantSignatureRepository.addSignatureSet(applicationSignatureSet, applicationKeyPair);
    initialized = true;
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @RequestMapping(value = "/initialize", method = RequestMethod.GET)
  @Permittable(AcceptedTokenType.GUEST)
  public ResponseEntity<Boolean> isInitialized()
  {
    return new ResponseEntity<>(initialized, HttpStatus.OK);
  }

  @RequestMapping(value = "/initialize", method = RequestMethod.DELETE)
  @Permittable(AcceptedTokenType.GUEST)
  public ResponseEntity<Void> uninitialize()
  {
    initialized = false;
    return new ResponseEntity<>(HttpStatus.OK);
  }
}