package org.odfi.oooxoo.generator

import org.apache.commons.cli.Options
import org.apache.commons.cli.DefaultParser
import org.odfi.ooxoo.gradle.plugin.ModelCompiler
import org.odfi.ooxoo.model.writers.FileWriters
import java.io.File

object OGenerator {

  lazy val compiler = {
    val c = new ModelCompiler
    val modelCompileOut = new File("").getCanonicalFile
    println(s"Compiling models to: $modelCompileOut")
    c.compiler.setCompilerOutput(new File("").getCanonicalFile)
    c
  }

  def main(args: Array[String]) = {

    println("Running OOXOO Generator 3")
    //println(s"Args: $args")
    args.foreach {
      a =>
        println(s"Arg $a")
    }

    var options = new Options()

    options.addOption("models", true, "Run in Batch Mode without GUI")
    options.addOption("output", true, "Run Pipelines Defined in File")
    options.addOption("javaee", false, "Generate with javax namespace instead of jakarta")

    var parser = new DefaultParser();
    var cmd = parser.parse(options, args);

    val modelsValue = cmd.getOptionValue("models")

    val outputDirectory = new File(cmd.getOptionValue("output")).getCanonicalFile
    outputDirectory.mkdirs()

    println(s"Output Folder: $outputDirectory")

    modelsValue match {
      case null =>
        println("No Models option defined")
      case other =>
        val models = modelsValue.split(java.io.File.pathSeparatorChar)
        models.map(new File(_)).foreach {
          model =>
            println(s"- Processing $model")

            // Compiling
            //------
            val modelInfos = compiler.compile(model)

            // Generate for all producers
            //------------
            if (modelInfos != null) {
              modelInfos.producers.value() match {
                case null =>
                case producers =>
                  producers.foreach {
                    producerAnnotation =>

                      // Get Producer from def
                      //--------------
                      var producer = producerAnnotation.value.getDeclaredConstructor().newInstance()

                      // Prepare output
                      //-----------
                    //  var producerOutputDir = File(outputDirectory/*, producer.outputType*/)
                     // outputDirectory.mkdirs()

                      var out = FileWriters(outputDirectory)

                      // Config
                      //--------
                      if (options.hasOption("-javaee")) {
                        modelInfos.model.parameter(("javax") -> "true")
                      }


                      // Produce
                      //---------------
                      compiler.produce(modelInfos, producer, out)
                    //ModelCompiler.produce(modelInfos, producer, out)
                  }
              }

            } else {
              println("-- No Model infos compiled")
            }

        }
    }
  }

}
