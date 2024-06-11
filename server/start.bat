@echo off

set "JAVA_EXECUTABLE=java"
set "JAR_FILE=netial-server-all.jar"
set "SSL_PORT=8443"
set "SSL_KEYSTORE="
set "KEY_ALIAS="
set "KEYSTORE_PASSWORD="
set "PRIVATE_KEY_PASSWORD="

"%JAVA_EXECUTABLE%" -jar "%JAR_FILE%" -- ^
  -sslPort=%SSL_PORT% ^
  -sslKeyStore="%SSL_KEYSTORE%" ^
  -P:ktor.security.ssl.keyAlias=%KEY_ALIAS% ^
  -P:ktor.security.ssl.keyStorePassword=%KEYSTORE_PASSWORD% ^
  -P:ktor.security.ssl.privateKeyPassword=%PRIVATE_KEY_PASSWORD% ^
  -P:Dio.ktor.development=false