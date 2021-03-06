/*
 * #%L
 * Core runtime for OOXOO
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

package com.idyria.osi.ooxoo.model

import java.io.{File, PrintWriter, StringWriter}

import com.idyria.osi.ooxoo.model.Writer

import scala.io._
import scala.tools.nsc._
import scala.tools.nsc.interpreter._
import scala.runtime._
import java.net._

import org.odfi.tea.compile.PrintWriterReplReporter

import scala.jdk.javaapi.CollectionConverters._

/**
 * Return informations about a model
 */
class ModelInfos(

    var name: String) {

  /**
   * The list of @producer annotation instances declared on model
   */
  var producers: producers = null

}

/*
    The SpeakRunner Object runs a speak script by wrapping it around necessary code and compiling/running

*/
object ModelCompiler {

  // Reporter
  //-------------


  // Run Statistics
  //---------------------

  /// Number of script runs
  var runCount = 0

  // Prepare Class Loader for compiler
  //-------------------------
  var bootclasspath = List[URL]()
  var bootclasspath2 = List[String]()

  //--- Scala Compiler and library
  try {
    val compilerPath = java.lang.Class.forName("scala.tools.nsc.Interpreter").getProtectionDomain.getCodeSource.getLocation
    val libPath = java.lang.Class.forName("scala.Some").getProtectionDomain.getCodeSource.getLocation
   
    
    val runtimeObject = java.lang.Class.forName("scala.runtime.RichInt").getProtectionDomain.getCodeSource.getLocation
    
    bootclasspath = compilerPath :: libPath :: runtimeObject :: bootclasspath
 } catch {
    case e: Throwable =>
 }

  //-- Mex
  val mexPath = java.lang.Class.forName("com.idyria.osi.ooxoo.model.ModelCompiler").getProtectionDomain.getCodeSource.getLocation

    bootclasspath =  mexPath :: bootclasspath
 

  //-- If Classloader is an URL classLoader, add all its urls to the compiler
  //println("Classloader type: "+getClass().getClassLoader())
  if (getClass.getClassLoader.isInstanceOf[URLClassLoader]) {

    //println("Adding URLs from class loader to boot class path")

    //-- Gather URLS
    getClass.getClassLoader.asInstanceOf[URLClassLoader].getURLs().foreach {
      url =>

        var urlFile = new File(url.getFile)
        bootclasspath2 = urlFile.getAbsolutePath :: bootclasspath2

        //println(s" -> URL translated: ${urlFile.toURI.toURL}")
        bootclasspath = url :: bootclasspath
    }

  }

  // Prepare Compiler Settings Settings
  //----------------
  var settings2 = new GenericRunnerSettings({
    error => println(error)
  })
  settings2.usejavacp.value = true
  settings2.classpath.value = (bootclasspath mkString java.io.File.pathSeparator) + java.io.File.pathSeparator + (bootclasspath2 mkString java.io.File.pathSeparator)
  settings2.bootclasspath.value = (bootclasspath mkString java.io.File.pathSeparator) + java.io.File.pathSeparator + (bootclasspath2 mkString java.io.File.pathSeparator)

  //-- Show some infos
  //println("compilerPath=" + compilerPath);
  //println("settings.bootclasspath.value=" + settings2.bootclasspath.value);

  // Create Compiler
  //---------------------
  var interpreterOutput = new StringWriter
  val compilerReporter = new PrintWriterReplReporter(settings2,new PrintWriter(interpreterOutput))

  var imain = new IMain(settings2,compilerReporter)

  // Compilation result

  def resetCompiler = {
    imain = new IMain(settings2, compilerReporter)
  }
  
  /**
   * Binds a named variable to a value for the model compiler
   */
  def bind(name: String, value: Any) = {

    imain.bind(name, value)
    // imain.
  }

  /**
   * @return The Model name
   */
  def compile(file: File): ModelInfos = {

    // Get Script input
    //-----------------------
    var inputModel = Source.fromFile(file).mkString

    // Code analyses
    //---------------------

    // use package anylis to determine base model name
    var (modelName, wrappedModel) = """\s*package\s+(.+)\s*""".r.findFirstMatchIn(inputModel) match {

      //-- If there is a package info, the code must be compilable, and we have to use the package as name basis for the object
      case Some(p) => 
       // println("Returning input model")
        (p.group(1), inputModel)

      //-- No package definition, we can wrap the code and add some imports, and the package name stays empty
      case None =>

        ("", s"""
import com.idyria.osi.ooxoo.model._

$inputModel
            """)

    }

    // Determine object name
    //------------
    """object\s+([A-Za-z\w]+)\s+.+""".r.findFirstMatchIn(inputModel) match {
      case Some(matchRes) => modelName = List(modelName, matchRes.group(1)).filterNot(_ == "").mkString(".")
      case None =>
        throw new RuntimeException(s"Could not determine object name of model: $inputModel")
    }

   // println("")

    // Compile
    //--------------
    //println("Compiling: "+wrappedModel)
    imain.compileString(wrappedModel) match {

      // OK -> Return Model
      case true =>

        var modelInfos = new ModelInfos(modelName)

        //println("Compiled Model2: " + modelInfos.name)

        //  imain.allDefSymbols

        /* imain.allDefSymbols.foreach {
                  s => println(s"Available term: "+s)
                }
                imain.allDefinedNames.foreach {
                  n => println(s"Available Name: "+n)
                }*/

        // Get Annotations
        //----------
        imain.bind("modelInfos", modelInfos)
        imain.bind("file", file)

        // Run
        //----------------
        // Interpret
        imain.interpret(s"modelInfos.producers=${modelInfos.name}.producers") match {
          case scala.tools.nsc.interpreter.Results.Error =>

            //println(s"Compilation error: ${interpreterOutput.toString()}")
            throw new RuntimeException(s"Could not interpret content: ${interpreterOutput.toString()}")
          case _ =>
        }

        imain.interpret(s"${modelInfos.name}.sourceFile = file")

        modelInfos

      case false =>
        throw new RuntimeException(s"Could not compile: ${interpreterOutput.toString()}")
    }

  }

  /**
   * Compile and produce a file
   */
  def produce(file: File, producer: ModelProducer, out: Writer): Unit = {

    // Compile
    //-------------------
    var modelInfos = this.compile(file)
    this.produce(modelInfos, producer, out)

  }

  /**
   * Produce an already compiled file
   */
  def produce(modelInfos: ModelInfos, producer: ModelProducer, out: Writer): Unit = {

    //println("Produce compiled model: "+modelInfos.name)

    //imain.bindValue(s"${modelInfos.name}",modelInfos)
    imain.bind("producer", producer)
    imain.bind("writer", out)
    imain.interpret(s"""${modelInfos.name}.name match { case null => ${modelInfos.name}.name = "${modelInfos.name}"; case _ => ;}""")
    /* imain.interpret(s"""
println("Model infos "+${modelInfos.name})
//try {
        if (${modelInfos.name}.name==null) {
    ${modelInfos.name}.name = "${modelInfos.name}"
        }
//} catch {
//    case e : Throwable => 
//}
        """)*/
    imain.interpret(s"${modelInfos.name}.produce(producer,writer)")
  }

}
