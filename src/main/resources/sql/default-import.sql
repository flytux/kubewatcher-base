INSERT INTO chart_query (C_QUERY_ID, API_QUERY, QUERY_TYPE, LEGEND, QUERY_STEP, CREATE_TIME, UPDATE_TIME, PANEL_ID) values (	85	,	 '/api/v1/query_range?query=avg(jvm_memory_pool_bytes_max{pool="Eden Space", job="jmx-metrics", application=~"^.*$", instance=~"^.*$"}) by (application)'	,	 'PROXY_METRIC'	,	 'Max {{application}}'	,	 '15'	,	CURRENT_TIMESTAMP()	,	CURRENT_TIMESTAMP()	,	35	);
INSERT INTO chart_query (C_QUERY_ID, API_QUERY, QUERY_TYPE, LEGEND, QUERY_STEP, CREATE_TIME, UPDATE_TIME, PANEL_ID) values (	86	,	 '/api/v1/query_range?query=avg(jvm_memory_pool_bytes_used{pool="Eden Space",job="jmx-metrics", application=~"^.*$", instance=~"^.*$"}) by (application)'	,	 'PROXY_METRIC'	,	 'Used {{application}}'	,	 '15'	,	CURRENT_TIMESTAMP()	,	CURRENT_TIMESTAMP()	,	35	);
INSERT INTO chart_query (C_QUERY_ID, API_QUERY, QUERY_TYPE, LEGEND, QUERY_STEP, CREATE_TIME, UPDATE_TIME, PANEL_ID) values (	87	,	 '/api/v1/query_range?query=avg(jvm_memory_pool_bytes_committed{pool="Eden Space",job="jmx-metrics", application=~"^.*$", instance=~"^.*$"}) by (application)'	,	 'PROXY_METRIC'	,	 'Commited {{application}}'	,	 '15'	,	CURRENT_TIMESTAMP()	,	CURRENT_TIMESTAMP()	,	35	);
INSERT INTO chart_query (C_QUERY_ID, API_QUERY, QUERY_TYPE, LEGEND, QUERY_STEP, CREATE_TIME, UPDATE_TIME, PANEL_ID) values (	88	,	 '/api/v1/query_range?query=avg(jvm_memory_pool_bytes_max{pool="Tenured Gen", job="jmx-metrics", application=~"^.*$", instance=~"^.*$"}) by (application)'	,	 'PROXY_METRIC'	,	 'Max {{application}}'	,	 '15'	,	CURRENT_TIMESTAMP()	,	CURRENT_TIMESTAMP()	,	36	);
INSERT INTO chart_query (C_QUERY_ID, API_QUERY, QUERY_TYPE, LEGEND, QUERY_STEP, CREATE_TIME, UPDATE_TIME, PANEL_ID) values (	89	,	 '/api/v1/query_range?query=avg(jvm_memory_pool_bytes_used{pool="Tenured Gen",job="jmx-metrics", application=~"^.*$", instance=~"^.*$"}) by (application)'	,	 'PROXY_METRIC'	,	 'Used {{application}}'	,	 '15'	,	CURRENT_TIMESTAMP()	,	CURRENT_TIMESTAMP()	,	36	);
INSERT INTO chart_query (C_QUERY_ID, API_QUERY, QUERY_TYPE, LEGEND, QUERY_STEP, CREATE_TIME, UPDATE_TIME, PANEL_ID) values (	90	,	 '/api/v1/query_range?query=avg(jvm_memory_pool_bytes_committed{pool="Tenured Gen",job="jmx-metrics", application=~"^.*$", instance=~"^.*$"}) by (application)'	,	 'PROXY_METRIC'	,	 'Commited {{application}}'	,	 '15'	,	CURRENT_TIMESTAMP()	,	CURRENT_TIMESTAMP()	,	36	);
INSERT INTO chart_query (C_QUERY_ID, API_QUERY, QUERY_TYPE, LEGEND, QUERY_STEP, CREATE_TIME, UPDATE_TIME, PANEL_ID) values (	103	,	 '/api/v1/query_range?query=sum(tomcat_requestprocessor_requestcount{application="$application", instance=~"$instance"})'	,	 'PROXY_METRIC'	,	 'Request'	,	 '15'	,	CURRENT_TIMESTAMP()	,	CURRENT_TIMESTAMP()	,	46	);
INSERT INTO chart_query (C_QUERY_ID, API_QUERY, QUERY_TYPE, LEGEND, QUERY_STEP, CREATE_TIME, UPDATE_TIME, PANEL_ID) values (	110	,	 '/api/v1/query_range?query=jvm_memory_pool_bytes_max{pool="Eden Space",application=~"$application", instance=~"$instance"}'	,	 'PROXY_METRIC'	,	 'Max'	,	 '15'	,	CURRENT_TIMESTAMP()	,	CURRENT_TIMESTAMP()	,	49	);
INSERT INTO chart_query (C_QUERY_ID, API_QUERY, QUERY_TYPE, LEGEND, QUERY_STEP, CREATE_TIME, UPDATE_TIME, PANEL_ID) values (	111	,	 '/api/v1/query_range?query=jvm_memory_pool_bytes_used{pool="Eden Space",application=~"$application", instance=~"$instance"}'	,	 'PROXY_METRIC'	,	 'Used'	,	 '15'	,	CURRENT_TIMESTAMP()	,	CURRENT_TIMESTAMP()	,	49	);
INSERT INTO chart_query (C_QUERY_ID, API_QUERY, QUERY_TYPE, LEGEND, QUERY_STEP, CREATE_TIME, UPDATE_TIME, PANEL_ID) values (	112	,	 '/api/v1/query_range?query=jvm_memory_pool_bytes_committed{pool="Eden Space",application=~"$application", instance=~"$instance"}'	,	 'PROXY_METRIC'	,	 'Commited'	,	 '15'	,	CURRENT_TIMESTAMP()	,	CURRENT_TIMESTAMP()	,	49	);
INSERT INTO chart_query (C_QUERY_ID, API_QUERY, QUERY_TYPE, LEGEND, QUERY_STEP, CREATE_TIME, UPDATE_TIME, PANEL_ID) values (	113	,	 '/api/v1/query_range?query=jvm_memory_pool_bytes_max{pool="Tenured Gen",application=~"$application", instance=~"$instance"}'	,	 'PROXY_METRIC'	,	 'Max'	,	 '15'	,	CURRENT_TIMESTAMP()	,	CURRENT_TIMESTAMP()	,	50	);
INSERT INTO chart_query (C_QUERY_ID, API_QUERY, QUERY_TYPE, LEGEND, QUERY_STEP, CREATE_TIME, UPDATE_TIME, PANEL_ID) values (	114	,	 '/api/v1/query_range?query=jvm_memory_pool_bytes_used{pool="Tenured Gen",application=~"$application", instance=~"$instance"}'	,	 'PROXY_METRIC'	,	 'Used'	,	 '15'	,	CURRENT_TIMESTAMP()	,	CURRENT_TIMESTAMP()	,	50	);
INSERT INTO chart_query (C_QUERY_ID, API_QUERY, QUERY_TYPE, LEGEND, QUERY_STEP, CREATE_TIME, UPDATE_TIME, PANEL_ID) values (	115	,	 '/api/v1/query_range?query=jvm_memory_pool_bytes_committed{pool="Tenured Gen", application=~"$application", instance=~"$instance"}'	,	 'PROXY_METRIC'	,	 'Commited'	,	 '15'	,	CURRENT_TIMESTAMP()	,	CURRENT_TIMESTAMP()	,	50	);
INSERT INTO chart_query (C_QUERY_ID, API_QUERY, QUERY_TYPE, LEGEND, QUERY_STEP, CREATE_TIME, UPDATE_TIME, PANEL_ID) values (	260	,	 '/api/v1/query?query=label_replace(count(kube_deployment_created{deployment=~"$services"}) by (deployment), "application", "$1", "deployment", "(.*)(-deploy)?") * on (application) group_left(namespace) count(jvm_info) by (namespace, application) or on (application) label_replace(count(kube_deployment_created{deployment=~"$services"}) by (deployment), "application", "$1", "deployment", "(.*)(-deploy)?") * -1'	,	 'PROXY_METRIC'	,	 '{{application}}'	,	 '60'	,	CURRENT_TIMESTAMP()	,	CURRENT_TIMESTAMP()	,	138	);
INSERT INTO chart_query (C_QUERY_ID, API_QUERY, QUERY_TYPE, LEGEND, QUERY_STEP, CREATE_TIME, UPDATE_TIME, PANEL_ID) values (	261	,	 '/api/v1/query?query=avg((increase(tomcat_requestprocessor_processingtime{job="jmx-metrics", application=~"$services"}[1m]) / increase(tomcat_requestprocessor_requestcount{job="jmx-metrics", application=~"$services"}[1m])) > 0) by (application)'	,	 'PROXY_METRIC'	,	 'responsetime-{{application}}'	,	 '60'	,	CURRENT_TIMESTAMP()	,	CURRENT_TIMESTAMP()	,	138	);
INSERT INTO chart_query (C_QUERY_ID, API_QUERY, QUERY_TYPE, LEGEND, QUERY_STEP, CREATE_TIME, UPDATE_TIME, PANEL_ID) values (	262	,	 '/api/v1/query_range?query=(sum(tomcat_requestprocessor_requestcount{job="jmx-metrics",application=~".*"}) - sum(tomcat_requestprocessor_errorcount{job="jmx-metrics",application=~".*"}))/sum(tomcat_requestprocessor_requestcount{job="jmx-metrics",application=~".*"})*100'	,	 'PROXY_METRIC'	,	 ''	,	 '60'	,	CURRENT_TIMESTAMP()	,	CURRENT_TIMESTAMP()	,	139	);
INSERT INTO chart_query (C_QUERY_ID, API_QUERY, QUERY_TYPE, LEGEND, QUERY_STEP, CREATE_TIME, UPDATE_TIME, PANEL_ID) values (	263	,	 '/api/v1/query_range?query=avg(sum(increase(tomcat_requestprocessor_processingtime{job="jmx-metrics", application=~".*"}[1m]))/sum(increase(tomcat_requestprocessor_requestcount{job="jmx-metrics", application=~".*"}[1m])))'	,	 'PROXY_METRIC'	,	 ''	,	 '60'	,	CURRENT_TIMESTAMP()	,	CURRENT_TIMESTAMP()	,	140	);
INSERT INTO chart_query (C_QUERY_ID, API_QUERY, QUERY_TYPE, LEGEND, QUERY_STEP, CREATE_TIME, UPDATE_TIME, PANEL_ID) values (	264	,	 '/api/v1/query_range?query=sum(tomcat_requestprocessor_requestcount{job="jmx-metrics",application=~".*"})'	,	 'PROXY_METRIC'	,	 ''	,	 '60'	,	CURRENT_TIMESTAMP()	,	CURRENT_TIMESTAMP()	,	141	);
INSERT INTO chart_query (C_QUERY_ID, API_QUERY, QUERY_TYPE, LEGEND, QUERY_STEP, CREATE_TIME, UPDATE_TIME, PANEL_ID) values (	272	,	 '/api/v1/query?query=label_replace(count(kube_deployment_created{deployment=~"$services"}) by (deployment), "application", "$1", "deployment", "(.*)(-deploy)?") * on (application) group_left(namespace) count(jvm_info) by (namespace, application) or on (application) label_replace(count(kube_deployment_created{deployment=~"$services"}) by (deployment), "application", "$1", "deployment", "(.*)(-deploy)?") * -1'	,	 'PROXY_METRIC'	,	 '{{application}}'	,	 '60'	,	CURRENT_TIMESTAMP()	,	CURRENT_TIMESTAMP()	,	147	);
INSERT INTO chart_query (C_QUERY_ID, API_QUERY, QUERY_TYPE, LEGEND, QUERY_STEP, CREATE_TIME, UPDATE_TIME, PANEL_ID) values (	273	,	 '/api/v1/query?query=avg((increase(tomcat_requestprocessor_processingtime{job="jmx-metrics", application=~"$services"}[1m]) / increase(tomcat_requestprocessor_requestcount{job="jmx-metrics", application=~"$services"}[1m])) > 0) by (application)'	,	 'PROXY_METRIC'	,	 'responsetime-{{application}}'	,	 '60'	,	CURRENT_TIMESTAMP()	,	CURRENT_TIMESTAMP()	,	147	);
INSERT INTO chart_query (C_QUERY_ID, API_QUERY, QUERY_TYPE, LEGEND, QUERY_STEP, CREATE_TIME, UPDATE_TIME, PANEL_ID) values (	274	,	 '/api/v1/query_range?query=(sum(tomcat_requestprocessor_requestcount{job="jmx-metrics",application=~".*"}) - sum(tomcat_requestprocessor_errorcount{job="jmx-metrics",application=~".*"}))/sum(tomcat_requestprocessor_requestcount{job="jmx-metrics",application=~".*"})*100'	,	 'PROXY_METRIC'	,	 'Transaction'	,	 '60'	,	CURRENT_TIMESTAMP()	,	CURRENT_TIMESTAMP()	,	148	);
INSERT INTO chart_query (C_QUERY_ID, API_QUERY, QUERY_TYPE, LEGEND, QUERY_STEP, CREATE_TIME, UPDATE_TIME, PANEL_ID) values (	275	,	 '/api/v1/query_range?query=avg(sum(increase(tomcat_requestprocessor_processingtime{job="jmx-metrics", application=~".*"}[1m]))/sum(increase(tomcat_requestprocessor_requestcount{job="jmx-metrics", application=~".*"}[1m])))'	,	 'PROXY_METRIC'	,	 'Avg. Response time'	,	 '60'	,	CURRENT_TIMESTAMP()	,	CURRENT_TIMESTAMP()	,	149	);
INSERT INTO chart_query (C_QUERY_ID, API_QUERY, QUERY_TYPE, LEGEND, QUERY_STEP, CREATE_TIME, UPDATE_TIME, PANEL_ID) values (	276	,	 '/api/v1/query_range?query=sum(tomcat_requestprocessor_requestcount{job="jmx-metrics",application=~".*"})'	,	 'PROXY_METRIC'	,	 'Request count'	,	 '60'	,	CURRENT_TIMESTAMP()	,	CURRENT_TIMESTAMP()	,	150	);
INSERT INTO chart_query (C_QUERY_ID, API_QUERY, QUERY_TYPE, LEGEND, QUERY_STEP, CREATE_TIME, UPDATE_TIME, PANEL_ID) values	(	279	,	 '/api/v1/query?query=sum(tomcat_requestprocessor_requestcount{job="jmx-metrics",application=~"$application"})'	,	 'PROXY_METRIC'	,	 ''	,	 '60'	,	CURRENT_TIMESTAMP()	,	CURRENT_TIMESTAMP()	,	154	);
INSERT INTO chart_query (C_QUERY_ID, API_QUERY, QUERY_TYPE, LEGEND, QUERY_STEP, CREATE_TIME, UPDATE_TIME, PANEL_ID) values	(	283	,	 '/api/v1/query_range?query=sum(increase(tomcat_requestprocessor_processingtime{job="jmx-metrics", application=~"$application"}[1m]))/sum(increase(tomcat_requestprocessor_requestcount{job="jmx-metrics", application=~"$application"}[1m]))'	,	 'PROXY_METRIC'	,	 ''	,	 '60'	,	CURRENT_TIMESTAMP()	,	CURRENT_TIMESTAMP()	,	158	);
INSERT INTO chart_query (C_QUERY_ID, API_QUERY, QUERY_TYPE, LEGEND, QUERY_STEP, CREATE_TIME, UPDATE_TIME, PANEL_ID) values	(	284	,	 '/api/v1/query?query=avg(sum(increase(tomcat_requestprocessor_processingtime{job="jmx-metrics", application=~"$application"}[1m]))/sum(increase(tomcat_requestprocessor_requestcount{job="jmx-metrics", application=~"$application"}[1m])))'	,	 'PROXY_METRIC'	,	 ''	,	 '60'	,	CURRENT_TIMESTAMP()	,	CURRENT_TIMESTAMP()	,	159	);
INSERT INTO chart_query (C_QUERY_ID, API_QUERY, QUERY_TYPE, LEGEND, QUERY_STEP, CREATE_TIME, UPDATE_TIME, PANEL_ID) values	(	285	,	 '/api/v1/query?query=sum(tomcat_requestprocessor_errorcount{job="jmx-metrics",application=~"$application"})'	,	 'PROXY_METRIC'	,	 ''	,	 '60'	,	CURRENT_TIMESTAMP()	,	CURRENT_TIMESTAMP()	,	160	);


