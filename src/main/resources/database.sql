create table page
(
    page_id     BIGINT auto_increment primary key,
    menu_id     BIGINT not null,
    description VARCHAR(500),
    create_time TIMESTAMP default CURRENT_TIMESTAMP not null,
    update_time TIMESTAMP default CURRENT_TIMESTAMP not null
);

create table page_row
(
    page_row_id BIGINT auto_increment primary key,
    divider     TINYINT   default 0                 not null,
    page_id     BIGINT                              not null,
    sort        BIGINT                              not null,
    title       VARCHAR(200)                        not null,
    create_time TIMESTAMP default CURRENT_TIMESTAMP not null,
    update_time TIMESTAMP default CURRENT_TIMESTAMP not null
);


create table page_row_panel
(
    panel_id    BIGINT auto_increment primary key,
    data_type   VARCHAR(20)                         not null,
    panel_type  VARCHAR(20)                         not null,
    page_row_id      BIGINT                              not null,
    src         VARCHAR(1000)                       not null,
    title       VARCHAR(200)                        not null,
    create_time TIMESTAMP default CURRENT_TIMESTAMP not null,
    update_time TIMESTAMP default CURRENT_TIMESTAMP not null
);

ALTER TABLE page_row ADD CONSTRAINT PAGE_FK01 FOREIGN KEY (page_id) REFERENCES page (menu_id);
ALTER TABLE page_row_panel ADD CONSTRAINT PAGE_ROW_FK01 FOREIGN KEY (page_row_id) REFERENCES page_row (page_row_id);

# page table insert
INSERT INTO page (page_id, create_time, update_time, description, menu_id) VALUES (1, '2020-08-03 04:50:47', '2020-08-03 04:50:47', 'Virtual Machine Overview', 140);
INSERT INTO page (page_id, create_time, update_time, description, menu_id) VALUES (2, '2020-08-03 04:50:47', '2020-08-05 04:45:39', 'Virtual Machine Overview 현황', 141);


# page_row table insert
INSERT INTO page_row (page_row_id, create_time, update_time, divider, sort, title, page_id) VALUES (1, '2020-08-03 04:53:19', '2020-08-03 04:53:19', 0, 1, '보험코어 VM 현황', 1);
INSERT INTO page_row (page_row_id, create_time, update_time, divider, sort, title, page_id) VALUES (2, '2020-08-03 04:53:19', '2020-08-04 00:27:50', 0, 2, '보험코어 VM 리소스', 1);
INSERT INTO page_row (page_row_id, create_time, update_time, divider, sort, title, page_id) VALUES (3, '2020-08-05 04:51:40', '2020-08-05 04:51:40', 0, 1, '#Bar', 2);
INSERT INTO page_row (page_row_id, create_time, update_time, divider, sort, title, page_id) VALUES (4, '2020-08-05 04:51:40', '2020-08-05 04:51:40', 0, 2, 'CPU', 2);
INSERT INTO page_row (page_row_id, create_time, update_time, divider, sort, title, page_id) VALUES (5, '2020-08-05 04:51:40', '2020-08-05 04:51:40', 0, 3, 'Memory', 2);
INSERT INTO page_row (page_row_id, create_time, update_time, divider, sort, title, page_id) VALUES (6, '2020-08-05 04:51:40', '2020-08-05 04:51:40', 0, 4, 'Disk', 2);
INSERT INTO page_row (page_row_id, create_time, update_time, divider, sort, title, page_id) VALUES (7, '2020-08-05 04:51:40', '2020-08-05 04:51:40', 0, 5, 'Network', 2);
INSERT INTO page_row (page_row_id, create_time, update_time, divider, sort, title, page_id) VALUES (8, '2020-08-05 04:51:40', '2020-08-05 04:51:40', 0, 6, 'File System', 2);


