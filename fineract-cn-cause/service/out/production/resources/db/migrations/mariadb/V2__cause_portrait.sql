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

CREATE TABLE cass_portraits (
  id BIGINT NOT NULL AUTO_INCREMENT,
  cause_id BIGINT NOT NULL,
  content_type VARCHAR(256) NOT NULL,
  size BIGINT NOT NULL,
  image MEDIUMBLOB NOT NULL,
  CONSTRAINT cass_portraits_pk PRIMARY KEY (id),
  CONSTRAINT cass_id_portraits_causes_fk FOREIGN KEY (cause_id) REFERENCES cass_causes (id) ON UPDATE RESTRICT
);

ALTER TABLE cass_images ADD created_by VARCHAR(32) NULL;
ALTER TABLE cass_images ADD created_on TIMESTAMP(3) NULL;
ALTER TABLE cass_images ADD last_modified_by VARCHAR(32) NULL;
ALTER TABLE cass_images ADD last_modified_on TIMESTAMP(3) NULL;
