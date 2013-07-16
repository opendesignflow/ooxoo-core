package com.idyria.osi.ooxoo.core.buffers.datatypes

import com.idyria.osi.ooxoo.core.buffers.structural.AbstractDataBuffer


/**
    Buffer to represent an Integer

*/
class IntBuffer extends AbstractDataBuffer[Integer] with Comparable[Integer] {


    def dataFromString(str: String) : Integer = {
        this.data = Integer.getInteger(str)
        this.data
    }

    def dataToString : String = this.data.toString()

    override def toString : String = this.dataToString

    def equals(comp: IntBuffer): Boolean = this.data == comp.data

    def compareTo(comp: Integer ) : Int = this.data.compareTo(comp)

}

object IntBuffer {

    def apply( value: Integer) = {
        var res = new IntBuffer
        res.data = value
        res
    }

    implicit def convertIntegerToIntBuffer(value: Integer): IntBuffer = IntBuffer(value)
    implicit def convertIntBufferToInteger(buffer: IntBuffer): Integer = buffer.data

}

/**
    Buffer to represent a Double

*/
class DoubleBuffer extends AbstractDataBuffer[java.lang.Double] with Comparable[java.lang.Double] {


    def dataFromString(str: String) : java.lang.Double = {
        this.data = java.lang.Double.parseDouble(str)
        this.data
    }

    def dataToString : String = this.data.toString()

    override def toString : String = this.dataToString

    def equals(comp: DoubleBuffer): Boolean = this.data == comp.data

    def compareTo(comp: java.lang.Double ) : Int = this.data.compareTo(comp)

}

object DoubleBuffer {

    def apply( value: java.lang.Double) = {
        var res = new DoubleBuffer
        res.data = value
        res
    }

    implicit def convertDoubleToDoubleBuffer(value: java.lang.Double): DoubleBuffer = DoubleBuffer(value)
    implicit def convertDoubleBufferToDouble(buffer: DoubleBuffer): java.lang.Double = buffer.data

}


/**
    Buffer to represent a Float

*/
class FloatBuffer extends AbstractDataBuffer[java.lang.Float] with Comparable[java.lang.Float] {


    def dataFromString(str: String) : java.lang.Float = {
        this.data = java.lang.Float.parseFloat(str)
        this.data
    }

    def dataToString : String = this.data.toString()

    override def toString : String = this.dataToString

    def equals(comp: FloatBuffer): Boolean = this.data == comp.data

    def compareTo(comp: java.lang.Float ) : Int = this.data.compareTo(comp)

}

object FloatBuffer {

    def apply( value: java.lang.Float) = {
        var res = new FloatBuffer
        res.data = value
        res
    }

    implicit def convertFloatToFloatBuffer(value: java.lang.Float): FloatBuffer = FloatBuffer(value)
    implicit def convertFloatBufferToFloat(buffer: FloatBuffer): java.lang.Float = buffer.data

}
