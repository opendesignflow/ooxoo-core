/*
 * This Kotlin source file was generated by the Gradle 'init' task.
 */
package org.odfi.ooxoo.gradle.plugin

import org.gradle.testfixtures.ProjectBuilder
import kotlin.test.Test
import kotlin.test.assertNotNull

/**
 * A simple unit test for the 'org.odfi.ooxoo.gradle.plugin.greeting' plugin.
 */
class OoxooGradlePluginPluginTest {
    @Test fun `plugin registers task`() {
        // Create a test project and apply the plugin
        val project = ProjectBuilder.builder().build()
        project.plugins.apply("org.odfi.ooxoo.gradle.plugin.greeting")

        // Verify the result
        assertNotNull(project.tasks.findByName("ooxoo"))
    }
}