INSERT INTO page_variable (VARIABLE_ID, NAME, SORT_ORDER, EDGE_FIELDS, QUERY_TYPE, VARIABLE_TYPE, REFRESH_INTERVAL, HIDDEN_YN, JOB_NAME, API_QUERY,  CREATE_TIME, UPDATE_TIME, PAGE_ID) values (	10	,	 'application'	,	1	,	 ''	,	 'PROXY_METRIC'	,'metric_label_values'	,	 '10s'	,	 'Y'	,	 'jmx-metrics'	,	 'count(jvm_info * on (application) group_left(deployment) label_replace(count(kube_deployment_created{deployment=~"$services"}) by (deployment), "application", "$1", "deployment", "(.*)(-deploy)?")) by (application)'	,	CURRENT_TIMESTAMP()	,	CURRENT_TIMESTAMP()	,	15	);

-- Group, User, Rule, Role
INSERT INTO kw_user_group (GROUPNAME, DESCRIPTION, CREATE_TIME, UPDATE_TIME) values ( 'CLUSTER', '설명1', CURRENT_TIMESTAMP(),   CURRENT_TIMESTAMP());
INSERT INTO kw_user_group (GROUPNAME, DESCRIPTION, CREATE_TIME, UPDATE_TIME) values ( 'NODE', '설명2', CURRENT_TIMESTAMP(),   CURRENT_TIMESTAMP());
INSERT INTO kw_user_group (GROUPNAME, DESCRIPTION, CREATE_TIME, UPDATE_TIME) values ( 'DATABASE', '설명3', CURRENT_TIMESTAMP(),   CURRENT_TIMESTAMP());
INSERT INTO kw_user_group (GROUPNAME, DESCRIPTION, CREATE_TIME, UPDATE_TIME) values ( 'TEST', '테스터 그룹', CURRENT_TIMESTAMP(),   CURRENT_TIMESTAMP());

