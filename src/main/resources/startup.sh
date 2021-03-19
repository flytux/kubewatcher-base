#!/bin/sh

set -xe

echo "parameter setup"
BASE_HOME=/hli_app/sw/kube-watcher
DEPLOY_HOME=${BASE_HOME}/pkg
DEPLOY_PACKAGE_NAME=kube-watcher-0.0.1-SNAPSHOT.jar
LOGS_HOME=${BASE_HOME}/logs/

PROFILES_ACTIVE=-Dspring.profiles.active=embedded,dev-cluster
LOGGING_FILE_PATH=-Dlogging.file.path=${LOGS_HOME}
DB_OPT="-Dspring.datasource.url=jdbc:h2:/hli_app/sw/kube-watcher/data/kube-watcher;MODE=MySQL"
#THYMELEAF_CACHE=-Dspring.thymeleaf.cache=true

echo "service start"
JVM_OPTS="${JVM_OPTS} -server"
JVM_OPTS="${JVM_OPTS} -Xms1024m"
JVM_OPTS="${JVM_OPTS} -Xmx1024m"
JVM_OPTS="${JVM_OPTS} -XX:+UseG1GC"
JVM_OPTS="${JVM_OPTS} -Dfile.encoding=UTF-8"
JVM_OPTS="${JVM_OPTS} -Dsun.net.inetaddr.ttl=0"
JVM_OPTS="${JVM_OPTS} -verbose:gc"
JVM_OPTS="${JVM_OPTS} -XX:+HeapDumpOnOutOfMemoryError"
JVM_OPTS="${JVM_OPTS} -XX:+PrintGCDateStamps"
JVM_OPTS="${JVM_OPTS} -XX:+PrintHeapAtGC"
JVM_OPTS="${JVM_OPTS} -XX:+PrintGCTimeStamps"
JVM_OPTS="${JVM_OPTS} -XX:HeapDumpPath=${LOGS_HOME}/dump"
JVM_OPTS="${JVM_OPTS} -Xloggc:${LOGS_HOME}/gc/gc.log"


nohup /hli_app/sw/kube-watcher/jdk1.8.0_261/bin/java -jar ${JVM_OPTS} -Dspring.jpa.hibernate.ddl-auto=create-drop ${PROFILES_ACTIVE} ${DB_OPT}  ${LOGGING_FILE_PATH} ${DEPLOY_HOME}/${DEPLOY_PACKAGE_NAME} 1>/dev/null 2>&1 &
#/hli_app/sw/kube-watcher/jdk1.8.0_261/bin/java -jar ${JVM_OPTS} ${PROFILES_ACTIVE} ${LOGGING_FILE_PATH} ${DEPLOY_HOME}/${DEPLOY_PACKAGE_NAME}
