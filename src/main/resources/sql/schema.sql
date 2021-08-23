DROP TABLE IF EXISTS `alert_history`;
DROP TABLE IF EXISTS `alert_rule`;
DROP TABLE IF EXISTS `alert_rule_metric`;
DROP TABLE IF EXISTS `application_management`;
DROP TABLE IF EXISTS `cluster_pod_usage`;

DROP TABLE IF EXISTS `kw_user_role`;
DROP TABLE IF EXISTS `kw_user`;
DROP TABLE IF EXISTS `kw_user_role_rule`;
DROP TABLE IF EXISTS `kw_user_group`;

DROP TABLE IF EXISTS `chart_query`;
DROP TABLE IF EXISTS `page_row_panel`;
DROP TABLE IF EXISTS `page_row`;
DROP TABLE IF EXISTS `page_variable`;
DROP TABLE IF EXISTS `page`;

CREATE TABLE IF NOT EXISTS `alert_history` (
    `history_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
    `create_time` timestamp NOT NULL DEFAULT current_timestamp(),
    `update_time` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
    `category` varchar(20) DEFAULT '',
    `resource` varchar(20) NOT NULL,
    `type` varchar(20) NOT NULL,
    `message` varchar(1000) NOT NULL DEFAULT '',
    `resolved` tinyint(4) NOT NULL DEFAULT 0,
    `severity` varchar(50) DEFAULT '',
    `target` varchar(255) NOT NULL DEFAULT 'Unknown',
    PRIMARY KEY (`history_id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `alert_rule` (
    `rule_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
    `create_time` timestamp NOT NULL DEFAULT current_timestamp(),
    `update_time` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
    `category` varchar(20) DEFAULT '',
    `resource` varchar(20) NOT NULL,
    `type` varchar(20) NOT NULL,
    `danger_level` bigint(20) unsigned NOT NULL DEFAULT 0,
    `detect_string` varchar(200) DEFAULT '',
    `duration` bigint(20) unsigned NOT NULL DEFAULT 0,
    `severity` varchar(50) DEFAULT '',
    `warning_level` bigint(20) unsigned NOT NULL DEFAULT 0,
    PRIMARY KEY (`rule_id`),
    UNIQUE KEY `ALERT_RULE_UK01` (`type`,`category`,`resource`,`detect_string`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `alert_rule_metric` (
    `category` varchar(20) NOT NULL DEFAULT '',
    `resource` varchar(20) NOT NULL,
    `type` varchar(20) NOT NULL,
    `create_time` timestamp NOT NULL DEFAULT current_timestamp(),
    `update_time` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
    `expression` varchar(1000) NOT NULL,
    `message_template` varchar(1000) NOT NULL DEFAULT '%s %s usage',
    `metric_name` varchar(20) NOT NULL,
    PRIMARY KEY (`category`,`resource`,`type`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `application_management` (
    `application_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
    `create_time` timestamp NOT NULL DEFAULT current_timestamp(),
    `update_time` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
    `code` varchar(50) NOT NULL,
    `display_name` varchar(50) NOT NULL,
    `name` varchar(50) NOT NULL,
    `namespace` varchar(50) NOT NULL,
    PRIMARY KEY (`application_id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `cluster_pod_usage` (
    `application` varchar(255) NOT NULL,
    `namespace` varchar(255) NOT NULL,
    `pod_count` int(10) unsigned NOT NULL DEFAULT 0,
    `max_cpu` decimal(27,9) unsigned NOT NULL DEFAULT 0.000000000,
    `avg_cpu` decimal(27,9) unsigned NOT NULL DEFAULT 0.000000000,
    `max_memory` decimal(18,0) unsigned NOT NULL DEFAULT 0,
    `avg_memory` decimal(18,0) unsigned NOT NULL DEFAULT 0,
    `create_time` timestamp NOT NULL DEFAULT current_timestamp(),
    PRIMARY KEY (`application`,`namespace`,`create_time`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `kw_user_group` (
    `groupname` varchar(40) NOT NULL,
    `create_time` timestamp NOT NULL DEFAULT current_timestamp(),
    `update_time` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
    `description` varchar(200) NOT NULL,
    PRIMARY KEY (`groupname`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `kw_user` (
    `username` varchar(20) NOT NULL,
    `create_time` timestamp NOT NULL DEFAULT current_timestamp(),
    `update_time` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
    `dept` varchar(200) NOT NULL,
    `name` varchar(20) NOT NULL,
    `password` varchar(200) NOT NULL,
    `userno` varchar(7) NOT NULL,
    `groupname` varchar(40) DEFAULT NULL,
    PRIMARY KEY (`username`),
    KEY `KW_USER_FK01` (`groupname`),
    CONSTRAINT `KW_USER_FK01` FOREIGN KEY (`groupname`) REFERENCES `kw_user_group` (`groupname`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `kw_user_role_rule` (
    `rule_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
    `create_time` timestamp NOT NULL DEFAULT current_timestamp(),
    `update_time` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
    `auth_code` varchar(3) DEFAULT NULL,
    `description` varchar(200) NOT NULL,
    `rule` varchar(100) NOT NULL,
    `rule_name` varchar(200) NOT NULL,
    PRIMARY KEY (`rule_id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `kw_user_role` (
    `rolename` varchar(255) NOT NULL,
    `username` varchar(255) NOT NULL,
    `create_time` timestamp NOT NULL DEFAULT current_timestamp(),
    `update_time` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
    `rule_id` bigint(20) unsigned DEFAULT NULL,
    PRIMARY KEY (`rolename`,`username`),
    KEY `KW_USER_ROLE_FK01` (`username`),
    KEY `KW_USER_ROLE_FK02` (`rule_id`),
    CONSTRAINT `KW_USER_ROLE_FK01` FOREIGN KEY (`username`) REFERENCES `kw_user` (`username`),
    CONSTRAINT `KW_USER_ROLE_FK02` FOREIGN KEY (`rule_id`) REFERENCES `kw_user_role_rule` (`rule_id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `page` (
    `page_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
    `create_time` timestamp NOT NULL DEFAULT current_timestamp(),
    `update_time` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
    `description` varchar(500) DEFAULT NULL,
    `menu_id` bigint(20) unsigned NOT NULL,
    `nav` varchar(1000) NOT NULL,
    `title` varchar(200) NOT NULL,
    PRIMARY KEY (`page_id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `page_row` (
    `page_row_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
    `create_time` timestamp NOT NULL DEFAULT current_timestamp(),
    `update_time` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
    `row_type` varchar(10) NOT NULL,
    `sort_order` bigint(20) unsigned NOT NULL,
    `title` varchar(200) NOT NULL,
    `page_id` bigint(20) unsigned DEFAULT NULL,
    PRIMARY KEY (`page_row_id`),
    KEY `PAGE_ROW_FK01` (`page_id`),
    CONSTRAINT `PAGE_ROW_FK01` FOREIGN KEY (`page_id`) REFERENCES `page` (`page_id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `page_variable` (
    `variable_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
    `create_time` timestamp NOT NULL DEFAULT current_timestamp(),
    `update_time` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
    `api_query` varchar(1000) NOT NULL,
    `edge_fields` varchar(100) DEFAULT NULL,
    `hidden_yn` varchar(1) NOT NULL,
    `job_name` varchar(50) NOT NULL,
    `name` varchar(100) NOT NULL,
    `query_type` varchar(20) NOT NULL,
    `refresh_interval` varchar(3) NOT NULL,
    `sort_order` bigint(20) unsigned NOT NULL DEFAULT 0,
    `variable_type` varchar(20) NOT NULL,
    `page_id` bigint(20) unsigned DEFAULT NULL,
    PRIMARY KEY (`variable_id`),
    KEY `PAGE_VARIABLE_FK01` (`page_id`),
    CONSTRAINT `PAGE_VARIABLE_FK01` FOREIGN KEY (`page_id`) REFERENCES `page` (`page_id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `page_row_panel` (
    `panel_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
    `create_time` timestamp NOT NULL DEFAULT current_timestamp(),
    `update_time` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
    `chart_type` varchar(20) NOT NULL,
    `fragment_name` varchar(50) DEFAULT NULL,
    `legend_visible` tinyint(1) NOT NULL DEFAULT 1,
    `main_yn` varchar(1) NOT NULL DEFAULT 'Y',
    `panel_type` varchar(20) NOT NULL,
    `refresh_interval` bigint(20) unsigned NOT NULL DEFAULT 60000,
    `repeat_variable` varchar(50) DEFAULT NULL,
    `sort_order` bigint(20) unsigned NOT NULL DEFAULT 0,
    `title` varchar(200) NOT NULL,
    `xaxis_mode` varchar(20) NOT NULL,
    `yaxis_label` varchar(50) NOT NULL,
    `yaxis_max` varchar(10) NOT NULL,
    `yaxis_min` varchar(10) NOT NULL,
    `yaxis_unit` varchar(20) NOT NULL,
    `page_row_id` bigint(20) unsigned DEFAULT NULL,
    PRIMARY KEY (`panel_id`),
    KEY `PAGE_ROW_PANEL_FK01` (`page_row_id`),
    CONSTRAINT `PAGE_ROW_PANEL_FK01` FOREIGN KEY (`page_row_id`) REFERENCES `page_row` (`page_row_id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `chart_query` (
    `c_query_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
    `create_time` timestamp NOT NULL DEFAULT current_timestamp(),
    `update_time` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
    `api_query` varchar(1000) NOT NULL,
    `legend` varchar(100) NOT NULL,
    `query_step` varchar(3) NOT NULL,
    `query_type` varchar(20) NOT NULL DEFAULT 'PROXY_METRIC',
    `panel_id` bigint(20) unsigned DEFAULT NULL,
    PRIMARY KEY (`c_query_id`),
    KEY `CHART_QUERY_FK01` (`panel_id`),
    CONSTRAINT `CHART_QUERY_FK01` FOREIGN KEY (`panel_id`) REFERENCES `page_row_panel` (`panel_id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