INSERT INTO kw_user (USERNAME, DEPT, NAME, PASSWORD, USERNO, GROUPNAME, CREATE_TIME, UPDATE_TIME) values ( 'testuser1', 'IT운영부', '김이박', 'password', '1000000', 'CLUSTER', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());
INSERT INTO kw_user (USERNAME, DEPT, NAME, PASSWORD, USERNO, GROUPNAME, CREATE_TIME, UPDATE_TIME) values ( 'testuser2', '개발사업부', '최소형', 'password', '1100000', 'CLUSTER', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());
INSERT INTO kw_user (USERNAME, DEPT, NAME, PASSWORD, USERNO, GROUPNAME, CREATE_TIME, UPDATE_TIME) values ( 'testuser3', 'DEVGRU', '김정남', 'password', '1200000', 'CLUSTER', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());
INSERT INTO kw_user (USERNAME, DEPT, NAME, PASSWORD, USERNO, GROUPNAME, CREATE_TIME, UPDATE_TIME) values ( 'testuser4', 'DEVELOPER', '최대형', 'password', '1300000', 'NODE', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());
INSERT INTO kw_user (USERNAME, DEPT, NAME, PASSWORD, USERNO, GROUPNAME, CREATE_TIME, UPDATE_TIME) values ( 'testuser5', 'IT운영부', '김정은', 'password', '1400000', 'NODE', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());
INSERT INTO kw_user (USERNAME, DEPT, NAME, PASSWORD, USERNO, GROUPNAME, CREATE_TIME, UPDATE_TIME) values ( 'testuser6', 'IT운영부', '트럼프', 'password', '1500000', 'DATABASE', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());
INSERT INTO kw_user (USERNAME, DEPT, NAME, PASSWORD, USERNO, GROUPNAME, CREATE_TIME, UPDATE_TIME) values ( 'testuser7', '개발사업부', '바이든', 'password', '1600000', 'DATABASE', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());
INSERT INTO kw_user (USERNAME, DEPT, NAME, PASSWORD, USERNO, GROUPNAME, CREATE_TIME, UPDATE_TIME) values ( 'test', '큐브웍스넷', '큐브웍스넷', '$2a$10$cwfUecBeTIlPkTMI.UwGY.8VRWMbDXzwzcn0lGipjUqxH9IpIB9NG', '1700000', 'TEST', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());

