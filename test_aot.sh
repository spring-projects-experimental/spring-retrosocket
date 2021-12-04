#!/usr/bin/env bash
CLIENT_DIR=spring-retrosocket-samples/hello-client/
./install.sh &&  mvn   -f $CLIENT_DIR/pom.xml -DskipTests=true spring-aot:generate  2>&1 | tee  $HOME/Desktop/aot.logs
