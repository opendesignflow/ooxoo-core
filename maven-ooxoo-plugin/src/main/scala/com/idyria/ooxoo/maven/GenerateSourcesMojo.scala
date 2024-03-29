/*
 * #%L
 * Maven plugin for ooxoo to generate sources from modeling
 * %%
 * Copyright (C) 2006 - 2017 Open Design Flow
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */
package org.odfi.ooxoo.maven

import java.util

import org.apache.maven.plugin.AbstractMojo
import org.apache.maven.plugin.MojoExecutionException
import org.apache.maven.project.MavenProject

import scala.beans.BeanProperty

/*
import org.apache.maven.reporting.MavenReport
import org.apache.maven.reporting.MavenReportException*/

import org.apache.maven.plugins.annotations._

import org.odfi.ooxoo.model._
import org.odfi.ooxoo.model.out.scala._
import org.odfi.ooxoo.model.writers._

import java.io._
import scala.io.Source

import org.scala_tools.maven.mojo.annotations._
import org.odfi.tea.file.DirectoryUtilities

/**
 * Generate sources from model, and copy model also to output
 *
 */
@goal("generate")
@phase("generate-sources")
@requiresProject(true)
class GenerateSourcesMojo extends AbstractMojo /*with MavenReport*/ {


  // @Parameter(property = "project", defaultValue = "${project}")

  @required
  @readOnly
  @parameter
  @defaultValue("${project}")
  var project: MavenProject = _


  /*@parameter
  @alias("ooxoo.force")
  @defaultValue("false")*/

  //@Parameter(property = "ooxoo.force", defaultValue = "true")

  @parameter
  @alias("ooxoo.force")
  @expression("ooxoo.force")
  @defaultValue("false")
  var ooxooForce: Boolean = false

  @Parameter(property = "ooxoo.cleanOutputs", defaultValue = "true")
  var cleanOutputs: Boolean = true


  @required
  @readOnly
  @parameter
  @defaultValue("${project.build.sourceDirectory}")
  var sourceFolder = new File("src/main/scala")
  //var sourceFolder = new File("src/main/scala")

  @Parameter(defaultValue = "${project.build.testSourceDirectory}")
  var testSourceFolder = new File("src/test/scala")

  var modelsFolder = new File("src/main/xmodels")

  @required
  @readOnly
  @parameter
  @defaultValue("${project.build.directory}/generated-sources/")
  var outputBaseFolder = new File("target/generated-sources/")


  @required
  @readOnly
  @parameter
  @defaultValue("${project.build.directory}/maven-status/maven-ooxoo-plugin/")
  var statusFolder = new File("target/maven-status/maven-ooxoo-plugin")

  @throws(classOf[MojoExecutionException])
  override def execute() = {

    getLog().info("Forcing: " + ooxooForce);
    getLog().info("Looking for xmodels to generate with project: " + project);

    getLog().info("Source Folders: " + project.getCompileSourceRoots)

    project.getCompileSourceRoots.forEach {
      sourceFolderStr =>

        val sourceFolder = new File(sourceFolderStr).getCanonicalFile
        getLog().info("Processing Source Folder: " + sourceFolder);


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
            val fileVisitor =
            new java.nio.file.SimpleFileVisitor[java.nio.file.Path] {

              override def visitFile(
                                      file: java.nio.file.Path,
                                      attributes: java.nio.file.attribute.BasicFileAttributes
                                    ) = {

                // Only Retain files ending with .xmodel.scala
                file.toString.endsWith(".xmodel.scala") match {
                  case true =>
                    xModelFiles = xModelFiles :+ file.toFile

                  case false =>
                }

                java.nio.file.FileVisitResult.CONTINUE

              }
            }

            java.nio.file.Files.walkFileTree(sourceFolder.toPath, fileVisitor)
            if (testSourceFolder.exists) {
              java.nio.file.Files.walkFileTree(testSourceFolder.toPath, fileVisitor)
              //getLog().info("Test Folder: " + testSourceFolder.toPath);
            }

            //-- Process all models
            //--  - First Filter the on that don't have to be regenerated
            //--  - Then Produce
            //------------------

            ooxooForce match {
              case true => getLog().info("Forcing regeneration of models");
              case false =>
            }

            xModelFiles
              .filter { f =>
                ooxooForce match {
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
                        lastTimeStamp = java.lang.Long.parseLong(
                          Source.fromFile(timestampFile).mkString
                        )
                      case false =>
                    }

                    // Write Actual timestamp
                    //-------------
                    //java.nio.file.Files.write(timestampFile.toPath,new String(s"${System.currentTimeMillis}").getBytes)

                    lastModified > lastTimeStamp
                }

              }
              .foreach { f =>
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
                if (modelInfos.producers != null && modelInfos.producers
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
                }
                // EOF Something to produce

                // Write Actual timestamp
                //-------------
                var timestampFile = new File(statusFolder, s"${f.getName}.ts")
                java.nio.file.Files.write(
                  timestampFile.toPath,
                  new String(s"${System.currentTimeMillis}").getBytes
                )

              }
            // EOF Xfiles loop

            // Add All Target Folder generated sources as compile unit
            //-------------------
            if (outputBaseFolder.exists) {
              //outputBaseFolder.listFiles.filter(_.isDirectory).foreach(f => this.project.addCompileSourceRoot(f.getAbsolutePath))
            }

          case false =>
            getLog().info("Source Folder does not exist: " + sourceFolder);
        }
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
      case dir if (dir == null) =>
        new File(
          project.getBasedir,
          project.getBuild.getOutputDirectory + "/OOXOO"
        ).getCanonicalFile
      case dir => dir
    }
  }

  def isExternalReport(): Boolean = {
    true
  }

  def setReportOutputDirectory(dir: java.io.File): Unit = {
    this.reportingOutputDirectory = dir
  }

  override def getPluginContext: java.util.Map[_, _] = super.getPluginContext

}
