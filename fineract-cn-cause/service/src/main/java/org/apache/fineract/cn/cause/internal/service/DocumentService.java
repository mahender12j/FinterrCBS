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
package org.apache.fineract.cn.cause.internal.service;

import org.apache.fineract.cn.cause.api.v1.domain.CauseDocument;
import org.apache.fineract.cn.cause.internal.mapper.DocumentMapper;
import org.apache.fineract.cn.cause.internal.repository.DocumentEntity;
import org.apache.fineract.cn.cause.internal.repository.DocumentPageEntity;
import org.apache.fineract.cn.cause.internal.repository.DocumentPageRepository;
import org.apache.fineract.cn.cause.internal.repository.DocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Myrle Krantz
 */
@Service
public class DocumentService {
    private final DocumentRepository documentRepository;
    private final DocumentPageRepository documentPageRepository;

    @Autowired
    public DocumentService(
            final DocumentRepository documentRepository,
            final DocumentPageRepository documentPageRepository) {
        this.documentRepository = documentRepository;
        this.documentPageRepository = documentPageRepository;
    }

    public Optional<DocumentPageEntity> findPage(
            final String causeIdentifier,
            final String documentIdentifier,
            final Integer pageNumber) {
        return this.documentPageRepository.findByCauseIdAndDocumentIdentifierAndPageNumber(
                causeIdentifier,
                documentIdentifier,
                pageNumber);
    }

    public Stream<CauseDocument> find(final String causeIdentifier) {
        final Stream<DocumentEntity> preMappedRet = this.documentRepository.findByCauseId(causeIdentifier);
        return preMappedRet.map(DocumentMapper::map);
    }

    public Optional<CauseDocument> findDocument(
            final String causeIdentifier,
            final String documentIdentifier) {
        return this.documentRepository.findByCauseIdAndDocumentIdentifier(causeIdentifier, documentIdentifier)
                .map(DocumentMapper::map);
    }

    public boolean documentExists(
            final String causeIdentifier,
            final String documentIdentifier) {
        return findDocument(causeIdentifier, documentIdentifier).isPresent();
    }

    public Stream<Integer> findPageNumbers(
            final String causeIdentifier,
            final String documentIdentifier) {
        return documentPageRepository.findByCauseIdAndDocumentIdentifier(causeIdentifier, documentIdentifier)
                .map(DocumentPageEntity::getPageNumber);
    }

    public boolean isDocumentCompleted(
            final String causeIdentifier,
            final String documentIdentifier) {
        return documentRepository.findByCauseIdAndDocumentIdentifier(causeIdentifier, documentIdentifier)
                .map(DocumentEntity::getCompleted).orElse(true);
    }

    public boolean isDocumentMissingPages(
            final String causeIdentifier,
            final String documentIdentifier) {
        final List<Integer> pageNumbers = findPageNumbers(causeIdentifier, documentIdentifier)
                .sorted(Integer::compareTo)
                .collect(Collectors.toList());
        for (int i = 0; i < pageNumbers.size(); i++) {
            if (i != pageNumbers.get(i))
                return true;
        }

        return false;
    }
}