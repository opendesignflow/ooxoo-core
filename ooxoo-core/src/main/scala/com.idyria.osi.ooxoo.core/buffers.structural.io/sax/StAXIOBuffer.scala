/**
 *
 */
package com.idyria.osi.ooxoo.core.buffers.structural.io.sax

import java.io.ByteArrayOutputStream
import java.io.Reader
import java.io.StringReader
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

import com.idyria.osi.tea.logging._

/**
 * @author rleys
 *
 */
@transient
class StAXIOBuffer(var xmlInput: Reader = null) extends BaseIOBuffer  with TLogSource {

  // Stream in parameters 
  //-----------------------

  var output: ByteArrayOutputStream = null

  var eventWriter: XMLStreamWriter = null


  // Stream out parameters
  //-----------------------
  var namespacePrefixesMap = Map[String,String]()

  /**
    Returns the last set prefix for the namespace, or generate one if non existent
  */
  def getPrefixForNamespace( ns : String) : String = this.namespacePrefixesMap.getOrElse(ns, {s"ns${this.namespacePrefixesMap.size}"})
 
 /*   this.namespacePrefixesMap.get(ns) match {
      case None => 

      case Some()
    }

  }*/

  /**
   * Writes the data unit to the output stream, then pass it on
   */
  override def streamOut(du: DataUnit) : Unit = {

    // Fetch Prefixes from data unit context
    //-----------------
    du("prefixes") match {
      case Some(mapObject) if (mapObject.isInstanceOf[Map[String,String]]) =>

            this.namespacePrefixesMap = this.namespacePrefixesMap ++ mapObject.asInstanceOf[Map[String,String]]
      case Some(mapObject) => 
      case None =>  
    }

    // Write
    //-----------
    var documentElement = false

    //-- Create output if none
    if (this.eventWriter == null) {

      this.output = new ByteArrayOutputStream()

      var of = XMLOutputFactory.newInstance()
      of.setProperty("javax.xml.stream.isRepairingNamespaces",true);

      this.eventWriter = of.createXMLStreamWriter(this.output)

      // Begin document
      this.eventWriter.writeStartDocument()
      documentElement = true

    }

    //-- Output Element
    //-------------------------
    if (du.element != null && documentElement) {

      //println("Stax: Start Element ${du.element.name}")
      du.element.ns match {
        case "" =>  this.eventWriter.writeStartElement(du.element.name)
        case _  =>  this.eventWriter.writeStartElement(getPrefixForNamespace(du.element.ns),du.element.name,du.element.ns)
      }
      


    } else if (du.element != null) {

      //println(s"Stax: Element ${du.element.name} / ${du.value} on ${this.eventWriter}")

      //-- Normal Element
      du.element.ns match {
        case "" =>  this.eventWriter.writeStartElement(du.element.name)
        case _  =>  this.eventWriter.writeStartElement(getPrefixForNamespace(du.element.ns),du.element.name,du.element.ns)
      }

      //-- With text content
      if (du.value != null)
    	  this.eventWriter.writeCharacters(du.value)

      //-- Close already if non hierarchical
      if (!du.getHierarchical) {
          //println(s"-> Closing already!")
        this.eventWriter.writeEndElement()

      }

    }
    //-- Output Attribute
    //----------------------------
    else if (du.attribute != null) {

      
      // try {
      du.attribute.ns match {
        case "" =>  
          //println(s"--> Attribute ${du.attribute.name} / ${du.value}")
          this.eventWriter.writeAttribute(du.attribute.name, du.value)
        case _  =>  
          //println(s"--> NS Attribute ${du.attribute.name} / ${du.value}")
          this.eventWriter.writeAttribute(getPrefixForNamespace(du.attribute.ns),du.attribute.ns,du.attribute.name, du.value)
      }
      // } catch {
      //  case e : Throwable => println(s"--> Failed Attribute ${du.attribute.name} on ${this.eventWriter}")
      //} 

    }
    //-- Close Element
    //--------------
    else if (du.attribute==null && du.element==null && du.value==null) {

      //println("StaxIO Closing")
      //try {
        this.eventWriter.writeEndElement()
      //} catch {
        //case e : Throwable => 
     // } 
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
        logFine(s"Produced element DataUnit: " + du.element.name);
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
        	logFine(s"Produced attribute DataUnit: " + du.attribute.name);
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

object StAXIOBuffer {


  /**
    Creates a StAXIOBuffer with initial content to the provided string, ready to be streamed in
  */
  def apply(str : String) = {

    new StAXIOBuffer(new StringReader(str))

  }
}
