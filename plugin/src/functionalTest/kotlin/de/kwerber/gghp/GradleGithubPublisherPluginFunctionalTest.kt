package de.kwerber.gghp

import java.io.File
import org.gradle.testkit.runner.GradleRunner
import kotlin.test.Test
import kotlin.test.assertTrue

class GradleGithubPublisherPluginFunctionalTest {

    // As of right now, this test does not do much

    @Test fun `can run task`() {
        // Setup the test build
        val projectDir = File("build/functionalTest")
        projectDir.mkdirs()
        projectDir.resolve("settings.gradle.kts").writeText("")
        projectDir.resolve("build.gradle.kts").writeText("""
            plugins {
                `maven-publish`
                id("de.kwerber.ghpub")
            }
            
            publishing {
                publications {
                    create<MavenPublication>("maven") {
                        groupId = "de.kwerber"
                        artifactId = "testArtifact"
                        version = "0.4"
                    }
                }
            }
            
            githubPublish {
                committerName.set("kwerber")
                committerEmail.set("werberkevin@gmail.com")
                githubRepoUrl.set(uri("https://github.com/kwerber/abc_repo.git"))
                pushChanges.set(false)
            }
        """)

        // Run the build
        val runner = GradleRunner.create()
        runner.forwardOutput()
        runner.withPluginClasspath()
        runner.withArguments("publish", "--info", "--stacktrace")
        runner.withProjectDir(projectDir)
        val result = runner.build();

        // Verify the result
        assertTrue(projectDir.resolve("build").resolve("github_maven_repo").exists())
    }
}
