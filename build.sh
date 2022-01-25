#!/bin/sh

set -xe

if [ "docker" = "$1" ]; then
    ./mvnw clean compile jib:build -DskipTests=true -DsendCredentialsOverHttp=true
else
    ./mvnw clean package
fi