import com.github.gradle.node.pnpm.task.PnpmTask

plugins {
    alias(libs.plugins.node.gradle)
}

node {
    download = true
    version = "18.20.3"
    pnpmVersion = "9.2.0"
}

tasks.register<PnpmTask>("pnpmDev") {
    dependsOn(tasks.pnpmInstall)
    pnpmDev()
}

tasks.register<PnpmTask>("pnpmBuild") {
    dependsOn(tasks.pnpmInstall)
    pnpmBuild()
}

tasks.register<Zip>("package") {
    val pnpmBuild = tasks.getByPath("pnpmBuild")
    dependsOn(pnpmBuild)
    archiveFileName = "client.zip"
    destinationDirectory = File(projectDir, "build")
    from(pnpmBuild)
}

tasks.register<Delete>("clean") {
    delete("build", "dist")
}

fun PnpmTask.pnpmDev() {
    pnpmCommand = listOf("run", "dev")
    ignoreExitValue = false
    environment = mapOf()
    workingDir = projectDir
    execOverrides {
        standardOutput = System.out
        standardInput = System.`in`
    }
    inputs.dir("node_modules")
    inputs.file("package.json")
    inputs.file("pnpm-lock.yaml")
    inputs.file("tsconfig.json")
    inputs.file("vite.config.ts")
    inputs.file("index.html")
    inputs.dir("src")
    outputs.upToDateWhen {
        true
    }
}

fun PnpmTask.pnpmBuild() {
    pnpmCommand = listOf("run", "build")
    ignoreExitValue = false
    environment = mapOf()
    workingDir = projectDir
    execOverrides {
        standardOutput = System.out
    }
    inputs.dir("node_modules")
    inputs.file("package.json")
    inputs.file("pnpm-lock.yaml")
    inputs.file("tsconfig.json")
    inputs.file("vite.config.ts")
    inputs.file("index.html")
    inputs.dir("src")
    outputs.dir("dist")
}