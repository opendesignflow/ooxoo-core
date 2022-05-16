package org.odfi.ooxoo.gradle.plugin

import org.odfi.ooxoo.model.writers.FileWriters
import org.gradle.workers.WorkAction
import scala.Tuple2
import java.io.File

abstract class GeneratorFromModel : WorkAction<XModelProducerParameters> {

    override fun execute() {
        TODO("Not yet implemented")

        // Compile
        //-----------
       /* println("Processing Model file (with reset): "+this.parameters.getModelFile())

        val compiler =  ModelCompiler()

       // ModelCompiler.resetCompiler()
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
        }*/

        // EOF Something to produce

      //  ModelCompiler.produce(modelInfos, producer, out)

    }

    companion object {

        fun produceModel(compiler: ModelCompiler, modelFile:File, outputDir:File,config:OOXOOExtension?) {

            // Compile
            //-----------
            println("Processing Model file: "+modelFile)

            var modelInfos = compiler.compile(modelFile)
            //var outputDir = this.parameters.getBuildOutput()!!.get().asFile

            // Get All Producers
            //---------
            // Produce for all defined producers
            //---------------
            modelInfos?.let {
                mif ->

                mif.producers()?.let {
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

                        // Config
                        //--------
                        config?.let {
                            if (it.javax.get()) {

                                modelInfos.model().parameter(Tuple2("javax","true"))
                            }
                        }

                        // Produce
                        //---------------
                        compiler.produce(modelInfos, producer, out)
                        //ModelCompiler.produce(modelInfos, producer, out)

                    }
                }
            }

        }

    }
}