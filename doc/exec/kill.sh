#!/bin/sh

set -xe

kill -15 $(cat ${TEST_HOME}/kube-watcher.pid)
