package de.kwerber.gghp

import org.gradle.testfixtures.ProjectBuilder
import kotlin.test.Test
import kotlin.test.assertNotNull

class GradleGithubPublisherPluginTest {

    // As of right now, these tests don't do anything

    @Test fun `plugin registers task`() {
        val project = ProjectBuilder.builder().build()
        project.plugins.apply("de.kwerber.ghpub")

        //assertNotNull(project.tasks.findByName("greeting"))
    }
}
