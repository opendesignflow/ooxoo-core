package com.idyria.osi.ooxoo.model

import java.io._
import scala.io._

/**
    Produce an output based on:

    - a model
    - an output writer interface


*/
abstract class Producer {

    /**
        To be overriden by Implementations
        Used for example by maven plugin to determine output folder
    */
    var outputType = "undefined"

    def produce(mode: Model, output: Writer)

}

trait Writer {

    // Indentation
    //----------------------
    var indentList = List[String]()

    def indent = this.indentList = "    " :: indentList

    def outdent = this.indentList = indentList.tail

    def indentString = indentList.mkString

    // Output
    //------------

    /**
        Opens a file at provided path
    */
    def file(path: String) : Unit

    /**
        Writes a line to output
    */
    def <<(str: String) : Unit

    /**
        Writes a File to output
    */
    def <<(file:File) : Unit = {
        this.<<(Source.fromFile(file).mkString)
    }

    /**
        Finish writing
    */
    def finish


}
