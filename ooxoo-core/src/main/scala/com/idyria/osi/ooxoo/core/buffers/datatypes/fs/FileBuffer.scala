/*-
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
package com.idyria.osi.ooxoo.core.buffers.datatypes.fs

import com.idyria.osi.ooxoo.core.buffers.structural.AbstractDataBuffer
import java.io.File
import java.net.URI

import scala.language.implicitConversions

class FileBuffer extends AbstractDataBuffer[File] {

    def this(str: String) = {
        this();
        //println("File Buffer constructor: "+str)
        str match {
            case null =>
            case s    => this.data = dataFromString(s)
        }

    }
    def dataFromString(str: String) = {
       // println("FIle Data from String: " + str)
        this.data = new File(new URI(str).getPath)

        this.data
    }
    def dataToString = data match {
        case null  => ""
        case other => this.data.toURI().toString()
    }

    override def toString: String = {
        data match {
            case null  => ""
            case other => other.toString
        }
    }

}

object FileBuffer {

    implicit def fToB(f: File) = {
        val b = new FileBuffer
        b.set(f)
        b
    }

    implicit def sToB(f: String) = {
        val b = new FileBuffer(f)
        b
    }

    implicit def bToF(f: FileBuffer) = f.data
}
