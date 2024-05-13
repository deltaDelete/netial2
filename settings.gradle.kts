rootProject.name = "ru.deltadelete.netial"

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
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