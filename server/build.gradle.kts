plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ktor)
}

group = "ru.deltadelete.netial"
version = "0.0.1"

application {
    mainClass = "ru.deltadelete.netial.ApplicationKt"

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

dependencies {
    implementation(libs.ktor.server.core.jvm)
    implementation(libs.ktor.server.openapi)
    implementation(libs.ktor.server.swagger.jvm)
    implementation(libs.ktor.server.call.logging.jvm)
    implementation(libs.ktor.server.content.negotiation.jvm)
    implementation(libs.ktor.serialization.jackson.jvm)
    implementation(libs.ktor.server.websockets.jvm)
    implementation(libs.ktor.server.cors)
    implementation(libs.ktor.server.default.headers)
    implementation(libs.ktor.network.tls.certificates)
    implementation(libs.ktor.server.host.common.jvm)
    implementation(libs.ktor.server.status.pages.jvm)
    implementation(libs.ktor.server.auth.jvm)
    implementation(libs.ktor.server.auth.jwt.jvm)
    implementation(libs.ktor.serialization.kotlinx.json.jvm)
    implementation(libs.ktor.server.call.id.jvm)
    implementation(libs.ktor.server.netty.jvm)
    implementation(libs.exposed.core)
    implementation(libs.exposed.jdbc)
    implementation(libs.exposed.dao)
    implementation(libs.exposed.kotlin.datetime)
    implementation(libs.h2)
    implementation(libs.postgresql)
    implementation(libs.jbcrypt)
    implementation(libs.logback.classic)
    implementation(libs.jakarta.mail)

    testImplementation(libs.ktor.server.tests)
    testImplementation(libs.kotlin.test.junit)
    testImplementation(libs.ktor.server.test.host.jvm)
    testImplementation(libs.ktor.client.content.negotiation.jvm)
    testImplementation(libs.kotlinx.coroutines.test)
}

distributions {
    create("custom") {
        val jarTask = tasks.getByPath(":netial-server:shadowJar")
        val clientBuild = tasks.getByPath(":client:pnpmBuild")
        distributionBaseName = rootProject.name
        contents {
            from(jarTask.outputs.files) {
                into("")
            }
            from(project.files("start.sh", "start.bat")) {
                into("")
            }
            from(rootProject.file("config.example.json")) {
                rename("config.example.json", "config.json")
                into("")
            }
            from(rootProject.file("docs/confirmation.html")) {
                into("templates")
            }
            from(clientBuild.outputs.files){
                into("wwwroot")
            }
        }
    }
}

tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = application.mainClass
    }
}

// Resolve duplicates
tasks.withType<AbstractCopyTask> {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}