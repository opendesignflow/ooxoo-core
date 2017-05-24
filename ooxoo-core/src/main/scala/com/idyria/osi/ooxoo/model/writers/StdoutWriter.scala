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
package com.idyria.osi.ooxoo.model.writers


import com.idyria.osi.ooxoo.model._

import java.io.PrintStream

class PrintStreamWriter( var out : PrintStream ) extends Writer {

    var filesWritten = List[String]()

    /**
        Also saves the path of written file for the fileWritten
    */
    def file(path: String) = {

        out.println(s"---------- File: $path -----------------")

        filesWritten =  path :: filesWritten
    }

    def fileWritten(path:String): Boolean = {

        this.filesWritten.contains(path)

    }

    def <<(str: String) : Writer = {
        out.println(s"${this.indentString}$str")
        this
    }

    def finish = {
        
    }

}

/**
    Simply outputs result to stdout, for example for debugging purpose
*/
class StdoutWriter extends PrintStreamWriter (System.out) {
    
    
}