# page_row_panel table insert
INSERT INTO page_row_panel (panel_id, create_time, update_time, data_type, panel_type, src, title, page_row_id, sort) VALUES (1, '2020-08-03 04:53:23', '2020-08-04 06:05:40', 'promql', 'card', 'sum(kube_node_info{node=~"^.*$"})', 'VM', 1, 1);
INSERT INTO page_row_panel (panel_id, create_time, update_time, data_type, panel_type, src, title, page_row_id, sort) VALUES (2, '2020-08-03 04:53:23', '2020-08-04 06:05:40', 'promql', 'card', 'sum(machine_cpu_cores{node=~"^.*$"})', 'Core', 1, 2);
INSERT INTO page_row_panel (panel_id, create_time, update_time, data_type, panel_type, src, title, page_row_id, sort) VALUES (3, '2020-08-03 04:53:23', '2020-08-04 07:43:21', 'iframe', 'card-badge', 'http://grafana.kw-mgmt-dev/d-solo/icjpCppik/cluster?orgId=1&refresh=1m&panelId=6&theme=light', 'Avg. CPU', 1, 3);
INSERT INTO page_row_panel (panel_id, create_time, update_time, data_type, panel_type, src, title, page_row_id, sort) VALUES (4, '2020-08-03 04:53:23', '2020-08-04 08:14:42', 'iframe', 'card-badge', 'http://grafana.kw-mgmt-dev/d-solo/icjpCppik/cluster?orgId=1&refresh=1m&panelId=4&theme=light', 'Avg. Memory', 1, 4);
INSERT INTO page_row_panel (panel_id, create_time, update_time, data_type, panel_type, src, title, page_row_id, sort) VALUES (5, '2020-08-03 04:53:23', '2020-08-04 08:14:42', 'iframe', 'card-badge', 'http://grafana.kw-mgmt-dev/d-solo/icjpCppik/cluster?orgId=1&refresh=1m&panelId=7&theme=light', 'Avg. Disk', 1, 5);
INSERT INTO page_row_panel (panel_id, create_time, update_time, data_type, panel_type, src, title, page_row_id, sort) VALUES (6, '2020-08-03 04:53:23', '2020-08-04 08:14:42', 'iframe', 'card', 'http://grafana.kw-mgmt-dev/d-solo/icjpCppik/cluster?orgId=1&refresh=1m&panelId=2051&var-Node=All&theme=light', 'CPU', 2, 6);
INSERT INTO page_row_panel (panel_id, create_time, update_time, data_type, panel_type, src, title, page_row_id, sort) VALUES (7, '2020-08-03 04:53:23', '2020-08-04 08:14:42', 'iframe', 'card', 'http://grafana.kw-mgmt-dev/d-solo/icjpCppik/cluster?orgId=1&refresh=1m&panelId=2052&var-Node=All&theme=light', 'Memory', 2, 7);
INSERT INTO page_row_panel (panel_id, create_time, update_time, data_type, panel_type, src, title, page_row_id, sort) VALUES (8, '2020-08-03 04:53:23', '2020-08-04 08:14:42', 'iframe', 'card', 'http://grafana.kw-mgmt-dev/d-solo/qtZCNq4Gz/custom-cluster?panelId=2063&orgId=1&refresh=1m&var-Node=All&theme=light', 'Disk I/O', 2, 8);
INSERT INTO page_row_panel (panel_id, create_time, update_time, data_type, panel_type, src, title, page_row_id, sort) VALUES (9, '2020-08-03 04:53:23', '2020-08-04 08:18:48', 'iframe', 'card', 'http://grafana.kw-mgmt-dev/d-solo/icjpCppik/cluster?orgId=1&refresh=1m&var-Node=All&panelId=32&theme=light', 'Network', 2, 9);
INSERT INTO page_row_panel (panel_id, create_time, update_time, data_type, panel_type, src, title, page_row_id, sort) VALUES (10, '2020-08-03 04:53:23', '2020-08-04 08:37:47', 'iframe', 'card', 'http://grafana.kw-mgmt-dev/d-solo/qtZCNq4Gz/custom-cluster?orgId=1&refresh=1m&var-Node=All&panelId=181&theme=light', 'File System', 2, 10);
INSERT INTO page_row_panel (panel_id, create_time, update_time, data_type, panel_type, src, title, page_row_id, sort) VALUES (11, '2020-08-06 04:37:06', '2020-08-06 04:39:36', 'iframe', 'card', 'http://grafana.kw-mgmt-dev/d-solo/GfakcZIMk/node-exporter-full-custom?orgId=1&refresh=5s&panelId=77&theme=light', 'CPU Basic', 4, 1);
INSERT INTO page_row_panel (panel_id, create_time, update_time, data_type, panel_type, src, title, page_row_id, sort) VALUES (12, '2020-08-06 04:37:06', '2020-08-06 04:39:36', 'iframe', 'card', 'http://grafana.kw-mgmt-dev/d-solo/GfakcZIMk/node-exporter-full-custom?orgId=1&refresh=5s&panelId=3&theme=light', 'CPU', 4, 2);
INSERT INTO page_row_panel (panel_id, create_time, update_time, data_type, panel_type, src, title, page_row_id, sort) VALUES (13, '2020-08-06 04:37:06', '2020-08-06 04:39:36', 'iframe', 'card', 'http://grafana.kw-mgmt-dev/d-solo/GfakcZIMk/node-exporter-full-custom?orgId=1&refresh=5s&panelId=7&theme=light', 'System Load', 4, 3);
INSERT INTO page_row_panel (panel_id, create_time, update_time, data_type, panel_type, src, title, page_row_id, sort) VALUES (14, '2020-08-06 04:37:06', '2020-08-06 04:39:36', 'iframe', 'card', 'http://grafana.kw-mgmt-dev/d-solo/GfakcZIMk/node-exporter-full-custom?orgId=1&refresh=5s&panelId=62&theme=light', 'Process Status', 4, 4);
INSERT INTO page_row_panel (panel_id, create_time, update_time, data_type, panel_type, src, title, page_row_id, sort) VALUES (15, '2020-08-06 04:37:06', '2020-08-06 04:39:36', 'iframe', 'card', 'http://grafana.kw-mgmt-dev/d-solo/GfakcZIMk/node-exporter-full-custom?orgId=1&refresh=5s&panelId=78&theme=light', 'Memory Basic', 5, 5);
INSERT INTO page_row_panel (panel_id, create_time, update_time, data_type, panel_type, src, title, page_row_id, sort) VALUES (16, '2020-08-06 04:37:06', '2020-08-06 04:39:36', 'iframe', 'card', 'http://grafana.kw-mgmt-dev/d-solo/GfakcZIMk/node-exporter-full-custom?orgId=1&refresh=5s&panelId=24&theme=light', 'Memory Stack', 5, 6);
INSERT INTO page_row_panel (panel_id, create_time, update_time, data_type, panel_type, src, title, page_row_id, sort) VALUES (17, '2020-08-06 04:37:06', '2020-08-06 04:39:36', 'iframe', 'card', 'http://grafana.kw-mgmt-dev/d-solo/GfakcZIMk/node-exporter-full-custom?orgId=1&refresh=5s&panelId=191&theme=light', 'Memory Active/Inactive', 5, 7);
INSERT INTO page_row_panel (panel_id, create_time, update_time, data_type, panel_type, src, title, page_row_id, sort) VALUES (18, '2020-08-06 04:37:06', '2020-08-06 04:39:36', 'iframe', 'card', 'http://grafana.kw-mgmt-dev/d-solo/GfakcZIMk/node-exporter-full-custom?orgId=1&refresh=5s&panelId=176&theme=light', 'Memory Page In/Out', 5, 8);
INSERT INTO page_row_panel (panel_id, create_time, update_time, data_type, panel_type, src, title, page_row_id, sort) VALUES (19, '2020-08-06 04:37:06', '2020-08-06 04:39:36', 'iframe', 'card', 'http://grafana.kw-mgmt-dev/d-solo/GfakcZIMk/node-exporter-full-custom?orgId=1&refresh=5s&panelId=152&theme=light', 'Disk Space Used Basic', 6, 9);
INSERT INTO page_row_panel (panel_id, create_time, update_time, data_type, panel_type, src, title, page_row_id, sort) VALUES (20, '2020-08-06 04:37:06', '2020-08-06 04:39:36', 'iframe', 'card', 'http://grafana.kw-mgmt-dev/d-solo/GfakcZIMk/node-exporter-full-custom?orgId=1&refresh=5s&panelId=156&theme=light', 'Disk Space Used', 6, 10);
INSERT INTO page_row_panel (panel_id, create_time, update_time, data_type, panel_type, src, title, page_row_id, sort) VALUES (21, '2020-08-06 04:37:06', '2020-08-06 04:39:36', 'iframe', 'card', 'http://grafana.kw-mgmt-dev/d-solo/GfakcZIMk/node-exporter-full-custom?orgId=1&refresh=5s&panelId=42&theme=light', 'Disk I/O(Read/Write Bytes', 6, 11);
INSERT INTO page_row_panel (panel_id, create_time, update_time, data_type, panel_type, src, title, page_row_id, sort) VALUES (22, '2020-08-06 04:37:06', '2020-08-06 04:39:36', 'iframe', 'card', 'http://grafana.kw-mgmt-dev/d-solo/GfakcZIMk/node-exporter-full-custom?orgId=1&refresh=5s&panelId=229&theme=light', 'Disk IOps', 6, 12);
INSERT INTO page_row_panel (panel_id, create_time, update_time, data_type, panel_type, src, title, page_row_id, sort) VALUES (23, '2020-08-06 04:37:06', '2020-08-06 04:39:36', 'iframe', 'card', 'http://grafana.kw-mgmt-dev/d-solo/GfakcZIMk/node-exporter-full-custom?orgId=1&refresh=5s&panelId=74&theme=light', 'Network Traffic basic', 7, 13);
INSERT INTO page_row_panel (panel_id, create_time, update_time, data_type, panel_type, src, title, page_row_id, sort) VALUES (24, '2020-08-06 04:37:06', '2020-08-06 04:39:36', 'iframe', 'card', 'http://grafana.kw-mgmt-dev/d-solo/GfakcZIMk/node-exporter-full-custom?orgId=1&refresh=5s&panelId=60&theme=light', 'Network Traffic by packets', 7, 14);
INSERT INTO page_row_panel (panel_id, create_time, update_time, data_type, panel_type, src, title, page_row_id, sort) VALUES (25, '2020-08-06 04:37:06', '2020-08-06 04:39:36', 'iframe', 'card', 'http://grafana.kw-mgmt-dev/d-solo/GfakcZIMk/node-exporter-full-custom?orgId=1&refresh=5s&panelId=317&theme=light', 'File System Table', 8, 15);
INSERT INTO page_row_panel (panel_id, create_time, update_time, data_type, panel_type, src, title, page_row_id, sort) VALUES (26, '2020-08-03 04:53:23', '2020-08-04 06:05:40', 'labelValues', 'selectBox', 'label_values(node_load1, node)', 'VM Detail Template Variable', 3, 0);