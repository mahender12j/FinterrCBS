--
-- Licensed to the Apache Software Foundation (ASF) under one
-- or more contributor license agreements.  See the NOTICE file
-- distributed with this work for additional information
-- regarding copyright ownership.  The ASF licenses this file
-- to you under the Apache License, Version 2.0 (the
-- "License"); you may not use this file except in compliance
-- with the License.  You may obtain a copy of the License at
--
--   http://www.apache.org/licenses/LICENSE-2.0
--
-- Unless required by applicable law or agreed to in writing,
-- software distributed under the License is distributed on an
-- "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
-- KIND, either express or implied.  See the License for the
-- specific language governing permissions and limitations
-- under the License.
--

CREATE TABLE cass_documents (
  id BIGINT NOT NULL AUTO_INCREMENT,
  cause_id BIGINT NOT NULL,
  rating VARCHAR(32) NOT NULL,
  is_completed BOOLEAN NOT NULL,
  created_on TIMESTAMP(3) NOT NULL,
  created_by VARCHAR(32) NOT NULL,
  CONSTRAINT cass_documents_pk PRIMARY KEY (id),
  CONSTRAINT cass_documents_uq UNIQUE (cause_id, rating),
  CONSTRAINT cass_documents_fk FOREIGN KEY (cause_id) REFERENCES cass_causes (id) ON UPDATE RESTRICT
);

CREATE TABLE cass_document_pages (
  id BIGINT NOT NULL AUTO_INCREMENT,
  document_id BIGINT NOT NULL,
  page_number INT NOT NULL,
  content_type VARCHAR(256) NOT NULL,
  size BIGINT NOT NULL,
  image MEDIUMBLOB NOT NULL,
  CONSTRAINT cass_document_pages_pk PRIMARY KEY (id),
  CONSTRAINT cass_document_pages_uq UNIQUE (document_id, page_number),
  CONSTRAINT cass_document_pages_fk FOREIGN KEY (document_id) REFERENCES cass_documents (id)
);
