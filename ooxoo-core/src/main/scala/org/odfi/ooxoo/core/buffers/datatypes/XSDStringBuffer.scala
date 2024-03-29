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
package org.odfi.ooxoo.core.buffers.datatypes

import scala.language.implicitConversions
import org.odfi.ooxoo.core.buffers.structural.{AbstractDataBuffer, DataUnit}

/**
 * Buffer used to define a string
 * @author rleys
 *
 */
class XSDStringBuffer extends AbstractDataBuffer[String] with Comparable[String] {

    def this(str: String) = {
        this();
        str match {
            case null =>
            case s    => this.data = dataFromString(s)
        }

    }

    def dataToString: String = {
        this.data

    }

    /**
     * Set provided string to actual data
     */
    def dataFromString(str: String): String = str match {
        case null => null
        case s if (data == null) =>
            //println(s"Data from strng with: "+str)
            /*try {
        throw new RuntimeException("test")
      }catch {
        case e: Throwable => e.printStackTrace(System.out)
      }*/
            //this.data = str.trim;
            //data
            this.data = str.trim
            this.data
        case s =>
            /* println(s"Data from strng with: "+str)
       try {
        throw new RuntimeException("test")
      }catch {
        case e: Throwable =>  e.printStackTrace(System.out)
      }*/
            this.data = this.data + s.trim
            this.data

        //data
    }

    override def toString: String = {
        if (this.data == null)
            super.toString
        this.data

    }

    def toEmptyStringIfNull = {
        this.data match {
            case null  => ""
            case other => this.toString
        }
    }

    def equals(comp: XSDStringBuffer): Boolean = {
        //println("Called equals to xsdstringbuffer")
        this.data.equals(comp.data)
    }

    def equals(comp: String): Boolean = {

        //println("Called equals to String")
        this.data == comp
    }

    def compareTo(comp: String): Int = {

        //println("Called compare to to xsdstringbuffer")
        this.data.compareTo(comp)
    }

    def ===(str: String): Boolean = {
        // println(s"in equals compare")
        this.data == str
    }

    def isEmptyString = {
        this.toString == "" || this.toString == null
    }

    /* implicit def convertSubClassesToStringBufferType[T <: XSDStringBuffer](str:String) : T = {


   var r = this.getClass.newInstance()
    r.dataFromString(str)
   r.asInstanceOf[T]
  }*/

}
object XSDStringBuffer {

    def apply(str: String) = new XSDStringBuffer(str)
    def apply() = new XSDStringBuffer

    implicit def convertAnyToXSDStringBuffer(str: Any): XSDStringBuffer = new XSDStringBuffer(str.toString)
    implicit def convertStringToXSDStringBuffer(str: String): XSDStringBuffer = new XSDStringBuffer(str)
    implicit def convertXSDStringBufferToString(str: XSDStringBuffer): String = {
        str match {
            case null  => ""
            case other => other.toString()
        }
    }

    def convertFromString(data: String): XSDStringBuffer = new XSDStringBuffer(data)
}

class CDataBuffer extends XSDStringBuffer {

    def this(str: String) = {
        this();
        str match {
            case null =>
            case s =>
                this.data = dataFromString(s)
        }

    }

    /**
     * Override streamout to add cdata parameter to data unit
     */
    override def streamOut(du: DataUnit) = {

        du("cdata" -> true)

        super.streamOut(du)
    }

}
object CDataBuffer {

    def apply(str: String) = new CDataBuffer(str)
    def apply() = new CDataBuffer
    implicit def convertStringToCDataBuffer(str: String): CDataBuffer = new CDataBuffer(str)
    def convertFromString(data: String): CDataBuffer = new CDataBuffer(data)

}

class StringMapBuffer extends MapBuffer[XSDStringBuffer]({ du => new XSDStringBuffer })

object StringMapBuffer {
    def apply() = new StringMapBuffer
}
