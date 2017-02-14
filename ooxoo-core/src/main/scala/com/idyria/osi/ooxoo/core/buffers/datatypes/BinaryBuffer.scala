package com.idyria.osi.ooxoo.core.buffers.datatypes

import com.idyria.osi.ooxoo.core.buffers.structural.AbstractDataBuffer
import com.idyria.osi.tea.io.Base64
import java.io.ByteArrayInputStream
import java.io.DataInputStream
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream

class BinaryBuffer extends AbstractDataBuffer[Array[Byte]] {

  def dataFromString(str: String): Array[Byte] = {
    this.data = Base64.decode(str)
    this.data
  }

  def dataToString: String = data match {
    case null => null
    case d => Base64.encodeBytes(d)
  }

  override def toString: String = this.dataToString

}

class IntBinaryBuffer extends AbstractDataBuffer[Array[Int]] {

  def dataFromString(str: String): Array[Int] = {

    //-- Get bytes and read from them
    var bytes = new ByteArrayInputStream(Base64.decode(str))
    var dis = new DataInputStream(bytes)
    var doubles = Vector[Int]()
    while (dis.available() > 0) {
      doubles = doubles :+ dis.readInt()
    }
    dis.close

    this.data = doubles.toArray
    this.data
  }

  def dataToString: String = data match {
    case null => null
    case d =>

      var bytes = new ByteArrayOutputStream()
      var dos = new DataOutputStream(bytes)
      d.foreach(dos.writeInt(_))

      Base64.encodeBytes(bytes.toByteArray())
  }
}

object IntBinaryBuffer {
  
  implicit def convertIntArrayToBuffer(arr:Array[Int]) = {
    
    var b = new IntBinaryBuffer
    b.set(arr)
    b
    
  }
  
  implicit def convertBufferToIntArray(b:IntBinaryBuffer) = {
    
    b.data
    
  }
}

class DoubleBinaryBuffer extends AbstractDataBuffer[Array[Double]] {

  def dataFromString(str: String): Array[Double] = {

    //-- Get bytes and read from them
    var bytes = new ByteArrayInputStream(Base64.decode(str))
    var dis = new DataInputStream(bytes)
    var doubles = Vector[Double]()
    while (dis.available() > 0) {
      doubles = doubles :+ dis.readDouble()
    }
    dis.close

    this.data = doubles.toArray
    this.data
  }

  def dataToString: String = data match {
    case null => null
    case d =>

      var bytes = new ByteArrayOutputStream()
      var dos = new DataOutputStream(bytes)
      d.foreach(dos.writeDouble(_))

      Base64.encodeBytes(bytes.toByteArray())
  }
}

object DoubleBinaryBuffer {
  
  implicit def convertArrayToBuffer(arr:Array[Double]) = {
    
    var b = new DoubleBinaryBuffer
    b.set(arr)
    b
    
  }
  
  implicit def convertBufferToArray(b:DoubleBinaryBuffer) = {
    
    b.data
    
  }
}
