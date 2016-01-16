/*
 * #%L
 * Core runtime for OOXOO
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
package com.idyria.osi.ooxoo.model

import java.io._
import scala.io._

/**
    Produce an output based on:

    - a model
    - an output writer interface


*/
abstract class ModelProducer {

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

    def indentCount = indentList.size

    // Output
    //------------

    /**
        Opens a file at provided path
    */
    def file(path: String) : Unit

    /**
        Returns true if the file for the given path has already been written
    */
    def fileWritten(path:String) : Boolean

    /**
        Writes a line to output
    */
    def <<(str: String) : Writer

    /**
        Writes a File to output
    */
    def <<(file:File) : Writer = {
        this.<<(Source.fromFile(file).mkString)
        this
    }

    /**
        Finish writing
    */
    def finish


}
