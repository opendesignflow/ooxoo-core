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

    var outputFolder = new File("target/generated-sources/scala/")
 
    @throws(classOf[MojoExecutionException])
    override def execute()  {
        getLog().info( "Looking for xmodels to generate" );



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

                // Prepare Producer
                //-----------------------
                var scalaProducer = new ScalaProducer()

                // Prepare Writer
                //----------------------
                outputFolder.mkdirs()
                var out = new FileWriters(outputFolder)


                // Compile
                //---------------------
                ModelCompiler.produce(f,scalaProducer,out)

                // Check Result
                //-------------------------
        }

        // Add Target Folder to compile source if existing
        //-----------------
        if (outputFolder.exists) {
            this.project.addCompileSourceRoot(outputFolder.getAbsolutePath);
        }

    }
}
