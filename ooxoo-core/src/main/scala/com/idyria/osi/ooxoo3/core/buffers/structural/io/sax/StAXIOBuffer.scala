/**
 *
 */
package com.idyria.osi.ooxoo3.core.buffers.structural.io.sax

import java.io.ByteArrayOutputStream
import java.io.Reader
import com.idyria.osi.ooxoo3.core.buffers.structural.BaseBuffer
import com.idyria.osi.ooxoo3.core.buffers.structural.DataUnit
import com.idyria.osi.ooxoo3.core.buffers.structural.DataUnit
import com.idyria.osi.ooxoo3.core.buffers.structural.io.IOBuffer
import javax.xml.stream.XMLInputFactory
import javax.xml.stream.XMLOutputFactory
import javax.xml.stream.XMLStreamWriter
import com.idyria.osi.ooxoo3.core.buffers.structural.xelement
import com.idyria.osi.ooxoo3.core.buffers.structural.xattribute
import com.idyria.osi.ooxoo3.core.buffers.structural.io.BaseIOBuffer

/**
 * @author rleys
 *
 */
@transient
class StAXIOBuffer(var xmlInput: Reader = null) extends BaseIOBuffer  {

  var output: ByteArrayOutputStream = null

  var eventWriter: XMLStreamWriter = null

  /**
   * Writes the data unit to the output stream, then pass it on
   */
  override def pushOut(du: DataUnit) = {

    // Write
    //-----------
    var documentElement = false

    //-- Create output if none
    if (this.eventWriter == null) {
      this.output = new ByteArrayOutputStream()
      var of = XMLOutputFactory.newInstance()
      this.eventWriter = of.createXMLStreamWriter(this.output)

      // Begin document
      this.eventWriter.writeStartDocument()
      documentElement = true

    }

    //-- Output Element
    //-------------------------
    if (du.element != null && documentElement) {

      println("Stax: Start Element")
      this.eventWriter.writeStartElement(du.element.name)
      
      
    } else if (du.element != null) {

      println(s"Stax: Element ${du.element} / ${du.value}")

      //-- Normal Element
      this.eventWriter.writeStartElement(du.element.name)

      //-- With text content
      this.eventWriter.writeCharacters(du.value)

      //-- Close already if non hierarchical
      if (!du.getHierarchical)
        this.eventWriter.writeEndElement()

    } 
    //-- Output Attribute
    //----------------------------
    else if (du.attribute != null) {

      this.eventWriter.writeAttribute(du.attribute.name, du.value)

    }
    //-- Close Element
    //--------------
    else if (du.attribute==null && du.element==null && du.value==null) {
      this.eventWriter.writeEndElement()
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
  override def fetchIn = {

    // XML input must be provided
    require(this.xmlInput != null)

    // Prepare XML Source
    //------------------------

    // Prepare input
    //-----------------
    var reader = XMLInputFactory.newInstance().createXMLStreamReader(this.xmlInput)

    while (reader.hasNext()) {
      reader.next();

      // Send Start Element
      //----------------------------
      if (reader.isStartElement()) {

        //-- Prepare data unit
        var du = new DataUnit
        du.element = new xelement
        du.element.name = reader.getLocalName()
        du.hierarchical = true
        if (reader.hasText())
          du.value = reader.getText()

        //-- send
        println(s"Produced element DataUnit: " + du.element.name);
        this.fetchIn(du)

        //-- Send attributes if any
        //--------------
        if (reader.getAttributeCount() > 0) {
          for (i <- 0 to reader.getAttributeCount() - 1) {

            //-- Prepare data unit
        	du = new DataUnit
        	du.attribute = new xattribute
        	du.attribute.name = reader.getAttributeName(i).getLocalPart();
        	du.value = reader.getAttributeValue(i)
        	
            //-- send
        	println(s"Produced attribute DataUnit: " + du.attribute.name);
        	this.fetchIn(du)
          }
        }

      }
      // End Element
      //---------------
      else if (reader.isEndElement()) {
        
        // Just send an empty data unit with hiearchical = false
         this.fetchIn(new DataUnit)
         
      }
      else if (reader.isCharacters()) {
        
        // Send a value only event
        var du =  new DataUnit
        du.value = reader.getText()
        this.fetchIn(du)
      }

    }

  }

  def createDataUnit: DataUnit = {

    null

  }

  def cloneIO: IOBuffer = {

    this

  }

  def eofLevel = {

    this.eventWriter.writeEndElement()
    this.eventWriter.flush()

  }

}