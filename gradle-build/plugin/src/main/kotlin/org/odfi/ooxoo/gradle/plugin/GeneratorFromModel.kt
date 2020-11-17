package org.odfi.ooxoo.gradle.plugin

import com.idyria.osi.ooxoo.model.ModelCompiler
import com.idyria.osi.ooxoo.model.producers
import com.idyria.osi.ooxoo.model.writers.FileWriters
import org.gradle.workers.WorkAction
import java.io.File
import kotlin.reflect.full.primaryConstructor

abstract class GeneratorFromModel : WorkAction<XModelProducerParameters> {

    override fun execute() {
        //TODO("Not yet implemented")

        // Compile
        //-----------
        println("Processing Model file: "+this.parameters.getModelFile())

        var modelInfos = ModelCompiler.compile(this.parameters.getModelFile()!!.get().asFile)
        var outputDir = this.parameters.getBuildOutput()!!.get().asFile

        // Get All Producers
        //---------
        // Produce for all defined producers
        //---------------
        modelInfos?.let {
            modelInfos ->

            modelInfos.producers()?.let {
                producersDef ->

                producersDef.value.forEach {
                    producerAnnotation ->

                    // Get Producer from def
                    //--------------
                    var producer = producerAnnotation.value.primaryConstructor?.call()!!

                    // Prepare output
                    //-----------
                    var producerOutputDir = File(outputDir,producer.outputType())
                    producerOutputDir.mkdirs()

                    var out = FileWriters(producerOutputDir)

                    // Produce
                    //---------------
                    ModelCompiler.produce(modelInfos, producer, out)

                }
            }
        }
        /*if (modelInfos.producers != null && modelInfos.producers
                        .value() != null) {
            modelInfos.producers.value().foreach { producerAnnotation =>
                // Get Producer
                //---------
                var producer = producers.get(producerAnnotation.value) match {
                    case Some(producer) =>
                    producer
                    case None =>
                    var producer = producerAnnotation.value
                            .getDeclaredConstructor()
                            .newInstance()
                    producers = producers + (producerAnnotation.value -> producer)
                    producer
                }

                // Produce or produce later
                //----------
                producer.outputType match {

                    // Report, so save and generate when reports are generated
                    case outputType if (outputType.startsWith("report.")) =>
                    // Produce now as sources
                    case _ =>
                    // Prepare Output
                    //--------------
                    var outputFolder =
                            new File(outputBaseFolder, producer.outputType)
                    /*if (cleanOutputs) {
                        println("Cleaning: "+outputFolder)
                        DirectoryUtilities.deleteDirectoryContent(outputFolder)
                      }*/
                    outputFolder.mkdirs()
                    var out = new FileWriters(outputFolder)

                    ModelCompiler.produce(modelInfos, producer, out)

                    // Add Target Folder to compile source if existing
                    //-----------------
                    if (outputFolder.exists) {
                        //this.project.addCompileSourceRoot(outputFolder.getAbsolutePath);
                    }
                }

            }
            // EOF Foreach producers
        }*/
        // EOF Something to produce

      //  ModelCompiler.produce(modelInfos, producer, out)

    }

    companion object {

        fun produceModel(modelFile:File,outputDir:File) {

            // Compile
            //-----------
            println("Processing Model file: "+modelFile)

            var modelInfos = ModelCompiler.compile(modelFile)
            //var outputDir = this.parameters.getBuildOutput()!!.get().asFile

            // Get All Producers
            //---------
            // Produce for all defined producers
            //---------------
            modelInfos?.let {
                modelInfos ->

                modelInfos.producers()?.let {
                    producersDef ->

                    producersDef.value.forEach {
                        producerAnnotation ->

                        // Get Producer from def
                        //--------------

                        var producer = producerAnnotation.value.java.getDeclaredConstructor().newInstance()!!

                        // Prepare output
                        //-----------
                        var producerOutputDir = File(outputDir,producer.outputType())
                        producerOutputDir.mkdirs()

                        var out = FileWriters(producerOutputDir)

                        // Produce
                        //---------------
                        ModelCompiler.produce(modelInfos, producer, out)

                    }
                }
            }

        }

    }
}