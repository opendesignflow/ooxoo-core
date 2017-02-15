package com.idyria.osi.ooxoo.core.buffers.datatypes

import com.idyria.osi.ooxoo.core.buffers.structural.AbstractDataBuffer
import com.idyria.osi.tea.io.Base64
import java.io.ByteArrayInputStream
import java.io.DataInputStream
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream
import com.idyria.osi.ooxoo.core.buffers.structural.DataUnit

class BinaryBuffer extends AbstractDataBuffer[Array[Byte]] {

  var base64Buffer = ""

  override def streamIn(du: DataUnit) = {
    super.streamIn(du)
    if (du.isHierarchyClose) {

      this.data = Base64.decode(base64Buffer)
      base64Buffer = ""
    }
  }

  def dataFromString(str: String): Array[Byte] = {
    base64Buffer += str.trim

    this.data
  }

  def dataToString: String = data match {
    case null => null
    case d => Base64.encodeBytes(d)
  }

  override def toString: String = this.dataToString

}

class IntBinaryBuffer extends AbstractDataBuffer[Array[Int]] {

  var base64Buffer = ""

  override def streamIn(du: DataUnit) = {
    super.streamIn(du)
    if (du.isHierarchyClose) {

      //-- Get bytes and read from them
      var bytes = new ByteArrayInputStream(Base64.decode(base64Buffer))
      base64Buffer = ""

      //println("Decoding: " + bytes.available())

      var dis = new DataInputStream(bytes)
      var ints = Vector[Int]()
      while (dis.available() > 0) {
        ints = ints :+ dis.readInt()
      }
      dis.close

      this.data = ints.toArray
    }
  }

  def dataFromString(str: String): Array[Int] = {

    //println("From bytes: " + str)
    base64Buffer += str.trim

    this.data
  }

  def dataToString: String = data match {
    case null => null
    case d =>

      // println("Encoding ints")
      var bytes = new ByteArrayOutputStream()
      var dos = new DataOutputStream(bytes)
      d.foreach(dos.writeInt(_))

      Base64.encodeBytes(bytes.toByteArray())
  }
}

object IntBinaryBuffer {

  implicit def convertIntArrayToBuffer(arr: Array[Int]) = {

    var b = new IntBinaryBuffer
    b.set(arr)
    b

  }

  implicit def convertBufferToIntArray(b: IntBinaryBuffer) = {

    b.data

  }
}

class DoubleBinaryBuffer extends AbstractDataBuffer[Array[Double]] {

  var base64Buffer = ""

  override def streamIn(du: DataUnit) = {
    super.streamIn(du)
    if (du.isHierarchyClose) {

      //-- Get bytes and read from them
      var bytes = new ByteArrayInputStream(Base64.decode(base64Buffer))
      base64Buffer = ""

      var dis = new DataInputStream(bytes)
      var doubles = Vector[Double]()
      while (dis.available() > 0) {
        doubles = doubles :+ dis.readDouble()
      }
      dis.close

      this.data = doubles.toArray

    }
  }

  def dataFromString(str: String): Array[Double] = {

    base64Buffer += str.trim

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

  implicit def convertArrayToBuffer(arr: Array[Double]) = {

    var b = new DoubleBinaryBuffer
    b.set(arr)
    b

  }

  implicit def convertBufferToArray(b: DoubleBinaryBuffer) = {

    b.data

  }
}
