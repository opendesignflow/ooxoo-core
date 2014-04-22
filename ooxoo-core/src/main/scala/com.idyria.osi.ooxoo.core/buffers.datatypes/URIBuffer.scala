package com.idyria.osi.ooxoo.core.buffers.datatypes

import com.idyria.osi.ooxoo.core.buffers.structural.AbstractDataBuffer
import java.net.URI
import scala.language.implicitConversions


class URIBuffer extends AbstractDataBuffer[URI] {
  
  // Constructors
  //-------------------
  
  def this(str: String) = { this();

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
dataFromString(str) }

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
  
  def apply() = new URIBuffer
  
  implicit def convertFromStringToURIBuffer(str:String) : URIBuffer = new URIBuffer(str)
  implicit def convertFromURItoURIBuffer(uri: URI) : URIBuffer = new URIBuffer(uri)
  
}