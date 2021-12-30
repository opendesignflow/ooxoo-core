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

package org.odfi.ooxoo.gradle.plugin

import org.odfi.ooxoo.model.Writer
import org.odfi.ooxoo.model.{Model, ModelBuilder, ModelProducer, Writer, producers}
import org.odfi.tea.compile.IDCompiler

import java.io.{File, PrintWriter, StringWriter}
import java.net.URLClassLoader


/**
 * Return informations about a model
 */
class ModelInfos(

                  var name: String) {

  /**
   * The list of @producer annotation instances declared on model
   */
  var producers: producers = null

  var model: Model = null

}

/*
    The SpeakRunner Object runs a speak script by wrapping it around necessary code and compiling/running

*/
class ModelCompiler {


  // Compiler
  //----------------
  val compiler = new IDCompiler

  // Search for CP for ooxoo library itself
  val mexPath = java.lang.Class.forName("org.odfi.ooxoo.gradle.plugin.ModelCompiler").getProtectionDomain.getCodeSource.getLocation
  compiler.addClasspathURL(mexPath)

  //-- If Classloader is an URL classLoader, add all its urls to the compiler
  //println("Classloader type: "+getClass().getClassLoader())
  getClass.getClassLoader match {
    case value: URLClassLoader =>

      //println("Adding URLs from class loader to boot class path")

      //-- Gather URLS
      compiler.addClasspathURL(value.getURLs)

    case _ =>
  }

  /**
   * @return The Model name
   */
  def compile(file: File): ModelInfos = {

    // Compile
    //-------------



    this.compiler.compileFile(file) match {
      case Left(result) if (result.hasGeneratedTypes) =>

        // Create Model Infos
        var modelInfos = new ModelInfos(result.getFirstGeneratedType)

        // Load
        try {
          result.loadFirstGeneratedClassInstanceOfType[ModelBuilder] match {
            case Left(model) =>

              modelInfos.model = model

              // Get the Annotation for producers
              model.getClass.getAnnotation(classOf[producers]) match {
                case null =>
                case producers => modelInfos.producers = producers
              }

            case Right(err) => throw err
          }
        } catch {
          case e: IllegalArgumentException =>
            /*println("Wrong type")
            val cl = result.loadFirstGeneratedClass
            println(s"T: ${cl.getCanonicalName} <- ${cl.getSuperclass}")
            cl.getInterfaces.foreach {
              i =>
                println(s" - I: ${i} (${classOf[ModelBuilder]==(i)})")
            }*/


            throw e
        }


        modelInfos

      case Left(_) =>
        throw new RuntimeException(s"Model file contains no class")
      case Right(err) =>

        throw new RuntimeException(s"Could not compile: ${err.getLocalizedMessage}")
    }




    // Get Script input
    //-----------------------
    /* var inputModel = Source.fromFile(file).mkString

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
 import org.odfi.ooxoo.model._

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
 */
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
    modelInfos.model.produce(producer, out)
  }

}
