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
package org.apache.fineract.cn.cause.api.v1.events;

import java.util.Objects;

/**
 * @author Myrle Krantz
 */
@SuppressWarnings("unused")
public class DocumentPageEvent {

    private String causeIdentifier;

    private String documentIdentifier;

    private int pageNumber;

    public DocumentPageEvent(String causeIdentifier, String documentIdentifier, int pageNumber) {
        this.causeIdentifier = causeIdentifier;
        this.documentIdentifier = documentIdentifier;
        this.pageNumber = pageNumber;
    }

    public String getCauseIdentifier() {
        return causeIdentifier;
    }

    public void setCauseIdentifier(String causeIdentifier) {
        this.causeIdentifier = causeIdentifier;
    }

    public String getDocumentIdentifier() {
        return documentIdentifier;
    }

    public void setDocumentIdentifier(String documentIdentifier) {
        this.documentIdentifier = documentIdentifier;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DocumentPageEvent that = (DocumentPageEvent) o;
        return pageNumber == that.pageNumber &&
                Objects.equals(causeIdentifier, that.causeIdentifier) &&
                Objects.equals(documentIdentifier, that.documentIdentifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(causeIdentifier, documentIdentifier, pageNumber);
    }

    @Override
    public String toString() {
        return "DocumentPageEvent{" +
                "causeIdentifier='" + causeIdentifier + '\'' +
                ", documentIdentifier='" + documentIdentifier + '\'' +
                ", pageNumber=" + pageNumber +
                '}';
    }
}
