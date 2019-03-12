create table maat_documents_status
(
  id                bigint auto_increment primary key,
  document_id       bigint                                    not null,
  created_by        varchar(32)                               not null,
  status            varchar(256)                              not null,
  type              varchar(256)                              not null,
  sub_type          varchar(256)                              not null,
  approved_by       varchar(256)                              null,
  approved_on       timestamp(3)                              null,
  rejected_on       timestamp(3)                              null,
  rejected_by       varchar(32)                               null,
  reason_for_reject text                                      null,
  created_on        timestamp(3) default CURRENT_TIMESTAMP(3) not null on update CURRENT_TIMESTAMP(3),
  constraint maat_documents_status_fk
    foreign key (document_id) references cass_documents (id)
);

