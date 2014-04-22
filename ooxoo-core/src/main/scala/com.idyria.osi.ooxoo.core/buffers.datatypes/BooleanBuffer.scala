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
package com.idyria.osi.ooxoo.core.buffers.datatypes

import com.idyria.osi.ooxoo.core.buffers.structural.AbstractDataBuffer

import scala.language.implicitConversions

class BooleanBuffer extends AbstractDataBuffer[java.lang.Boolean] with Comparable[java.lang.Boolean] {


    def dataFromString(str: String) : java.lang.Boolean = {
        this.data = java.lang.Boolean.parseBoolean(str)
        this.data
    }

    def dataToString : String = if (data!=null) this.data.toString() else null

    override def toString : String = this.dataToString

    def equals(comp: BooleanBuffer): java.lang.Boolean = this.data == comp.data

    def compareTo(comp: java.lang.Boolean ) : Int = this.data.compareTo(comp)

   

}
 
object BooleanBuffer {

	def apply() : BooleanBuffer = false
  
    implicit def convertBooleanBufferToBool( b : BooleanBuffer) : Boolean = b.data
    implicit def convertBoolToBooleanBuffer( b : Boolean) : BooleanBuffer = {

        var bb = new BooleanBuffer
        bb.data = b 
        bb

    }
}