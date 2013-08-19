package com.idyria.osi.ooxoo.model

import java.io._

import scala.io._
import scala.tools.nsc._
import scala.tools.nsc.interpreter._

import scala.runtime._

import java.net._

import scala.collection.JavaConversions._

/**
    Return informations about a model
*/
class ModelInfos(

    var name : String

    ) {

    /**
        The list of @producer annotation instances declared on model
    */
    var producers : producers = null

}

/*
    The SpeakRunner Object runs a speak script by wrapping it around necessary code and compiling/running

*/
object ModelCompiler {

    // Run Statistics
    //---------------------

    /// Number of script runs
    var runCount = 0
    
    // Prepare Class Loader for compiler
    //-------------------------
    var bootclasspath = List[URL]()

    //--- Scala Compiler and library
    val compilerPath = java.lang.Class.forName("scala.tools.nsc.Interpreter").getProtectionDomain.getCodeSource.getLocation
    val libPath = java.lang.Class.forName("scala.Some").getProtectionDomain.getCodeSource.getLocation
    
    //-- Mex
    val mexPath = java.lang.Class.forName("com.idyria.osi.ooxoo.model.ModelCompiler").getProtectionDomain.getCodeSource.getLocation

    bootclasspath = compilerPath :: libPath :: mexPath :: bootclasspath 

    //-- If Classloader is an URL classLoader, add all its urls to the compiler
    //println("Classloader type: "+getClass().getClassLoader())
    if (getClass.getClassLoader.isInstanceOf[URLClassLoader]) {

        //println("Adding URLs from class loader to boot class path")

        //-- Gather URLS
        getClass.getClassLoader.asInstanceOf[URLClassLoader].getURLs().foreach( url => bootclasspath = url ::  bootclasspath )

    }


    // Prepare Compiler Settings Settings
    //----------------
    var settings2 = new GenericRunnerSettings({
        error => println(error)
    })
    settings2.usejavacp.value = true
    settings2.bootclasspath.value = bootclasspath  mkString java.io.File.pathSeparator

    //-- Show some infos
    //println("compilerPath=" + compilerPath);
    //println("settings.bootclasspath.value=" + settings2.bootclasspath.value);

    // Create Compiler
    //---------------------
    val imain = new IMain(settings2)

    // Compilation result

    /**
        @return The Model name
    */
    def compile(file: File) : ModelInfos = {

         // Get Script input
        //-----------------------
        var inputModel = Source.fromFile(file).mkString


        // Wrap Around valid object
        //---------------------
        var wrappedModel = s"""

import com.idyria.osi.ooxoo.model._

$inputModel

        """

        // Determine object name
        //------------
        var modelName = ""
        """object\s+([A-Za-z]+)\s+extends\s+ModelBuilder\s+.+""".r.findFirstMatchIn(inputModel) match {
            case Some(matchRes) => modelName = matchRes.group(1) 
            case None =>
                throw new RuntimeException(s"Could not determine object name of model: $inputModel")
        }

        // Compile
        //--------------
        imain.compileString(wrappedModel) match {

            // OK -> Return Model
            case true =>
                
                var modelInfos = new ModelInfos(modelName)

                // Get Annotations
                imain.bindValue("modelInfos",modelInfos)
                imain.interpret(s"modelInfos.producers=${modelInfos.name}.producers")

                modelInfos

            case false =>
                throw new RuntimeException("Could not compile")
        }


    }

    /**
        Compile and produce a file
    */
    def produce( file: File , producer: Producer, out: Writer) : Unit = {

        // Compile
        //-------------------
        var modelInfos = this.compile(file)
        this.produce(modelInfos,producer,out)
        
    }

    /**
        Produce an already compiled file
    */
    def produce( modelInfos: ModelInfos , producer: Producer, out: Writer)  : Unit = {


        imain.bindValue("producer",producer)
        imain.bindValue("writer",out)
        imain.interpret(s"${modelInfos.name}.produce(producer,writer)")
    }

 
}
