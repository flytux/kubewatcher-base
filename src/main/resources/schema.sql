-- create database `kube-watcher`;
-- create user 'kwuser'@'%' identified by 'kubeworks!@';
-- create user 'kwuser'@'localhost' identified by 'kubeworks!@';
--
-- grant all privileges on `kube-watcher`.* to kwuser@'%';
-- grant all privileges on `kube-watcher`.* to kwuser@'localhost';
-- flush privileges;

create table page
(
    page_id     bigint unsigned auto_increment
        primary key,
    create_time timestamp default CURRENT_TIMESTAMP not null,
    update_time timestamp default CURRENT_TIMESTAMP not null,
    description varchar(500)                        null,
    menu_id     bigint unsigned                     not null
);

create table page_row
(
    page_row_id bigint unsigned auto_increment
        primary key,
    create_time timestamp   default CURRENT_TIMESTAMP not null,
    update_time timestamp   default CURRENT_TIMESTAMP not null,
    sort        bigint unsigned                       not null,
    title       varchar(200)                          not null,
    type        varchar(10) default 'P'               not null,
    page_id     bigint unsigned                       null,
    constraint PAGE_ROW_FK01
        foreign key (page_id) references page (page_id)
);

create table page_row_panel
(
    panel_id    bigint unsigned auto_increment
        primary key,
    create_time timestamp       default CURRENT_TIMESTAMP not null,
    update_time timestamp       default CURRENT_TIMESTAMP not null,
    data_type   varchar(20)                               not null,
    panel_type  varchar(20)                               not null,
    sort        bigint unsigned default '0'               not null,
    src         varchar(1000)                             not null,
    title       varchar(200)                              not null,
    page_row_id bigint unsigned                           null,
    constraint PAGE_ROW_PANEL_FK01
        foreign key (page_row_id) references page_row (page_row_id)
);

create table page_variable
(
    variable_id bigint unsigned auto_increment
        primary key,
    create_time timestamp       default CURRENT_TIMESTAMP not null,
    update_time timestamp       default CURRENT_TIMESTAMP not null,
    name        varchar(100)                              not null,
    sort        bigint unsigned default '0'               not null,
    src         varchar(1000)                             not null,
    type        varchar(20)     default 'api'             not null,
    page_id     bigint unsigned                           null,
    edge_fields varchar (100)                             null,
    constraint PAGE_VARIABLE_FK01
        foreign key (page_id) references page (page_id)
);
