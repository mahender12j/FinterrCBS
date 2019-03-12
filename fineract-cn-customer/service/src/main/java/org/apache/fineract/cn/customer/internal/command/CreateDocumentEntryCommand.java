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
package org.apache.fineract.cn.customer.internal.command;

import org.apache.fineract.cn.customer.api.v1.domain.CustomerDocumentEntry;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Myrle Krantz
 */
public class CreateDocumentEntryCommand {
    private final String customerIdentifier;
    private final String documentIdentifier;
    private final CustomerDocumentEntry documentEntry;
    private final MultipartFile file;

    public CreateDocumentEntryCommand(
            final MultipartFile file,
            final String customerIdentifier,
            final String documentIdentifier,
            final CustomerDocumentEntry customerDocumentStatus) {
        this.customerIdentifier = customerIdentifier;
        this.documentIdentifier = documentIdentifier;
        this.documentEntry = customerDocumentStatus;
        this.file = file;
    }

    public String getCustomerIdentifier() {
        return customerIdentifier;
    }

    public String getDocumentIdentifier() {
        return documentIdentifier;
    }

    public CustomerDocumentEntry getDocumentEntry() {
        return documentEntry;
    }

    public MultipartFile getFile() {
        return this.file;
    }
}
