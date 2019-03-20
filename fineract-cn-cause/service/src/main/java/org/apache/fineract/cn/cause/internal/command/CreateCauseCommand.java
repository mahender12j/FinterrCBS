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
package org.apache.fineract.cn.cause.internal.command;

import org.apache.fineract.cn.cause.api.v1.domain.Cause;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public class CreateCauseCommand {

    private final Cause cause;
    private final MultipartFile feature;
    private final List<MultipartFile> gallery;
    private final MultipartFile tax;
    private final MultipartFile terms;
    private final MultipartFile other;

    public CreateCauseCommand(final Cause cause,
                              final MultipartFile feature,
                              final List<MultipartFile> gallery,
                              final MultipartFile tax,
                              final MultipartFile terms,
                              final MultipartFile other) {
        super();
        this.cause = cause;
        this.feature = feature;
        this.gallery = gallery;
        this.tax = tax;
        this.terms = terms;
        this.other = other;
    }

    public Cause getCause() {
        return cause;
    }

    public MultipartFile getFeature() {
        return feature;
    }

    public List<MultipartFile> getGallery() {
        return gallery;
    }

    public MultipartFile getTax() {
        return tax;
    }

    public MultipartFile getTerms() {
        return terms;
    }

    public MultipartFile getOther() {
        return other;
    }
}
