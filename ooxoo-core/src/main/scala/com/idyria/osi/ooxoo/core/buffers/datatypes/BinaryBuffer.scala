/*-
 * #%L
 * Core runtime for OOXOO
 * %%
 * Copyright (C) 2006 - 2017 Open Design Flow
 * %%
 * This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */
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
