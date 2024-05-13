import com.github.gradle.node.npm.task.NpmTask

plugins {
    alias(libs.plugins.node.gradle)
}

tasks.register<NpmTask>("npmDev") {
    dependsOn(tasks.npmInstall)
    npmCommand = listOf("run", "dev")
    ignoreExitValue = false
    environment = mapOf()
    workingDir = projectDir
    execOverrides {
        standardOutput = System.out
        standardInput = System.`in`
    }
    inputs.dir("node_modules")
    inputs.file("package.json")
    inputs.file("tsconfig.json")
    inputs.file("vite.config.ts")
    inputs.file("index.html")
    inputs.dir("src")
    outputs.upToDateWhen {
        true
    }
}

tasks.register<NpmTask>("npmBuild") {
    dependsOn(tasks.npmInstall)
    npmCommand = listOf("run", "build")
    ignoreExitValue = false
    environment = mapOf()
    workingDir = projectDir
    execOverrides {
        standardOutput = System.out
    }
    inputs.dir("node_modules")
    inputs.file("package.json")
    inputs.file("tsconfig.json")
    inputs.file("vite.config.ts")
    inputs.file("index.html")
    inputs.dir("src")
    outputs.dir("dist")
}

tasks.register<Zip>("package") {
    val npmBuild = tasks.getByPath("npmBuild")
    dependsOn(npmBuild)
    archiveFileName = "client.zip"
    destinationDirectory = File(projectDir, "build")
    from(npmBuild)
}