package com.idyria.osi.ooxoo.core.buffers.datatypes.fs

import com.idyria.osi.ooxoo.core.buffers.structural.AbstractDataBuffer
import java.io.File
import java.net.URI

class FileBuffer extends AbstractDataBuffer[File] {
  
  
   def dataFromString(str: String) = new File(new URI(str).getPath)
   def dataToString = this.data.toURI().toString()
   
   override def toString: String = {
     data.toString()
   }
  
}

object FileBuffer {
  
  implicit def fToB(f:File) = {
    val b = new FileBuffer
    b.set(f)
    b
  }
  
  implicit def sToB(f:String) = {
    val b = new FileBuffer
    b.set(new File(f))
    b
  }
  
  implicit def bToF(f:FileBuffer) = f.data
}