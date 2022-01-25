#!/bin/sh

set -xe

export MYSQL_DB_PATH=127.0.0.1:3306/kubeworksdb

BASE_HOME=${TEST_HOME:-${HOME}}
LOGS_HOME=${BASE_HOME}/logs/
DEPLOY_HOME=${BASE_HOME}/pkg
PACKAGE_NAME=kube-watcher-0.1.0.jar

LOGGING_FILE_PATH=--logging.file.path=${LOGS_HOME}
PROFILES_ACTIVE=--spring.profiles.active=local-h,logging
PID_FILE_PATH=--spring.pid.file=${BASE_HOME}/kube-watcher.pid

JAVA_OPTS="${JAVA_OPTS} -ea -server"
JAVA_OPTS="${JAVA_OPTS} -Xms1024m -Xmx1024m"
JAVA_OPTS="${JAVA_OPTS} -verbose:gc -XX:+UseG1GC"
JAVA_OPTS="${JAVA_OPTS} -XX:+PrintGCDateStamps -XX:+PrintGCTimeStamps"
JAVA_OPTS="${JAVA_OPTS} -XX:+PrintHeapAtGC -Xloggc:${LOGS_HOME}gc/gc.log"
JAVA_OPTS="${JAVA_OPTS} -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=${LOGS_HOME}dump"
JAVA_OPTS="${JAVA_OPTS} -Dfile.encoding=UTF-8 -Dsun.net.inetaddr.ttl=0"

nohup java -jar ${JAVA_OPTS} ${DEPLOY_HOME}/${PACKAGE_NAME} ${PROFILES_ACTIVE} ${LOGGING_FILE_PATH} ${PID_FILE_PATH} 1>/dev/null 2>&1 &
