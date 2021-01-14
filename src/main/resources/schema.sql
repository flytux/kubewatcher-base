-- create database `kube-watcher`;
-- create user 'kwuser'@'%' identified by 'kubeworks!@';
-- create user 'kwuser'@'localhost' identified by 'kubeworks!@';
--
-- grant all privileges on `kube-watcher`.* to kwuser@'%';
-- grant all privileges on `kube-watcher`.* to kwuser@'localhost';
-- flush privileges;
-- drop table page, page_row, page_row_panel, chart_query, page_variable, variable_query;

create table page
(
    page_id        bigint unsigned auto_increment      primary key,
    title          varchar(200)                     not null,
    nav            varchar(1000)                    not null,
    fragment_name  varchar(50)                      null,
    create_time    timestamp default CURRENT_TIMESTAMP not null,
    update_time    timestamp default CURRENT_TIMESTAMP not null,
    description    varchar(500)                        null,
    menu_id        bigint unsigned                     not null
);

create table page_row
(
    page_row_id bigint unsigned auto_increment
        primary key,
    title       varchar(200)                          not null,
    sort_order  bigint unsigned                       not null,
    row_type    varchar(10)                           not null,
    create_time timestamp   default CURRENT_TIMESTAMP not null,
    update_time timestamp   default CURRENT_TIMESTAMP not null,
    page_id     bigint unsigned                       null,
    constraint PAGE_ROW_FK01
        foreign key (page_id) references page (page_id)
);

create table page_row_panel
(
    panel_id    bigint unsigned auto_increment
        primary key,
    title       varchar(200)                              not null,
    sort_order  bigint unsigned                           not null,
    panel_type  varchar(20)                               not null,
    chart_type  varchar(20)                               not null,
    refresh_interval  bigint unsigned       default 60000    not null,
    yaxis_label varchar(50)                               not null,
    yaxis_unit  varchar(20)                               not null,
    yaxis_min   varchar(10)                               not null,
    yaxis_max   varchar(10)                               not null,
    xaxis_mode  varchar(20)                               not null,
    create_time timestamp       default CURRENT_TIMESTAMP not null,
    update_time timestamp       default CURRENT_TIMESTAMP not null,
    page_row_id bigint unsigned                           null,
    constraint PAGE_ROW_PANEL_FK01
        foreign key (page_row_id) references page_row (page_row_id)
);

create table page_variable
(
    variable_id bigint unsigned auto_increment                    primary key,
    name                varchar(100)                              not null,
    sort_order          bigint unsigned                           not null,
    edge_fields         varchar (100)                             null,
    variable_type       varchar(20)                               not null,
    refresh_interval    varchar(3)                                not null,
    hidden_yn           varchar(1)                                not null,
    job_name            varchar(50)                               not null,
    api_query           varchar(1000)                             not null,
    create_time     timestamp           default CURRENT_TIMESTAMP not null,
    update_time     timestamp           default CURRENT_TIMESTAMP not null,
    page_id     bigint unsigned                                   null,
    constraint PAGE_VARIABLE_FK01
        foreign key (page_id) references page (page_id)
);

create table chart_query
(
    c_query_id bigint unsigned auto_increment                    primary key,
    api_query   varchar(1000)                                    not null,
    legend      varchar(100)                                     not null,
    query_step  varchar(3)                                       not null,
    create_time     timestamp          default CURRENT_TIMESTAMP not null,
    update_time     timestamp          default CURRENT_TIMESTAMP not null,
    panel_id    bigint unsigned                                  null,
    constraint CHART_QUERY_FK01
        foreign key (panel_id) references page_row_panel (panel_id)
);