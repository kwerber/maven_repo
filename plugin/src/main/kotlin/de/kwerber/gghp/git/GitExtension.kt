package de.kwerber.gghp.git

import java.io.File

fun File.isGitRepo(): Boolean {
    return isGitRepo(this)
}

fun File.stageChanges() {
    return stageChanges(this)
}

fun File.makeGitRepo(repoUrl: String) {
    return cloneInNonEmptyDir(repoUrl, this, null)
}

fun File.makeGitRepo(repoUrl: String, branch: String) {
    return cloneInNonEmptyDir(repoUrl, this, branch)
}