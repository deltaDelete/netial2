#!/usr/bin/env bash

JAVA_EXECUTABLE=java
JAR_FILE=netial-server-all.jar
SSL_PORT=8443
SSL_KEYSTORE=
KEY_ALIAS=
KEYSTORE_PASSWORD=
PRIVATE_KEY_PASSWORD=

$JAVA_EXECUTABLE -jar $JAR_FILE -- \
  -sslPort=$SSL_PORT \
  -sslKeyStore=$SSL_KEYSTORE \
  -P:ktor.security.ssl.keyAlias=$KEY_ALIAS \
  -P:ktor.security.ssl.keyStorePassword=$KEYSTORE_PASSWORD \
  -P:ktor.security.ssl.privateKeyPassword=$PRIVATE_KEY_PASSWORD \
  -P:Dio.ktor.development=false