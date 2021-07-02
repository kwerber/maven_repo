//package de.kwerber.gghp
//
//import de.kwerber.gghp.git.*
//import org.gradle.api.DefaultTask
//import org.gradle.api.provider.Property
//import org.gradle.api.publish.PublishingExtension
//import org.gradle.api.publish.maven.MavenPublication
//import org.gradle.api.tasks.Input
//import org.gradle.api.tasks.TaskAction
//import java.io.File
//import java.net.URI
//
//abstract class GithubPublishTask : DefaultTask() {
//
//    @get:Input
//    abstract val githubRepoUrl: Property<URI>
//
//    @get:Input
//    abstract val localRepoDir: Property<File>
//
//    @get:Input
//    abstract val committerName: Property<String>
//
//    @get:Input
//    abstract val committerEmail: Property<String>
//
//    init {
//        dependsOn("publish")
//    }
//
//    @TaskAction
//    fun run() {
//        logger.warn("A")
//
//        val repoDir = localRepoDir.get()
//
//        repoDir.mkdirs()
//
//        logger.warn("A")
//
//        if (!repoDir.isGitRepo()) {
//            logger.warn("B")
//
//            if (repoDir.deleteRecursively()) {
//                logger.warn("C")
//
//                repoDir.mkdirs()
//
//                logger.warn("repoDir created: " + repoDir.absolutePath)
//
//                clone(githubRepoUrl.get().toString(), repoDir)
//
//                logger.warn("local repo created, run task again to publish")
//
//                return
//            }
//            else {
//                throw IllegalStateException("repo dir is not a git repo: " + repoDir.absolutePath)
//            }
//        }
//
//        if (!repoDir.isGitRepo()) {
//            throw IllegalStateException("repo dir is still not a git repo: " + repoDir.absolutePath)
//        }
//
//        // Setup user.name and user.email
//        if (committerName.isPresent && committerEmail.isPresent) {
//            setupCredentials(repoDir, committerName.get(), committerEmail.get())
//        }
//
//        // Get latest changes
//        pullChanges(repoDir)
//
//        // Read commit hash and remote url of project
//        val commitHash = getLatestCommitHash(project.projectDir)
//        val remoteUrl = getRemoteUrl(project.projectDir)
//        val commitPage = "$remoteUrl/commit/$commitHash"
//
//        logger.info("commitHash: $commitHash")
//        logger.info("remoteUrl: $remoteUrl")
//        logger.info("commitPage: $commitPage")
//
//        // For each publication...
//        val publishing = project.extensions.getByType(PublishingExtension::class.java)
//
//        publishing.publications.withType(MavenPublication::class.java) {
//            val groupDirStr = it.groupId.replace(".", File.separator)
//            val groupDir = localRepoDir.get().resolve(File(groupDirStr))
//            val artifactDir = groupDir.resolve(it.artifactId)
//
//            logger.warn("groupDirStr: $groupDirStr")
//            logger.warn("artifactDir: ${artifactDir.absoluteFile}")
//
//            // ... Stage changes
//            artifactDir.stageChanges()
//
//            // Commit changes
//            val message = "Release ${it.artifactId} ${it.version}\nCreated from $commitPage"
//
//            commitChanges(repoDir, message)
//        }
//
//        // Upload changes
//        //pushChanges(repoDir)
//    }
//
//    /*private fun initRepoIfNecessary(repoDir: File) {
//        repoDir.resolve("tmp").mkdirs()
//
//        if (!repoDir.isGitRepo()) {
//            init(repoDir)
//
//            addRemote(repoDir, githubRepoUrl.get().toString())
//
//            pullChanges(repoDir, "origin", "master")
//
//            switchToBranch(repoDir, "master")
//        }
//
//        // Setup user.name and user.email
//        if (committerName.isPresent && commiterEmail.isPresent) {
//            setupCredentials(repoDir, committerName.get(), commiterEmail.get())
//        }
//
//        // Get changes from remote just to be up to date
//        pullChanges(repoDir)
//    }*/
//
//}