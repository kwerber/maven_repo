package de.kwerber.gghp

import org.gradle.api.Project
import org.gradle.api.provider.Property
import java.io.File
import java.net.URI

abstract class GithubPublishExtension constructor(project: Project) {

    /**
     * URL of the maven/git repo
     */
    abstract val githubRepoUrl: Property<URI>

    /**
     * The branch of the github repo that the artifact should be committed to
     */
    abstract val githubRepoBranch: Property<String>

    /**
     * Path where the maven repo should be cloned to
     */
    abstract val localRepoDir: Property<File>

    /**
     * The name associated with all publications.
     */
    abstract val committerName: Property<String>

    /**
     * The email address associated with all publications.
     */
    abstract val committerEmail: Property<String>

    /**
     * Whether or not to push the commits to the maven repo after committing them.
     */
    abstract val pushChanges: Property<Boolean>

    /**
     * Which gpg key to use to sign the commits.
     */
    abstract val signingKey: Property<String>

    init {
        githubRepoUrl.convention(URI.create("https://github.com/kwerber/maven_repo"))
        localRepoDir.convention(project.projectDir.resolve("build").resolve(CUSTOM_REPOSITORY_NAME))
        pushChanges.convention(true)
    }

}