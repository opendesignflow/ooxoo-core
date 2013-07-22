package com.idyria.osi.ooxoo.core.buffers.datatypes

import com.idyria.osi.ooxoo.core.buffers.structural.AbstractDataBuffer


/**
    Buffer to represent an Integer

*/
class IntegerBuffer extends AbstractDataBuffer[Integer] with Comparable[Integer] {


    def dataFromString(str: String) : Integer = {
        this.data = Integer.decode(str)
        this.data
    }

    def dataToString : String = this.data.toString()

    override def toString : String = this.dataToString

    def equals(comp: IntegerBuffer): Boolean = this.data == comp.data

    def compareTo(comp: Integer ) : Int = this.data.compareTo(comp)

}

object IntegerBuffer {

    def apply( value: Integer) = {
        var res = new IntegerBuffer
        res.data = value
        res
    }

    implicit def convertIntegerToIntegerBuffer(value: Integer): IntegerBuffer = IntegerBuffer(value)
    implicit def convertIntegerBufferToInteger(buffer: IntegerBuffer): Integer = buffer.data

}

/**
    Buffer to represent a Long

*/
class LongBuffer extends AbstractDataBuffer[java.lang.Long] with Comparable[java.lang.Long] {


    def dataFromString(str: String) : java.lang.Long = {
        this.data = java.lang.Long.decode(str)
        return this.data
    }

    def dataToString : String = this.data.toString()

    override def toString : String = this.dataToString

    def equals(comp: LongBuffer): Boolean = this.data == comp.data

    def compareTo(comp: java.lang.Long ) : Int = this.data.compareTo(comp)

}

object LongBuffer {

    def apply( value: java.lang.Long) = {
        var res = new LongBuffer
        res.data = value
        res
    }

    implicit def convertLongToLongBuffer(value: java.lang.Long): LongBuffer = LongBuffer(value)
    implicit def convertLongBufferToLong(buffer: LongBuffer): java.lang.Long = buffer.data

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
