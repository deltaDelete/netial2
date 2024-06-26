#!/usr/bin/env kotlin

@file:DependsOn("io.github.typesafegithub:github-workflows-kt:1.8.0")

import io.github.typesafegithub.workflows.actions.actions.CheckoutV4
import io.github.typesafegithub.workflows.actions.actions.SetupJavaV4
import io.github.typesafegithub.workflows.actions.actions.SetupNodeV4
import io.github.typesafegithub.workflows.actions.actions.UploadArtifactV4
import io.github.typesafegithub.workflows.domain.Mode
import io.github.typesafegithub.workflows.domain.Permission
import io.github.typesafegithub.workflows.domain.RunnerType
import io.github.typesafegithub.workflows.domain.actions.Action
import io.github.typesafegithub.workflows.domain.actions.RegularAction
import io.github.typesafegithub.workflows.domain.triggers.Push
import io.github.typesafegithub.workflows.dsl.workflow
import io.github.typesafegithub.workflows.yaml.writeToFile


workflow(
    name = "Build artifact",
    on = listOf(Push(paths = listOf(
        "./server",
        "./client",
        "./build.gradle.kts",
        "./settings.gradle.kts",
        "./gradle.properties",
        "./gradle",
        "./.github/workflows"
    ))),
    sourceFile = __FILE__.toPath(),
) {
    job(
        id = "build", runsOn = RunnerType.UbuntuLatest,
        permissions = mapOf(Permission.Contents to Mode.Read)
    ) {
        uses(name = "Check out", action = CheckoutV4())
        uses(
            name = "Setup Java",
            action = SetupJavaV4(
                javaVersion = "17",
                distribution = SetupJavaV4.Distribution.Temurin
            )
        )
        uses(
            name = "Setup pnpm",
            action = SetupPnpmV4("9")
        )
        uses(
            name = "Setup Node",
            action = SetupNodeV4(
                nodeVersion = "18",
                cache = SetupNodeV4.PackageManager.Pnpm,
                cacheDependencyPath = listOf("./client/pnpm-lock.yaml")
            )
        )
        run(
            name = "Build distribution",
            command = "./gradlew clean customDistZip"
        )
        uses(
            name = "Upload distribution",
            action = UploadArtifactV4(
                name = "distribution",
                path = listOf("server/build/distributions/ru.deltadelete.netial-0.0.1.zip"),
                ifNoFilesFound = UploadArtifactV4.BehaviorIfNoFilesFound.Error
            )
        )
    }
    job(
        id = "dependency_submission", runsOn = RunnerType.UbuntuLatest,
        permissions = mapOf(Permission.Contents to Mode.Write)
    ) {
        uses(name = "Check out", action = CheckoutV4())
        uses(
            name = "Setup Java",
            action = SetupJavaV4(
                javaVersion = "17",
                distribution = SetupJavaV4.Distribution.Temurin
            )
        )
        uses(
            name = "Setup pnpm",
            action = SetupPnpmV4("9")
        )
        uses(
            name = "Setup Node",
            action = SetupNodeV4(
                nodeVersion = "18",
                cache = SetupNodeV4.PackageManager.Pnpm,
                cacheDependencyPath = listOf("./client/pnpm-lock.yaml")
            )
        )
        uses(
            name = "Setup Gradle",
            action = GradleDependencySubmissionV3()
        )
    }
}.writeToFile()

class GradleDependencySubmissionV3() : RegularAction<Action.Outputs>("gradle", "actions/dependency-submission", "v3") {
    override fun buildOutputObject(stepId: String): Action.Outputs = Action.Outputs(stepId)

    override fun toYamlArguments(): LinkedHashMap<String, String> = linkedMapOf()
}

class SetupPnpmV4(
    private val version: String
) : RegularAction<Action.Outputs>("pnpm", "action-setup", "v4") {
    override fun buildOutputObject(stepId: String): Action.Outputs = Action.Outputs(stepId)

    override fun toYamlArguments(): LinkedHashMap<String, String> = linkedMapOf("version" to version)
}