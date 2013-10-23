package com.idyria.osi.ooxoo.core.buffers.datatypes

import com.idyria.osi.ooxoo.core.buffers.structural.AbstractDataBuffer
import java.net.URI



class URIBuffer extends AbstractDataBuffer[URI] {
  
  // Constructors
  //-------------------
  
  def this(str: String) = { this(); dataFromString(str) }

  def this(uri:URI) = {
    this()
    this.data = uri
  }
  
  def dataToString: String = {
    this.data.toASCIIString()
    
  }

  /**
   * Set provided string to actual data
   */
  def dataFromString(str: String): URI =  {this.data = URI.create(str);data}


  override def toString: String = {
    if (this.data==null)
      super.toString
    this.data.toASCIIString()

  }

  def equals(comp: URIBuffer): Boolean = {
    //println("Called equals to xsdstringbuffer")
    this.data.equals(comp.data) 
  }
  
 
  def equals(comp: URI): Boolean = {
    
    //println("Called equals to String")
    this.data.equals(comp)
  }

  def compareTo(comp:URI) : Int = {
    
    //println("Called compare to to xsdstringbuffer")
     this.data.compareTo(comp)
  }
  
}

object URIBuffer {
  
  implicit def convertFromStringToURIBuffer(str:String) : URIBuffer = new URIBuffer(str)
  implicit def convertFromURItoURIBuffer(uri: URI) : URIBuffer = new URIBuffer(uri)
  
}
