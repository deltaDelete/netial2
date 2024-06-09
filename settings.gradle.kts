rootProject.name = "ru.deltadelete.netial"

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

include("server")
project(":server").apply {
    name = "netial-server"
}
include(":client")