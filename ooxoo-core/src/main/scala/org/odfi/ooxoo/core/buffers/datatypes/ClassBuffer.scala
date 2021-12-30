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

import org.odfi.ooxoo.core.buffers.structural.AbstractDataBuffer

import scala.language.implicitConversions
import scala.reflect.ClassTag

class ClassBuffer[T] extends AbstractDataBuffer[Class[T]] {


  def dataFromString(str: String) = {


    data = Thread.currentThread().getContextClassLoader.loadClass(str).asInstanceOf[Class[T]]
    data
  }

  def dataToString: String = this.toString


  override def toString = data.getCanonicalName


}


class GenericClassBuffer extends AbstractDataBuffer[Class[_]] {


  def dataFromString(str: String) = {


    data = Thread.currentThread().getContextClassLoader().loadClass(str.trim())
    data

  }

  def dataToString: String = this.toString


  override def toString = data.getCanonicalName()


}

object GenericClassBuffer {
  def apply() = new GenericClassBuffer

  implicit def convertFromClassToBuffer(cl: Class[_]): GenericClassBuffer = {

    var b = new GenericClassBuffer
    b.data = cl
    b

  }
}

object ClassBuffer {

  def apply() = new ClassBuffer

  implicit def convertFromClassTagToClassBuffer[T](ct: ClassTag[T]): ClassBuffer[T] = {

    var cb = new ClassBuffer[T]
    cb.data = ct.runtimeClass.asInstanceOf[Class[T]]
    cb
  }

  implicit def convertFromClassToClassBuffer[T](cl: Class[T]): ClassBuffer[T] = {

    var cb = new ClassBuffer[T]
    cb.data = cl
    cb
  }

  implicit def convertFromGenClassToClassBuffer[T](cl: Class[_]): ClassBuffer[T] = {

    var cb = new ClassBuffer[T]
    cb.data = cl.asInstanceOf[Class[T]]
    cb
  }

}
