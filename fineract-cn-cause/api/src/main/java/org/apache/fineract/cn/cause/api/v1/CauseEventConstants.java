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
package org.apache.fineract.cn.cause.api.v1;

@SuppressWarnings("unused")
public interface CauseEventConstants {

    String DESTINATION = "cause-v1";

    String SELECTOR_NAME = "action";

    String INITIALIZE = "initialize";

    String POST_CAUSE = "post-cause";
    String PUT_CAUSE = "put-cause";
    String PUBLISH_CAUSE = "publish-cause";
    String PUT_ADDRESS = "put-address";
    String PUT_CONTACT_DETAILS = "put-contact-details";
    String POST_IDENTIFICATION_CARD = "post-identification-card";
    String PUT_IDENTIFICATION_CARD = "put-identification-card";
    String DELETE_IDENTIFICATION_CARD = "delete-identification-card";

    String POST_IDENTIFICATION_CARD_SCAN = "post-identification-card-scan";
    String DELETE_IDENTIFICATION_CARD_SCAN = "delete-identification-card-scan";

    String ACTIVATE_CAUSE = "activate-cause";
    String LOCK_CAUSE = "lock-cause";
    String UNLOCK_CAUSE = "unlock-cause";
    String CLOSE_CAUSE = "close-cause";
    String REOPEN_CAUSE = "reopen-cause";
    String DELETE_CAUSE = "delete-cause";

    String POST_TASK = "post-task";
    String PUT_TASK = "put-task";

    String POST_PORTRAIT = "post-portrait";
    String DELETE_PORTRAIT = "delete-portrait";
    
    String POST_CATEGORY = "post-category";
    String PUT_CATEGORY = "put-category";
    String DELETE_CATEGORY = "delete-category";
    String POST_CATEGORY_COMPLETE = "post-category-complete";

    String POST_RATING = "post-rating";
    String PUT_RATING = "put-rating";
    String DELETE_RATING = "delete-rating";
    String POST_RATING_COMPLETE = "post-rating-complete";

    String POST_DOCUMENT = "post-document";
    String PUT_DOCUMENT = "put-document";
    String DELETE_DOCUMENT = "delete-document";
    String POST_DOCUMENT_PAGE = "post-document-page";
    String DELETE_DOCUMENT_PAGE = "delete-document-page";
    String POST_DOCUMENT_COMPLETE = "post-document-complete";

    String SELECTOR_INITIALIZE = SELECTOR_NAME + " = '" + INITIALIZE + "'";

    String SELECTOR_POST_CAUSE = SELECTOR_NAME + " = '" + POST_CAUSE + "'";
    String SELECTOR_PUT_CAUSE = SELECTOR_NAME + " = '" + PUT_CAUSE + "'";
    String SELECTOR_DELETE_CAUSE = SELECTOR_NAME + " = '" + DELETE_CAUSE + "'";
    String SELECTOR_PUT_ADDRESS = SELECTOR_NAME + " = '" + PUT_ADDRESS + "'";
    String SELECTOR_PUT_CONTACT_DETAILS = SELECTOR_NAME + " = '" + PUT_CONTACT_DETAILS + "'";

    String SELECTOR_POST_IDENTIFICATION_CARD = SELECTOR_NAME + " = '" + POST_IDENTIFICATION_CARD + "'";
    String SELECTOR_PUT_IDENTIFICATION_CARD = SELECTOR_NAME + " = '" + PUT_IDENTIFICATION_CARD + "'";
    String SELECTOR_DELETE_IDENTIFICATION_CARD = SELECTOR_NAME + " = '" + DELETE_IDENTIFICATION_CARD + "'";

    String SELECTOR_POST_IDENTIFICATION_CARD_SCAN = SELECTOR_NAME + " = '" + POST_IDENTIFICATION_CARD_SCAN + "'";
    String SELECTOR_DELETE_IDENTIFICATION_CARD_SCAN = SELECTOR_NAME + " = '" + DELETE_IDENTIFICATION_CARD_SCAN + "'";

    String SELECTOR_ACTIVATE_CAUSE = SELECTOR_NAME + " = '" + ACTIVATE_CAUSE + "'";
    String SELECTOR_LOCK_CAUSE = SELECTOR_NAME + " = '" + LOCK_CAUSE + "'";
    String SELECTOR_UNLOCK_CAUSE = SELECTOR_NAME + " = '" + UNLOCK_CAUSE + "'";
    String SELECTOR_CLOSE_CAUSE = SELECTOR_NAME + " = '" + CLOSE_CAUSE + "'";
    String SELECTOR_REOPEN_CAUSE = SELECTOR_NAME + " = '" + REOPEN_CAUSE + "'";

    String SELECTOR_POST_TASK = SELECTOR_NAME + " = '" + POST_TASK + "'";
    String SELECTOR_PUT_TASK = SELECTOR_NAME + " = '" + PUT_TASK + "'";

    String SELECTOR_PUT_PORTRAIT = SELECTOR_NAME + " = '" + POST_PORTRAIT + "'";
    String SELECTOR_DELETE_PORTRAIT = SELECTOR_NAME + " = '" + DELETE_PORTRAIT + "'";

    String SELECTOR_POST_CATEGORY = SELECTOR_NAME + " = '" + POST_CATEGORY + "'";
    String SELECTOR_PUT_CATEGORY = SELECTOR_NAME + " = '" + PUT_CATEGORY + "'";
    String SELECTOR_DELETE_CATEGORY = SELECTOR_NAME + " = '" + DELETE_CATEGORY + "'";
    String SELECTOR_POST_CATEGORY_COMPLETE = SELECTOR_NAME + " = '" + POST_CATEGORY_COMPLETE + "'";

    String SELECTOR_POST_RATING = SELECTOR_NAME + " = '" + POST_RATING + "'";
    String SELECTOR_PUT_RATING = SELECTOR_NAME + " = '" + PUT_RATING + "'";
    String SELECTOR_DELETE_RATING = SELECTOR_NAME + " = '" + DELETE_RATING + "'";
    String SELECTOR_POST_RATING_COMPLETE = SELECTOR_NAME + " = '" + POST_RATING_COMPLETE + "'";

    String SELECTOR_POST_DOCUMENT = SELECTOR_NAME + " = '" + POST_DOCUMENT + "'";
    String SELECTOR_PUT_DOCUMENT = SELECTOR_NAME + " = '" + PUT_DOCUMENT + "'";
    String SELECTOR_DELETE_DOCUMENT = SELECTOR_NAME + " = '" + DELETE_DOCUMENT + "'";
    String SELECTOR_POST_DOCUMENT_PAGE = SELECTOR_NAME + " = '" + POST_DOCUMENT_PAGE + "'";
    String SELECTOR_DELETE_DOCUMENT_PAGE = SELECTOR_NAME + " = '" + DELETE_DOCUMENT_PAGE + "'";
    String SELECTOR_POST_DOCUMENT_COMPLETE = SELECTOR_NAME + " = '" + POST_DOCUMENT_COMPLETE + "'";
}
