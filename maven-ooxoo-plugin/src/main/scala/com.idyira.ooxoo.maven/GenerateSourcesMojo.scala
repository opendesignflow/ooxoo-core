package com.idyira.osi.ooxoo.maven


import org.apache.maven.plugin.AbstractMojo
import org.apache.maven.plugin.MojoExecutionException
import org.apache.maven.project.MavenProject

import org.apache.maven.plugins.annotations._

import com.idyria.osi.ooxoo.model._
import com.idyria.osi.ooxoo.model.out.scala._
import com.idyria.osi.ooxoo.model.writers._

import java.io._
import scala.io.Source


/**
    Generate sources from model, and copy model also to output

*/
@Mojo( name = "generate-sources")
class GenerateSourcesMojo extends AbstractMojo {

    @Parameter(defaultValue="${project}")
    var project : MavenProject = null

    var modelsFolder = new File("src/main/xmodels")

    var outputBaseFolder = new File("target/generated-sources/")
 
    @throws(classOf[MojoExecutionException])
    override def execute()  {
        getLog().info( "Looking for xmodels to generate" );


        //-- Parameters
        //-------------------

        //-- Map to store instances of producers, for reuse purpose
        var producers = Map[Class[ _ <: Producer],Producer]()

        //-- Search the xmodels
        //---------------------------------
        var xmodelsFiles = modelsFolder.listFiles(new FilenameFilter() {

            def accept(dir:File,name:String) : Boolean = {
                name.matches(".*\\.xmodel")
            } 

        })

        //-- Process all models
        //------------------
        xmodelsFiles.foreach {
            f => 
                getLog().info( "Processing model: "+f );

                // Get Model as String
                //--------------------------
                var source = Source.fromFile(f)
                var content = source.mkString


                // Compile to get annotated producers
                //---------------------
                var modelInfos = ModelCompiler.compile(f)

                // Produce for all defined producers
                //---------------
                if (modelInfos.producers!=null && modelInfos.producers.value()!=null) {
                    modelInfos.producers.value().foreach {
                        producerAnnotation => 

                            // Get Producer
                            //---------
                            var producer = producers.get(producerAnnotation.value) match {
                                    case Some(producer) => producer 
                                    case None =>  
                                        var producer = producerAnnotation.value.newInstance
                                        producers = producers +  (producerAnnotation.value -> producer)
                                        producer
                            }

                            // Prepare Output
                            //--------------
                            var outputFolder = new File(outputBaseFolder,producer.outputType)
                            outputFolder.mkdirs()
                            var out = new FileWriters(outputFolder)

                            // Produce
                            //----------
                            ModelCompiler.produce(modelInfos,producer,out)

                            // Add Target Folder to compile source if existing
                            //-----------------
                            if (outputFolder.exists) {
                                this.project.addCompileSourceRoot(outputFolder.getAbsolutePath);
                            }
                    }

                    // Copy Model to output
                    //-------------------------
                } 
        }
        // EOF Xfiles loop

        

    }
}
