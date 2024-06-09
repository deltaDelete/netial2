# Netial

[![Build artifact](https://github.com/deltaDelete/netial2/actions/workflows/buildArtifact.yaml/badge.svg)](https://github.com/deltaDelete/netial2/actions/workflows/buildArtifact.yaml)

## Getting started

### Prerequisites

- Node.js 18 or later
- JDK 17

> Specify backend url in `client/.env`
> ```env
> VITE_BACK_DOMAIN=https://example.com
> ```

### Building

1. Run gradle task customDistZip
    ```shell
    $ ./gradlew customDistZip
    ```
2. Take the compiled distribution from `server/build/distributions`

### Configuring

#### Runtime configuration

Configure following things in `config.json`

1. secret — string used for generating email confirmation codes
2. database
    1. url — JDBC connection string
    2. driver — for now only `org.postgresql.Driver` is supported
    3. user — username used to connect to the database
    4. password — password used to connect to the database
3. [jwt](https://jwt.io/)
4. email
    1. host — domain name of smtp server
    2. port — port of smtp server
    3. login — email used for outgoing messages
    4. password - smtp server password
5. templates
    1. emailConfirmationTemplate — html template for an email confirmation message, supported
       variables: `$name$`, `$username$`, `$confirmation$`, `$userId$`, `$email$`
6. storage
    1. attachments — directory where uploaded attachments will be stored
    2. wwwRoot — directory where SPA is stored

#### Starting configuration

To configure HTTPS, use the following command line arguments:

```
-sslPort=<port>
-sslKeyStore=<path to jks keystore>
-P:ktor.security.ssl.keyAlias=<alias in jks keystore>
-P:ktor.security.ssl.keyStorePassword=<keystore password>
-P:ktor.security.ssl.privateKeyPassword=<private key password>
```

### Testing

Use Gradle Task `test`

```shell
$ ./gradlew test
```