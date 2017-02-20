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

/**
 * Buffer to represent an Int
 *
 */
class IntegerBuffer extends AbstractDataBuffer[Int] with Comparable[Int] {

  this.data = 0
  
  def dataFromString(str: String): Int = {
    this.data = Integer.decode(str)
    this.data
  }

  def dataToString: String =  data.toString

  override def toString: String = this.dataToString

  def equals(comp: IntegerBuffer): Boolean = this.data == comp.data

  def compareTo(comp: Int): Int = this.data.compareTo(comp)

}

object IntegerBuffer {

  def apply() = new IntegerBuffer

  def apply(value: Int) = {
    var res = new IntegerBuffer
    res.data = value
    res
  }
  
  def convertFromString( str : String) : IntegerBuffer = {
    
    var b = new IntegerBuffer
    b.dataFromString(str)
    b
  }
  
  implicit def convertIntToIntegerBuffer(value: Int): IntegerBuffer = IntegerBuffer(value)
  implicit def convertIntegerBufferToInt(buffer: IntegerBuffer): Int = buffer.data

}

/**
 * Buffer to represent a Long
 *
 */
class LongBuffer extends AbstractDataBuffer[Long] with Comparable[Long] {

  this.data = 0
  
  def dataFromString(str: String): Long = {
    
    str.trim match {
      case "" =>
      case r => 
        this.data = java.lang.Long.decode(r)
    }
    
    return this.data
  }

  def dataToString: String =  data.toString

  override def toString: String = this.dataToString

  def equals(comp: LongBuffer): Boolean = this.data == comp.data

  def compareTo(comp: Long): Int = this.data.compareTo(comp)

  def +(add: Long): Long = this.data + add
}

object LongBuffer {

  def apply() = new LongBuffer

  def apply(value: Long) = {
    var res = new LongBuffer
    res.data = value
    res
  }
  
  def convertFromString( str : String)  = {
    
    var b = new LongBuffer
    b.dataFromString(str)
    b
  }

  implicit def convertLongToLongBuffer(value: Long): LongBuffer = LongBuffer(value)
  implicit def convertLongBufferToLong(buffer: LongBuffer): Long = buffer.data
  
  
}

/**
 * Buffer to represent a Double
 *
 */
class DoubleBuffer extends AbstractDataBuffer[Double] with Comparable[Double] {

  this.data = 0.0
  
  def dataFromString(str: String): Double = {
    this.data = java.lang.Double.parseDouble(str)
    this.data
  }

  def dataToString: String = data.toString

  override def toString: String = this.dataToString

  def equals(comp: DoubleBuffer): Boolean = this.data == comp.data

  def compareTo(comp: Double): Int = this.data.compareTo(comp)

}

object DoubleBuffer {

  def apply() = new DoubleBuffer

  def apply(value: java.lang.Double) = {
    var res = new DoubleBuffer
    res.data = value
    res
  }
  
  def convertFromString( str : String)  = {
    
    var b = new DoubleBuffer
    b.dataFromString(str)
    b
  }

  implicit def convertDoubleToDoubleBuffer(value: Double): DoubleBuffer = DoubleBuffer(value)
  implicit def convertDoubleBufferToDouble(buffer: DoubleBuffer): Double = buffer.data

}

/**
 * Buffer to represent a Float
 *
 */
class FloatBuffer extends AbstractDataBuffer[Float] with Comparable[Float] {

  this.data = 0.0f
  
  def dataFromString(str: String): Float = {
    this.data = java.lang.Float.parseFloat(str)
    this.data
  }

  def dataToString: String =  data.toString

  override def toString: String = this.dataToString

  def equals(comp: FloatBuffer): Boolean = this.data == comp.data

  def compareTo(comp: Float): Int = this.data.compareTo(comp)

}

object FloatBuffer {

  def apply() = new FloatBuffer

  def apply(value: Float) = {
    var res = new FloatBuffer
    res.data = value
    res
  }

  def convertFromString( str : String)  = {
    
    var b = new FloatBuffer
    b.dataFromString(str)
    b
  }
  
  implicit def convertFloatToFloatBuffer(value: Float): FloatBuffer = FloatBuffer(value)
  implicit def convertFloatBufferToFloat(buffer: FloatBuffer): Float = buffer.data


}
