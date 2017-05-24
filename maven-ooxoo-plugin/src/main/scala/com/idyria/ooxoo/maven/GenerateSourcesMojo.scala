/*
 * #%L
 * Maven plugin for ooxoo to generate sources from modeling
 * %%
 * Copyright (C) 2008 - 2014 OSI / Computer Architecture Group @ Uni. Heidelberg
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */
package com.idyria.osi.ooxoo.maven

import org.apache.maven.plugin.AbstractMojo
import org.apache.maven.plugin.MojoExecutionException
import org.apache.maven.project.MavenProject

/*
import org.apache.maven.reporting.MavenReport
import org.apache.maven.reporting.MavenReportException*/

import org.apache.maven.plugins.annotations._

import com.idyria.osi.ooxoo.model._
import com.idyria.osi.ooxoo.model.out.scala._
import com.idyria.osi.ooxoo.model.writers._

import java.io._
import scala.io.Source

import org.scala_tools.maven.mojo.annotations._

/**
 * Generate sources from model, and copy model also to output
 *
 */
//@Mojo(name = "generate-sources")
//@goal("generate-sources")
//@phase("generate-sources")
@Mojo(name = "generate-sources")
class GenerateSourcesMojo extends AbstractMojo /*with MavenReport*/ {

  @Parameter(defaultValue = "${project}")
  var project: MavenProject = _

  @Parameter(property = "ooxoo.force", defaultValue = "false")
  var force: Boolean = false

  @Parameter(defaultValue = "${project.build.sourceDirectory}")
  var sourceFolder = new File("src/main/scala")

  var modelsFolder = new File("src/main/xmodels")

  @Parameter(defaultValue = "${project.build.directory}/generated-sources/")
  var outputBaseFolder = new File("target/generated-sources/")
  
  @Parameter(defaultValue = "${project.build.directory}/maven-status/maven-ooxoo-plugin/")
  var statusFolder = new File("target/maven-status/maven-ooxoo-plugin")

  @throws(classOf[MojoExecutionException])
  override def execute() {
    getLog().info("Looking for xmodels to generate with project: "+project);

    sourceFolder.exists() match {
      case true => 

        statusFolder.mkdirs()
        /*Thread.currentThread.getContextClassLoader match {
                case urlCl : java.net.URLClassLoader => 

                    urlCl.getURLs.foreach {

                        u => println("Available in classLoader: "+u)
                    }

                case _ => 
            }*/

        var xModelFiles = List[File]()

        //-- Map to store instances of producers, for reuse purpose
        var producers = Map[Class[_ <: ModelProducer], ModelProducer]()

        // Search in models folder
        //---------------
        if (modelsFolder.exists) {

          //-- Search the xmodels
          //---------------------------------

          var xmodelsFiles = modelsFolder.listFiles(new FilenameFilter() {

            def accept(dir: File, name: String): Boolean = {
              name.matches(".*\\.xmodel")
            }

          })
          xModelFiles = xModelFiles ::: xmodelsFiles.toList
        }

        //-- Search in source package
        //--------------
        java.nio.file.Files.walkFileTree(sourceFolder.toPath, new java.nio.file.SimpleFileVisitor[java.nio.file.Path] {

          override def visitFile(file: java.nio.file.Path, attributes: java.nio.file.attribute.BasicFileAttributes) = {

            // Only Retain files ending with .xmodel.scala
            file.toString.endsWith(".xmodel.scala") match {
              case true =>

                xModelFiles = xModelFiles :+ file.toFile

              case false =>
            }

            java.nio.file.FileVisitResult.CONTINUE

          }
        })

        //-- Process all models
        //--  - First Filter the on that don't have to be regenerated
        //--  - Then Produce
        //------------------

        force match {
          case true => getLog().info("Forcing regeneration ofr models");
          case false =>

        }

        xModelFiles.filter {
          f =>
            force match {
              case true => true
              case false =>
                // Get or set a timestamp file to detect if model file changed since last run
                //-------------------

                //-- Set timestamps. If modified is greater than the last timestamp -> regenerate
                var lastTimeStamp: Long = 0
                var lastModified = f.lastModified

                statusFolder.mkdirs
                var timestampFile = new File(statusFolder, s"${f.getName}.ts")
                timestampFile.exists match {
                  case true =>
                    lastTimeStamp = java.lang.Long.parseLong(Source.fromFile(timestampFile).mkString)
                  case false =>
                }

                // Write Actual timestamp
                //-------------
                //java.nio.file.Files.write(timestampFile.toPath,new String(s"${System.currentTimeMillis}").getBytes)

                lastModified > lastTimeStamp
            }

        }.foreach {
          f =>
            getLog().info("(Re)generating model: " + f);

            // Get Model as String
            //--------------------------
            var source = Source.fromFile(f)
            var content = source.mkString

            // Compile to get annotated producers
            //---------------------
            var modelInfos = ModelCompiler.compile(f)

            // Produce for all defined producers
            //---------------
            if (modelInfos.producers != null && modelInfos.producers.value() != null) {
              modelInfos.producers.value().foreach {
                producerAnnotation =>

                  // Get Producer
                  //---------
                  var producer = producers.get(producerAnnotation.value) match {
                    case Some(producer) =>
                      producer
                    case None =>
                      var producer = producerAnnotation.value.newInstance
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
                      var outputFolder = new File(outputBaseFolder, producer.outputType)
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
            }
            // EOF Something to produce

            // Write Actual timestamp
            //-------------
            var timestampFile = new File(statusFolder, s"${f.getName}.ts")
            java.nio.file.Files.write(timestampFile.toPath, new String(s"${System.currentTimeMillis}").getBytes)

        }
        // EOF Xfiles loop

        // Add All Target Folder generated sources as compile unit
        //-------------------
        if (outputBaseFolder.exists) {
          //outputBaseFolder.listFiles.filter(_.isDirectory).foreach(f => this.project.addCompileSourceRoot(f.getAbsolutePath))
        }

      case false => 

        getLog().info("Source Folder does not exist: "+sourceFolder);
    }
    
    

  }

  // Reporting
  //---------------------------
  var defferedReporting = List[(ModelInfos, ModelProducer)]()

  var reportingOutputDirectory: java.io.File = null

  def canGenerateReport(): Boolean = {
    defferedReporting.size > 0
  }

 /* def generate(sink: org.codehaus.doxia.sink.Sink, locale: java.util.Locale): Unit = {

  }*/
  def getCategoryName(): String = {
    "OOXOO"
  }
  def getDescription(x$1: java.util.Locale): String = {
    "OOXOO Reports"
  }
  def getName(locale: java.util.Locale): String = {
    "OOXOO"
  }
  def getOutputName(): String = {
    "OOXOO"
  }
  def getReportOutputDirectory(): java.io.File = {
    this.reportingOutputDirectory match {
      case dir if (dir == null) => new File(project.getBasedir, project.getReporting.getOutputDirectory + "/OOXOO").getAbsoluteFile
      case dir => dir
    }
  }
  def isExternalReport(): Boolean = {
    true
  }
  def setReportOutputDirectory(dir: java.io.File): Unit = {
    this.reportingOutputDirectory = dir
  }

}
