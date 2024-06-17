import com.github.gradle.node.pnpm.task.PnpmTask
import java.io.FileWriter

plugins {
    alias(libs.plugins.node.gradle)
}

node {
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

tasks.register<DefaultTask>("composeSourceCode") {
    val appendToFile = project.layout.buildDirectory.file("generated/sourceCode")
    val appendTo = appendToFile.get().asFile
    appendTo.parentFile.mkdirs()
    appendTo.createNewFile()
    FileWriter(appendTo, Charsets.UTF_8, true).use {
        val files = project.fileTree("./src").files
        files.forEach { file ->
            println("Processing file ${file.path}")
            it.appendLine("// ${file.relativeTo(project.projectDir.absoluteFile).path}")
            val content = file.readText(Charsets.UTF_8)
            it.appendLine(content)
        }
    }
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
