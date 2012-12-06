/**
 *
 */
package com.idyria.osi.ooxoo3.core.buffers.structural.io.sax

import com.idyria.osi.ooxoo3.core.buffers.structural.BaseBuffer
import com.idyria.osi.ooxoo3.core.buffers.structural.DataUnit
import javax.xml.stream.XMLOutputFactory
import java.io.BufferedWriter
import java.io.ByteArrayOutputStream
import javax.xml.stream.XMLStreamWriter
import com.idyria.osi.ooxoo3.core.buffers.structural.io.IOBuffer

/**
 * @author rleys
 *
 */
@transient
class StAXIOBuffer extends BaseBuffer with IOBuffer {

  var output : ByteArrayOutputStream = null
  
  var eventWriter : XMLStreamWriter = null
  
  /**
   * Writes the data unit to the output stream, then pass it on
   */
  override def pushOut(du: DataUnit) = {
    
    
    
    // Write
    //-----------
    var documentElement = false
    
    //-- Create output if none
    if (this.eventWriter==null)  {
    	this.output = new ByteArrayOutputStream()
    	var of = XMLOutputFactory.newInstance()
    	this.eventWriter = of.createXMLStreamWriter(this.output)
    	
    	// Begin document
    	this.eventWriter.writeStartDocument()
    	documentElement = true
    	
    }
    
    //-- Output Element
    if (du.element!=null && documentElement) {
      
      println("Stax: Start Element")
      this.eventWriter.writeStartElement(du.element.name())
     
    } else if (du.element!=null) {
      
      println(s"Stax: Element ${du.element} / ${du.value}")
      
      //-- Normal Element
      this.eventWriter.writeStartElement(du.element.name())
     
      //-- With text content
      this.eventWriter.writeCharacters(du.value)
      
      //-- Close already if non hierarchical
      if (!du.getHierarchical)
        this.eventWriter.writeEndElement()
      
    }
    //-- Output Attribute
    else if (du.attribute!=null) {
      
      this.eventWriter.writeAttribute(du.attribute.name(), du.value)
      
    }
    
    
    // Pass it on
    //----------------
    super.pushOut(du)
    
    // Close if necessary
    //---------------
    /*if (du.element!=null) {
      
      this.eventWriter.writeEndElement()
      
    }*/
    
    this.eventWriter.flush()
    
  }
  
  
  def createDataUnit : DataUnit = {
    
    null
    
  }
  
  def cloneIO : IOBuffer = {
    
    this
    
  }
  
  def eofLevel = {
    
    this.eventWriter.writeEndElement()
    this.eventWriter.flush()
    
  }
  
}