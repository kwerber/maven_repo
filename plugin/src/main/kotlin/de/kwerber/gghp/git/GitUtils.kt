package de.kwerber.gghp.git

import java.io.File
import java.lang.ProcessBuilder
import java.nio.charset.Charset
import java.util.concurrent.TimeUnit

private fun exec(workingDir: File, vararg command: String): String {
    println(workingDir.toString() + "> CMD: ${command.joinToString(" ") }")

    val p = ProcessBuilder(command.toList())
        .directory(workingDir)
        .redirectError(ProcessBuilder.Redirect.INHERIT)
        .redirectOutput(ProcessBuilder.Redirect.PIPE)
        .start()

    p.waitFor(10, TimeUnit.SECONDS)

    val out = p.inputStream.readAllBytes().toString(Charset.defaultCharset())

    println(out)

    return out
}

fun isGitRepo(dir: File): Boolean {
    return dir.resolve(".git").isDirectory
}

fun init(repoDir: File) {
    exec(repoDir, "git", "init")
}

fun addRemote(repoDir: File, remoteUrl: String) {
    exec(repoDir, "git", "remote", "add", "origin", remoteUrl)
}

fun updateRemote(repoDir: File, remote: String) {
    exec(repoDir, "git", "remote", "update", remote)
}

fun setRemoteTrackedBranch(repoDir: File, remote: String, branchLabel: String) {
    exec(repoDir, "git", "remote", "set-branches", remote, branchLabel)
}

fun addRemoteTrackedBranch(repoDir: File, remote: String, branchLabel: String) {
    exec(repoDir, "git", "remote", "set-branches", "--add", remote, branchLabel)
}

fun clone(repoUrl: String, targetDir: File) {
    exec(targetDir, "git", "clone", repoUrl, ".")
}

fun cloneInNonEmptyDir(repoUrl: String, targetDir: File, branch: String?) {
    init(targetDir)
    addRemote(targetDir, repoUrl)
    fetchRemoteBranch(targetDir, branch ?: "empty")
    switchToBranch(targetDir, branch ?: "empty")
}

fun createBranch(repoDir: File, targetBranch: String) {
    exec(repoDir, "git", "branch", targetBranch)
}

fun getBranches(repoDir: File): List<String> {
    return exec(repoDir, "git", "branch", "-a")
        .split("\n")
        .filter { line -> line.isNotBlank() }
        .map { line -> line.substring(2) }
}

fun hasBranch(repoDir: File, targetBranch: String): Boolean {
    return getBranches(repoDir).contains(targetBranch)
}

fun switchToBranch(repoDir: File, targetBranch: String) {
    exec(repoDir, "git", "switch", targetBranch)
}

fun fetchRemoteBranch(repoDir: File, targetBranch: String) {
    exec(repoDir, "git", "remote", "set-branches", "--add", "origin", targetBranch)
}

fun stageChanges(dir: File) {
    exec(dir, "git", "add", ".")
}

fun tagLatestCommit(repoDir: File, tag: String) {
    exec(repoDir, "git", "tag", tag, "HEAD")
}

fun hasTag(repoDir: File, tag: String): Boolean {
    return exec(repoDir, "git", "tag", "--list", tag).isNotBlank()
}

fun deleteTag(repoDir: File, tag: String) {
    exec(repoDir, "git", "tag", "-d", tag)
}

fun setupCredentials(repoDir: File, authorName: String?, authorEmail: String?, signingKey: String?) {
    if (authorName != null) {
        exec(repoDir, "git", "config", "--local", "user.name", authorName)
    }

    if (authorEmail != null) {
        exec(repoDir, "git", "config", "--local", "user.email", authorEmail)
    }

    if (signingKey != null) {
        exec(repoDir, "git", "config", "--local", "user.signingkey", signingKey)
    }
}

fun commitChanges(repoDir: File, message: String, sign: Boolean) {
    if (sign) {
        exec(repoDir, "git", "commit", "-S", "-m", "\"${message}\"")
    }
    else {
        exec(repoDir, "git", "commit", "-m", "\"${message}\"")
    }
}

fun pullChanges(repoDir: File) {
    exec(repoDir, "git", "pull")
}

fun pullChanges(repoDir: File, remote: String, branch: String) {
    exec(repoDir, "git", "pull", remote, branch)
}

fun getLatestCommitHash(repoDir: File): String {
    return exec(repoDir, "git", "log", "-1", "--pretty=format:%h")
}

fun getRemoteUrl(repoDir: File): String {
    return exec(repoDir, "git", "remote", "get-url", "origin").replace("[\\n\\t ]", "")
}

fun pushChanges(repoDir: File, targetBranch: String) {
    exec(repoDir, "git", "push", "--set-upstream", "origin", targetBranch)
}

fun pushChanges(repoDir: File) {
    exec(repoDir, "git", "push")
}