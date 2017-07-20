/*
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
import scala.reflect._
import scala.reflect.runtime.universe._
import scala.language.implicitConversions

class ClassBuffer[T] extends AbstractDataBuffer[Class[T]] {
  
  
  def dataFromString(str:String) = {
    
    
    
    Thread.currentThread().getContextClassLoader().loadClass(str).asInstanceOf[Class[T]]
    
  }
  
  def dataToString : String = this.toString
 
  
  override def toString = data.getCanonicalName()
  
  
}

class GenericClassBuffer extends AbstractDataBuffer[Class[_]] {
  
  
  def dataFromString(str:String) = {
    
    
    
    data = Thread.currentThread().getContextClassLoader().loadClass(str)
    data
    
  }
  
  def dataToString : String = this.toString
 
  
  override def toString = data.getCanonicalName()
  
  
}

object GenericClassBuffer {
  def apply() = new GenericClassBuffer
  
   implicit def convertFromClassToBuffer(cl: Class[_]) : GenericClassBuffer = {
    
    var b = new GenericClassBuffer
    b.data = cl
    b
    
  }
}

object ClassBuffer {
  
  def apply() = new ClassBuffer
  
  implicit def convertFromClassTagToClassBuffer[T](ct: ClassTag[T]) : ClassBuffer[T] = {
    
    var cb = new ClassBuffer[T]
    
    
   
    val cm = runtime.universe.runtimeMirror(getClass.getClassLoader)
    cm.classSymbol(classTag[Long].runtimeClass).toString()
    
   // println("Conversion -> "+cm.classSymbol(classTag[Long].runtimeClass).fullName)
    
    
    cb.data = Thread.currentThread().getContextClassLoader().loadClass(cm.classSymbol(classTag[Long].runtimeClass).fullName).asInstanceOf[Class[T]]
    cb
  }
  
}
