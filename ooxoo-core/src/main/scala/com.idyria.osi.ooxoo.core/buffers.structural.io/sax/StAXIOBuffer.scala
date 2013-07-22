/**
 *
 */
package com.idyria.osi.ooxoo.core.buffers.structural.io.sax

import java.io.ByteArrayOutputStream
import java.io.Reader
import com.idyria.osi.ooxoo.core.buffers.structural.BaseBuffer
import com.idyria.osi.ooxoo.core.buffers.structural.DataUnit
import com.idyria.osi.ooxoo.core.buffers.structural.DataUnit
import com.idyria.osi.ooxoo.core.buffers.structural.io.IOBuffer
import javax.xml.stream.XMLInputFactory
import javax.xml.stream.XMLOutputFactory
import javax.xml.stream.XMLStreamWriter
import com.idyria.osi.ooxoo.core.buffers.structural.xelement_base
import com.idyria.osi.ooxoo.core.buffers.structural.xattribute_base
import com.idyria.osi.ooxoo.core.buffers.structural.io.BaseIOBuffer

import com.idyria.osi.tea.logging.TLog

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
  override def streamOut(du: DataUnit) = {

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

      TLog.logFine("Stax: Start Element")
      this.eventWriter.writeStartElement(du.element.name)


    } else if (du.element != null) {

      TLog.logFine(s"Stax: Element ${du.element.name} / ${du.value}")

      //-- Normal Element
      this.eventWriter.writeStartElement(du.element.name)

      //-- With text content
      if (du.value != null)
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
    super.streamOut(du)

    // Close if necessary
    //---------------
    /*if (du.element!=null) {

      this.eventWriter.writeEndElement()

    }*/

    this.eventWriter.flush()

  }
  override def streamIn = {

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
        du.element = new xelement_base()
        du.element.name = reader.getLocalName()
        du.hierarchical = true
        if (reader.hasText())
          du.value = reader.getText()

        //-- send
        TLog.logFine(s"Produced element DataUnit: " + du.element.name);
        this.streamIn(du)

        //-- Send attributes if any
        //--------------
        if (reader.getAttributeCount() > 0) {
          for (i <- 0 to reader.getAttributeCount() - 1) {

            //-- Prepare data unit
        	du = new DataUnit
        	du.attribute = new xattribute_base
        	du.attribute.name = reader.getAttributeName(i).getLocalPart();
        	du.value = reader.getAttributeValue(i)

            //-- send
        	TLog.logFine(s"Produced attribute DataUnit: " + du.attribute.name);
        	this.streamIn(du)
          }
        }

      }
      // End Element
      //---------------
      else if (reader.isEndElement()) {

        // Just send an empty data unit with hiearchical = false
         this.streamIn(new DataUnit)

      }
      else if (reader.isCharacters()) {

        // Send a value only event
        var du =  new DataUnit
        du.value = reader.getText()
        this.streamIn(du)
      }

    }

  }

  def cloneIO: IOBuffer = {

    this

  }

  def eofLevel = {

    this.eventWriter.writeEndElement()
    this.eventWriter.flush()

  }

}
