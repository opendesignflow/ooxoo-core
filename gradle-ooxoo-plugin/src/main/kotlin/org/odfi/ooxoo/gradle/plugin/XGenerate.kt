package org.odfi.ooxoo.gradle.plugin

import org.gradle.api.DefaultTask
import org.gradle.api.plugins.JavaPluginConvention
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

        logger.info("Generating for Model: " + project.buildDir)
        logger.info("Worker: " + getWorkerExecutor())

        // Work queue
        var workQueue = getWorkerExecutor()?.noIsolation()

        // workQueue.

        // Compiler
        //-----------------------
        val compiler = ModelCompiler()

        // Set Compiler output from project
        val ooxooCompilerOutputFile = File(project.buildDir, "classes/ooxoo")
        ooxooCompilerOutputFile.mkdirs()
        compiler.compiler().setCompilerOutput(ooxooCompilerOutputFile)


        // Look for main Source folder with all soruces
        //ScalaPluginConvention
        var sPlugin = project.convention.getPlugin<JavaPluginConvention>(JavaPluginConvention::class.java)

        sPlugin.sourceSets.asMap.forEach {

            // Source set and Directories
            //-------------
            logger.info("Source set: ${it.key} -> ${it.value}")
            it.value.allSource.srcDirs.forEach {
                logger.info("- dir: " + it.path)
            }

            // On Main, generate sources
            //---------------
            if (it.key == "main") {

                //println("All Scala: " + it.value.allSource.files)
                it.value.allSource.files.filter { it.name.endsWith("xmodel.scala") }.forEach {

                        modelFile ->

                    logger.info("Found Model to generate: " + modelFile)
                    logger.info("- Last modified: " + modelFile.lastModified())

                    // Get Output Dir
                    val targetDir = File(project.buildDir, "generated-sources")
                    targetDir.mkdirs()

                    // Submit
                    GeneratorFromModel.produceModel(compiler, modelFile, targetDir)
                    /*workQueue?.submit(GeneratorFromModel::class.java) { parameters ->

                        parameters.getBuildOutput()?.set(targetDir)
                        parameters.getModelFile()?.set(modelFile)

                    }*/
                }
            }
        }
    }
}