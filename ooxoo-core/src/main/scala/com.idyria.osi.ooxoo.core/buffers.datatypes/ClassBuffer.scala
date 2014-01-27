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
