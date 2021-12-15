#!/usr/bin/env bash
CLIENT_DIR=spring-retrosocket-samples/hello-client/
./install.sh &&  mvn -f $CLIENT_DIR/pom.xml -DskipTests=true -Pnative  clean package && $CLIENT_DIR/target/hello-client
