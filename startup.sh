#!/bin/sh

set -xe

export MYSQL_DB_PATH=127.0.0.1:3306/kubeworks
export H2_DB_PATH=${TEST_HOME}/data/kubeworksdb

BASE_HOME=${TEST_HOME}
LOGS_HOME=${BASE_HOME}/logs/
DEPLOY_HOME=${BASE_HOME}/pkg
PACKAGE_NAME=kube-watcher-0.1.0-minimum.jar

LOGGING_FILE_PATH=--logging.file.path=${LOGS_HOME}
PROFILES_ACTIVE=--spring.profiles.active=local-h,logging
PID_FILE_PATH=--spring.pid.file=${BASE_HOME}/kube-watcher.pid

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
JVM_OPTS="${JVM_OPTS} -XX:HeapDumpPath=${LOGS_HOME}dump"
JVM_OPTS="${JVM_OPTS} -Xloggc:${LOGS_HOME}gc/gc.log"

nohup java -jar ${JVM_OPTS} ${DEPLOY_HOME}/${PACKAGE_NAME} ${PROFILES_ACTIVE} ${LOGGING_FILE_PATH} ${PID_FILE_PATH} 1>/dev/null 2>&1 &
