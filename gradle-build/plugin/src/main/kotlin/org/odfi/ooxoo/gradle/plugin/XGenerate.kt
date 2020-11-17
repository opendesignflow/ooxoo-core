package org.odfi.ooxoo.gradle.plugin

import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.workers.WorkerExecutor
import java.io.File
import javax.inject.Inject


abstract class XGenerate : DefaultTask() {

    @Inject
    abstract fun getWorkerExecutor(): WorkerExecutor?

    /*@Inject
    abstract fun getInputModelFile() : RegularFileProperty*/

    /*@OutputDirectory
    abstract fun getGeneratedFileDir(): RegularFileProperty?*/

    @TaskAction
    open fun generate() {
        println("Generating for Model: "+project.buildDir)
        println("Worker: " + getWorkerExecutor())

        // Work queue
        var workQueue  = getWorkerExecutor()?.noIsolation()

       // workQueue.

        // Look for main Source folder with all soruces
        var sPlugin = project.convention.getPlugin<JavaPluginConvention>(JavaPluginConvention::class.java)
        sPlugin.sourceSets.asMap.forEach {

            // Source set and Directories
            //-------------
            println("Source set: ${it.key} -> ${it.value}")
            it.value.allSource.srcDirs.forEach {
                println("- dir: " + it.path)
            }

            // On Main, generate sources
            //---------------
            if (it.key == "main") {

                //println("All Scala: " + it.value.allSource.files)
                it.value.allSource.files.filter { it.name.endsWith("xmodel.scala") }.forEach {

                    modelFile ->

                    println("Found Model to generate: "+modelFile)

                    // Get Output Dir
                    val targetDir = File(project.buildDir,"generated-sources")
                    targetDir.mkdirs()

                    // Submit
                    GeneratorFromModel.produceModel(modelFile,targetDir)
                     /*workQueue?.submit(GeneratorFromModel::class.java) { parameters ->

                         parameters.getBuildOutput()?.set(targetDir)
                         parameters.getModelFile()?.set(modelFile)

                     }*/


                }
            }
        }



    }
}