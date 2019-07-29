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
package org.apache.fineract.cn.cause.internal.repository;

import javax.persistence.*;

/**
 * @author Myrle Krantz
 */
@Entity
@Table(name = "cass_document_pages")
public class DocumentPageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "document_id")
    private DocumentEntity document;

    @Column(name = "type")
    private String type;

    @Column(name = "doc_ref")
    private String docRef;

    @Column(name = "document_name")
    private String documentName;

    @Column(name = "doc_url")
    private String docUrl;


    public DocumentPageEntity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @SuppressWarnings("unused")
    public DocumentEntity getDocument() {
        return document;
    }

    public void setDocument(DocumentEntity document) {
        this.document = document;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDocumentName() {
        return documentName;
    }

    public String getDocRef() {
        return docRef;
    }

    public void setDocRef(String docRef) {
        this.docRef = docRef;
    }


    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }


    public String getDocUrl() {
        return docUrl;
    }

    public void setDocUrl(String docUrl) {
        this.docUrl = docUrl;
    }

    @Override
    public String toString() {
        return "DocumentPageEntity{" +
                "id=" + id +
                ", document=" + document +
                ", type='" + type + '\'' +
                ", docRef='" + docRef + '\'' +
                ", documentName='" + documentName + '\'' +
                '}';
    }
}
