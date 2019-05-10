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

/**
 * @author Myrle Krantz
 */
public class RejectDocumentCommand {
    private final String customerIdentifier;
    private final Long customerDocumentId;
    private final String reason;


    public RejectDocumentCommand(String customerIdentifier, Long customerDocumentId, String reason) {
        this.customerIdentifier = customerIdentifier;
        this.customerDocumentId = customerDocumentId;
        this.reason = reason;
    }


    public String getCustomerIdentifier() {
        return customerIdentifier;
    }

    public Long getCustomerDocumentId() {
        return customerDocumentId;
    }

    public String getReason() {
        return reason;
    }

    @Override
    public String toString() {
        return "ChangeDocumentStatusCommand{" +
                "customerIdentifier='" + customerIdentifier + '\'' +
                ", customerDocumentId=" + customerDocumentId +
                '}';
    }
}