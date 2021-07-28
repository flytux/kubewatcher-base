CREATE TABLE IF NOT EXISTS `application_management` (
	`application_id` bigint unsigned NOT NULL AUTO_INCREMENT,
	`code` varchar(50) NOT NULL,
	`name` varchar(50) NOT NULL,
	`namespace` varchar(50) NOT NULL,
	`display_name` varchar(50) NOT NULL,
	PRIMARY KEY (`application_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `cluster_pod_usage` (
    `application` varchar(255) NOT NULL,
    `namespace` varchar(255) NOT NULL,
    `pod_count` int unsigned NOT NULL DEFAULT '0',
    `max_cpu` decimal(27,9) unsigned NOT NULL DEFAULT '0.000000000',
    `avg_cpu` decimal(27,9) unsigned NOT NULL DEFAULT '0.000000000',
    `max_memory` decimal(18,0) unsigned NOT NULL DEFAULT '0',
    `avg_memory` decimal(18,0) unsigned NOT NULL DEFAULT '0',
    `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`application`,`namespace`,`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `page` (
    `page_id` bigint unsigned NOT NULL AUTO_INCREMENT,
    `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `description` varchar(500) DEFAULT NULL,
    `menu_id` bigint unsigned NOT NULL,
    `nav` varchar(1000) NOT NULL,
    `title` varchar(200) NOT NULL,
    PRIMARY KEY (`page_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `page_row` (
    `page_row_id` bigint unsigned NOT NULL AUTO_INCREMENT,
    `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `row_type` varchar(10) NOT NULL,
    `sort_order` bigint unsigned NOT NULL,
    `title` varchar(200) NOT NULL,
    `page_id` bigint unsigned DEFAULT NULL,
    PRIMARY KEY (`page_row_id`),
    KEY `PAGE_ROW_FK01` (`page_id`),
    CONSTRAINT `PAGE_ROW_FK01` FOREIGN KEY (`page_id`) REFERENCES `page` (`page_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `page_row_panel` (
      `panel_id` bigint unsigned NOT NULL AUTO_INCREMENT,
      `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
      `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
      `chart_type` varchar(20) NOT NULL,
      `fragment_name` varchar(50) DEFAULT NULL,
      `legend_visible` tinyint(1) NOT NULL DEFAULT '1',
      `main_yn` varchar(1) NOT NULL DEFAULT 'Y',
      `panel_type` varchar(20) NOT NULL,
      `refresh_interval` bigint unsigned NOT NULL DEFAULT '60000',
      `repeat_variable` varchar(50) DEFAULT NULL,
      `sort_order` bigint unsigned NOT NULL DEFAULT '0',
      `title` varchar(200) NOT NULL,
      `xaxis_mode` varchar(20) NOT NULL,
      `yaxis_label` varchar(50) NOT NULL,
      `yaxis_max` varchar(10) NOT NULL,
      `yaxis_min` varchar(10) NOT NULL,
      `yaxis_unit` varchar(20) NOT NULL,
      `page_row_id` bigint unsigned DEFAULT NULL,
      PRIMARY KEY (`panel_id`),
      KEY `PAGE_ROW_PANEL_FK01` (`page_row_id`),
      CONSTRAINT `PAGE_ROW_PANEL_FK01` FOREIGN KEY (`page_row_id`) REFERENCES `page_row` (`page_row_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `page_variable` (
    `variable_id` bigint unsigned NOT NULL AUTO_INCREMENT,
    `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `api_query` varchar(1000) NOT NULL,
    `edge_fields` varchar(100) DEFAULT NULL,
    `hidden_yn` varchar(1) NOT NULL,
    `job_name` varchar(50) NOT NULL,
    `name` varchar(100) NOT NULL,
    `query_type` varchar(20) NOT NULL,
    `refresh_interval` varchar(3) NOT NULL,
    `sort_order` bigint unsigned NOT NULL DEFAULT '0',
    `variable_type` varchar(20) NOT NULL,
    `page_id` bigint unsigned DEFAULT NULL,
    PRIMARY KEY (`variable_id`),
    KEY `PAGE_VARIABLE_FK01` (`page_id`),
    CONSTRAINT `PAGE_VARIABLE_FK01` FOREIGN KEY (`page_id`) REFERENCES `page` (`page_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `chart_query` (
    `c_query_id` bigint unsigned NOT NULL AUTO_INCREMENT,
    `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `api_query` varchar(1000) NOT NULL,
    `legend` varchar(100) NOT NULL,
    `query_step` varchar(3) NOT NULL,
	`query_type` varchar(20) NOT NULL DEFAULT 'PROXY_METRIC',
    `panel_id` bigint unsigned DEFAULT NULL,
    PRIMARY KEY (`c_query_id`),
    KEY `CHART_QUERY_FK01` (`panel_id`),
    CONSTRAINT `CHART_QUERY_FK01` FOREIGN KEY (`panel_id`) REFERENCES `page_row_panel` (`panel_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

