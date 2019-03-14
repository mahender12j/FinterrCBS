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

import org.apache.fineract.cn.customer.api.v1.domain.CustomerDocument;
import org.apache.fineract.cn.customer.api.v1.domain.CustomerDocumentsBody;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Myrle Krantz
 */
public class CreateKYCDocumentCommand {
    private final String customerIdentifier;
    private final CustomerDocumentsBody customerDocumentsBody;
    private final MultipartFile file;

    public CreateKYCDocumentCommand(final String customerIdentifier,
                                    final MultipartFile file,
                                    final CustomerDocumentsBody customerDocumentsBody) {
        this.customerIdentifier = customerIdentifier;
        this.customerDocumentsBody = customerDocumentsBody;
        this.file = file;
    }

    public String getCustomerIdentifier() {
        return customerIdentifier;
    }

    public CustomerDocumentsBody getCustomerDocumentsBody() {
        return customerDocumentsBody;
    }

    public MultipartFile getFile() {
        return file;
    }

    @Override
    public String toString() {
        return "CreateDocumentCommand{" +
                "customerIdentifier='" + customerIdentifier + '\'' +
                ", customerDocument=" + customerDocumentsBody.toString() +
                '}';
    }
}
