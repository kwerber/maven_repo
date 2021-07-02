package de.kwerber.gghp

import de.kwerber.gghp.git.clone
import de.kwerber.gghp.git.cloneInNonEmptyDir
import java.io.File

fun File.isGitRepo(): Boolean {
    return de.kwerber.gghp.git.isGitRepo(this)
}

fun File.stageChanges() {
    return de.kwerber.gghp.git.stageChanges(this)
}

fun File.makeGitRepo(repoUrl: String) {
    return cloneInNonEmptyDir(repoUrl, this, null)
}

fun File.makeGitRepo(repoUrl: String, branch: String) {
    return cloneInNonEmptyDir(repoUrl, this, branch)
}