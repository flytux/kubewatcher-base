# Kube-Watcher

### Cloud Native Cluster Management Application

- default profile (Inmemory H2 DB)
  - DDL & DML operation executed by default
- local-m profile (Mysql DB)
  - DDL & DML operation executed by default
  - ${MYSQL_DB_PATH} (default 127.0.0.1:3306/kubeworksdb)
  - ${MYSQL_DB_USERNAME} (default root), ${MYSQL_DB_PASSWORD} (default root)
- local-m-remote profile (Mysql DB)
  - ${MYSQL_DB_PATH} (default 127.0.0.1:3306/kubeworksdb)
  - ${MYSQL_DB_USERNAME} (default root), ${MYSQL_DB_PASSWORD} (default root)
