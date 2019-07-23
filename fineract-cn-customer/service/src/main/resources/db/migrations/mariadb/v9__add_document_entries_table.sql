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


create table maat_documents_entry
(
  id                bigint auto_increment
    primary key,
  document_id       bigint                                    not null,
  document_name     varchar(256)                              not null,
  typeUUID              varchar(65)                               not null,
  created_by        varchar(32)                               null,
  status            varchar(256)                              null,
  type              varchar(256)                              not null,
  sub_type          varchar(256)                              not null,
  approved_by       varchar(256)                              null,
  approved_on       timestamp(3)                              null,
  rejected_on       timestamp(3)                              null,
  rejected_by       varchar(32)                               null,
  reason_for_reject text                                      null,
  created_on        timestamp(3) default CURRENT_TIMESTAMP(3) null on update CURRENT_TIMESTAMP(3),
  description       varchar(32)                               null,
  constraint maat_documents_entry_fk
    foreign key (document_id) references maat_documents (id)
);


