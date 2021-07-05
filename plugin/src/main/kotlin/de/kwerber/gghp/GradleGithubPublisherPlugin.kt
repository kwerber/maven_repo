package de.kwerber.gghp

import de.kwerber.gghp.git.*
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin
import org.gradle.api.publish.maven.tasks.PublishToMavenRepository
import java.io.File
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.toPath

const val CUSTOM_REPOSITORY_NAME = "github_maven_repo"

class GradleGithubPublisherPlugin: Plugin<Project> {

    override fun apply(project: Project) {
        // Provide extension for configuration
        project.extensions.create("githubPublish", GithubPublishExtension::class.java, project)

        project.plugins.withType(MavenPublishPlugin::class.java) {
            // Hook into maven publish plugin to setup custom local repository
            val publishing: PublishingExtension = project.extensions.getByType(PublishingExtension::class.java)
            val ext: GithubPublishExtension = project.extensions.getByType(GithubPublishExtension::class.java)

            publishing.repositories.maven {
                it.name = CUSTOM_REPOSITORY_NAME
                it.url = ext.localRepoDir.get().toURI()
            }

            // Catch whenever some publication is to be published to the custom local repository
            project.tasks.withType(PublishToMavenRepository::class.java) { task ->
                task.doFirst {
                    beforePublish(it as PublishToMavenRepository)
                }

                task.doLast {
                    afterPublish(it as PublishToMavenRepository)
                }
            }
        }
    }

    @OptIn(ExperimentalPathApi::class)
    fun beforePublish(task: PublishToMavenRepository) {
        if (!task.repository.name.equals(CUSTOM_REPOSITORY_NAME)) {
            return
        }

        // Called before the artifact is actually published to the custom local repository
        // Setup git repo for the custom local repository here...

        task.logger.debug("Before publish: ${task.repository.name}, ${task.publication.artifactId}")

        val repoDir = task.repository.url.toPath().toFile()
        val ext = task.project.extensions.getByType(GithubPublishExtension::class.java)

        if (!repoDir.exists()) {
            task.logger.info("Creating dir $repoDir")
            repoDir.mkdirs()
        }

        if (!repoDir.isGitRepo()) {
            task.logger.info("Cloning gh repo from ${ext.githubRepoUrl.get()} to ${ext.localRepoDir.get()}")

            clone(ext.githubRepoUrl.get().toString(), ext.localRepoDir.get())

            task.logger.info("Clone finished.")
        }
        else {
            task.logger.info("Pulling changes from remote github repository...")

            pullChanges(repoDir)

            task.logger.info("Pull finished.")
        }

        // Always setup credentials and switch to the correct branch
        setupCredentials(repoDir, ext.committerName.orNull, ext.committerEmail.orNull, ext.signingKey.orNull)

        if (ext.githubRepoBranch.isPresent) {
            switchToBranch(repoDir, ext.githubRepoBranch.get())
        }
    }

    @OptIn(ExperimentalPathApi::class)
    fun afterPublish(task: PublishToMavenRepository) {
        if (!task.repository.name.equals(CUSTOM_REPOSITORY_NAME)) {
            return
        }

        // Called after the artifact was published to the custom local repository
        // Commit (and maybe push) changes to git...

        task.logger.debug("After publish: ${task.repository.name}, ${task.publication.artifactId}")

        val repoDir = task.repository.url.toPath().toFile() // url should be local file system path
        val pub = task.publication
        val ext = task.project.extensions.getByType(GithubPublishExtension::class.java)
        val tag = pub.groupId + "/" + pub.artifactId + "/" + pub.version

        if (hasTag(repoDir, tag)) {
            if (pub.version.endsWith("-SNAPSHOT")) {
                // Delete old tag and create new one later
                deleteTag(repoDir, tag)
            }
            else {
                throw IllegalStateException("artifact $tag has already been published. Maybe adjust the artifact version?")
            }
        }

        // Prepare
        val groupDirStr = pub.groupId.replace(".", File.separator)
        val groupDir = repoDir.resolve(File(groupDirStr))
        val artifactDir = groupDir.resolve(pub.artifactId)

        task.logger.debug("artifactDir: $artifactDir")

        // Stage changes
        artifactDir.stageChanges()

        // Commit changes
        val message = "Release ${pub.artifactId} ${pub.version}"

        commitChanges(repoDir, message, ext.signingKey.isPresent)

        task.logger.info("Committed changes.")

        // Tag changes
        tagLatestCommit(repoDir, tag)

        // Maybe push changes
        if (ext.pushChanges.get()) {
            task.logger.info("Pushing changes...")

            pushChanges(repoDir)

            task.logger.info("Pushed changes.")
        }
    }

}
