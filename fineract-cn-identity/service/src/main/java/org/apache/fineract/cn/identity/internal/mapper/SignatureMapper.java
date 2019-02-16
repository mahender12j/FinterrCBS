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
package org.apache.fineract.cn.identity.internal.mapper;

import org.apache.fineract.cn.anubis.api.v1.domain.ApplicationSignatureSet;
import org.apache.fineract.cn.anubis.api.v1.domain.Signature;
import org.apache.fineract.cn.identity.internal.repository.ApplicationSignatureEntity;
import org.apache.fineract.cn.identity.internal.repository.SignatureEntity;

/**
 * @author Myrle Krantz
 */
public interface SignatureMapper {
  static ApplicationSignatureSet mapToApplicationSignatureSet(final SignatureEntity signatureEntity) {
    return new ApplicationSignatureSet(
            signatureEntity.getKeyTimestamp(),
            new Signature(signatureEntity.getPublicKeyMod(), signatureEntity.getPublicKeyExp()),
            new Signature(signatureEntity.getPublicKeyMod(), signatureEntity.getPublicKeyExp()));
  }

  static Signature mapToSignature(final ApplicationSignatureEntity entity) {
    final Signature ret = new Signature();
    ret.setPublicKeyExp(entity.getPublicKeyExp());
    ret.setPublicKeyMod(entity.getPublicKeyMod());
    return ret;
  }
}
