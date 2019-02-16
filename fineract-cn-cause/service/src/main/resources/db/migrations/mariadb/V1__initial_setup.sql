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

CREATE TABLE cass_addresses (
  id BIGINT NOT NULL AUTO_INCREMENT,
  street VARCHAR(256) NOT NULL,
  city VARCHAR(256) NOT NULL,
  postal_code VARCHAR(32) NULL,
  region VARCHAR(256) NULL,
  country_code VARCHAR(2) NOT NULL,
  country VARCHAR(256) NOT NULL,
  CONSTRAINT cass_addresses_pk PRIMARY KEY (id)
);

CREATE TABLE cass_causes (
  id BIGINT NOT NULL AUTO_INCREMENT,
  identifier VARCHAR(32) NOT NULL,
  a_type VARCHAR(32) NOT NULL,
  title VARCHAR(256) NOT NULL,
  description TEXT NOT NULL,
  t_n_c TEXT NOT NULL,
  start_date DATE NOT NULL,
  end_date DATE NOT NULL,
  completed_date DATE NULL,
  beneficiary_account_identifier VARCHAR(512) NULL,
  soft_target DECIMAL(15,5) NOT NULL,
  hard_target DECIMAL(15,5) NOT NULL,
  actual_raised_fiat DECIMAL(15,5) NULL,
  actual_raised_fin DECIMAL(15,8) NULL,
  current_state VARCHAR(32) NOT NULL,
  discount_rate DECIMAL(2,2) NOT NULL,
  fin_rate DECIMAL(15,8) NOT NULL,
  fins_coll_limit BIGINT NOT NULL,
  address_id BIGINT NOT NULL,
  approved_by VARCHAR(32) NULL,
  approved_on DATE NULL,
  created_by VARCHAR(32) NULL,
  created_on TIMESTAMP(3) null,
  last_modified_by VARCHAR(32) NULL,
  last_modified_on TIMESTAMP(3) NULL,
  CONSTRAINT cass_causes_pk PRIMARY KEY (id),
  CONSTRAINT cass_cause_identifier_uq UNIQUE (identifier),
  CONSTRAINT cass_causes_addresses_fk FOREIGN KEY (address_id) REFERENCES cass_addresses (id)
);

CREATE TABLE cass_images (
  id BIGINT NOT NULL AUTO_INCREMENT,
  a_type VARCHAR(128) NOT NULL,
  cause_id BIGINT NOT NULL,
  doc_number VARCHAR(32) NULL,
  issuer VARCHAR(256) NULL,
  CONSTRAINT cass_images_pk PRIMARY KEY (id),
  CONSTRAINT cass_images_causes_fk FOREIGN KEY (cause_id) REFERENCES cass_causes (id) ON UPDATE RESTRICT
);


CREATE TABLE cass_commands (
  id BIGINT NOT NULL AUTO_INCREMENT,
  cause_id BIGINT NOT NULL,
  a_type VARCHAR(32) NOT NULL,
  a_comment VARCHAR(32) NULL,
  created_by VARCHAR(32) NOT NULL,
  created_on TIMESTAMP(3) NULL,
  CONSTRAINT cass_commands_pk PRIMARY KEY (id),
  CONSTRAINT cass_commands_causes_fk FOREIGN KEY (cause_id) REFERENCES cass_causes (id) ON UPDATE RESTRICT
);

CREATE TABLE cass_task_definitions (
  id BIGINT NOT NULL AUTO_INCREMENT,
  identifier VARCHAR(32) NOT NULL,
  a_type VARCHAR(32) NOT NULL,
  a_name VARCHAR(256) NOT NULL,
  description VARCHAR(4096) NULL,
  assigned_commands VARCHAR(512) NOT NULL,
  mandatory BOOLEAN NULL,
  predefined BOOLEAN NULL,
  CONSTRAINT cass_task_definitions_pk PRIMARY KEY (id),
  CONSTRAINT cass_task_def_identifier_uq UNIQUE (identifier)
);

CREATE TABLE cass_task_instances (
  id BIGINT NOT NULL AUTO_INCREMENT,
  task_definition_id BIGINT NOT NULL,
  cause_id BIGINT NOT NULL,
  a_comment VARCHAR(4096) NULL,
  executed_on TIMESTAMP(3) NULL,
  executed_by VARCHAR(32) NULL,
  CONSTRAINT cass_task_instances_pk PRIMARY KEY (id),
  CONSTRAINT cass_task_instances_def_fk FOREIGN KEY (task_definition_id) REFERENCES cass_task_definitions (id) ON UPDATE RESTRICT,
  CONSTRAINT cass_task_instances_caus_fk FOREIGN KEY (cause_id) REFERENCES cass_causes (id) ON UPDATE RESTRICT
);


