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

    implicit def convertBooleanBufferToBool( b : BooleanBuffer) : Boolean = b.data
    implicit def convertBoolToBooleanBuffer( b : Boolean) : BooleanBuffer = {

        var bb = new BooleanBuffer
        bb.data = b 
        bb

    }
}