INSERT INTO kw_user_role_rule (RULE_ID, RULE_NAME, RULE, CREATE_TIME, UPDATE_TIME) values ( 1,  'Operator', '1111111111111111111111111111111111111111111',  CURRENT_TIMESTAMP(),   CURRENT_TIMESTAMP());
INSERT INTO kw_user_role_rule (RULE_ID, RULE_NAME, RULE, CREATE_TIME, UPDATE_TIME) values ( 2,  'Monitor', '1101110111110111110111011110111011000011111',  CURRENT_TIMESTAMP(),   CURRENT_TIMESTAMP());
INSERT INTO kw_user_role_rule (RULE_ID, RULE_NAME, RULE, CREATE_TIME, UPDATE_TIME) values ( 3,  'Manager', '0000011111110000111100111110000110011011001',  CURRENT_TIMESTAMP(),   CURRENT_TIMESTAMP());

INSERT INTO kw_user_role (ROLENAME, DESCRIPTION, USERNAME, RULE_ID, CREATE_TIME, UPDATE_TIME) values ( 'Operator', '설명123abd', 'testuser1', 1, CURRENT_TIMESTAMP(),   CURRENT_TIMESTAMP());
INSERT INTO kw_user_role (ROLENAME, DESCRIPTION, USERNAME, RULE_ID, CREATE_TIME, UPDATE_TIME) values ( 'Operator', '설명123abd', 'testuser2', 1, CURRENT_TIMESTAMP(),   CURRENT_TIMESTAMP());
INSERT INTO kw_user_role (ROLENAME, DESCRIPTION, USERNAME, RULE_ID, CREATE_TIME, UPDATE_TIME) values ( 'Operator', '설명123abd', 'testuser3', 1, CURRENT_TIMESTAMP(),   CURRENT_TIMESTAMP());
INSERT INTO kw_user_role (ROLENAME, DESCRIPTION, USERNAME, RULE_ID, CREATE_TIME, UPDATE_TIME) values ( 'Monitor', '설명123abd', 'testuser4', 2, CURRENT_TIMESTAMP(),   CURRENT_TIMESTAMP());
INSERT INTO kw_user_role (ROLENAME, DESCRIPTION, USERNAME, RULE_ID, CREATE_TIME, UPDATE_TIME) values ( 'Monitor', '설명123abd', 'testuser5', 2, CURRENT_TIMESTAMP(),   CURRENT_TIMESTAMP());
INSERT INTO kw_user_role (ROLENAME, DESCRIPTION, USERNAME, RULE_ID, CREATE_TIME, UPDATE_TIME) values ( 'Monitor', '설명123abd', 'testuser6', 2, CURRENT_TIMESTAMP(),   CURRENT_TIMESTAMP());
INSERT INTO kw_user_role (ROLENAME, DESCRIPTION, USERNAME, RULE_ID, CREATE_TIME, UPDATE_TIME) values ( 'Manager', '설명123abd', 'testuser6', 3, CURRENT_TIMESTAMP(),   CURRENT_TIMESTAMP());
INSERT INTO kw_user_role (ROLENAME, DESCRIPTION, USERNAME, RULE_ID, CREATE_TIME, UPDATE_TIME) values ( 'Manager', '설명123abd', 'test', 3, CURRENT_TIMESTAMP(),   CURRENT_TIMESTAMP());

-- Alert Rule
INSERT INTO kw_alert_rule (RULE_ID, DETECT_TYPE, CATEGORY, RESOURCE,  DETECT_STRING, DANGER_LEVEL, WARNING_LEVEL, DURATION, SEVERITY, CREATE_TIME, UPDATE_TIME) values ( 1, 'metric', 'node', 'cpu', '', 70, 50, 5, 'danger|warning', CURRENT_TIMESTAMP(),   CURRENT_TIMESTAMP());
INSERT INTO kw_alert_rule (RULE_ID, DETECT_TYPE, CATEGORY, RESOURCE,  DETECT_STRING, DANGER_LEVEL, WARNING_LEVEL, DURATION, SEVERITY, CREATE_TIME, UPDATE_TIME) values ( 2, 'log', 'cluster', 'event', 'Liveness probe failed', 0, 0, 0, 'warning', CURRENT_TIMESTAMP(),   CURRENT_TIMESTAMP());