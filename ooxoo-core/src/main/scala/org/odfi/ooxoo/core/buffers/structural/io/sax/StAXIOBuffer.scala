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
package org.odfi.ooxoo.core.buffers.structural.io.sax

import com.sun.xml.txw2.output.IndentingXMLStreamWriter
import org.odfi.ooxoo.core.buffers.structural.io.{BaseIOBuffer, IOBuffer}
import org.odfi.ooxoo.core.buffers.structural.{DataUnit, ElementBuffer, xattribute_base, xelement_base}
import org.odfi.tea.logging.TLogSource
import org.w3c.dom.Node

import java.io.{ByteArrayOutputStream, InputStream, InputStreamReader, OutputStream, Reader, StringReader, StringWriter}
import java.net.URL
import javax.xml.stream.{XMLInputFactory, XMLOutputFactory, XMLStreamWriter}
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult
import scala.collection.convert.AsJavaConverters

/**
 * @author rleys
 *
 */
@transient
class StAXIOBuffer(var xmlInput: Reader = null, var xmlNode: Node = null) extends BaseIOBuffer with TLogSource with AsJavaConverters {

  // Constructors
  //----------------
  def this(url: URL) = this(new InputStreamReader(url.openStream()))
  def this(stream: InputStream) = this(new InputStreamReader(stream))

  // Stream in parameters
  //-----------------------

  var output: OutputStream = null

  var outputBytesWritten = 0

  var eventWriter: XMLStreamWriter = null

  // Stream out parameters
  //-----------------------

  var indenting = false

  /**
   * ns/prefix
   */
  var namespacePrefixesMap = Map[String, String]()

  /**
   * Returns the last set prefix for the namespace, or generate one if non existent
   */
  def getPrefixForNamespace(ns: String): String = this.namespacePrefixesMap.getOrElse(ns, { s"ns${this.namespacePrefixesMap.size}" })

  /*   this.namespacePrefixesMap.get(ns) match {
      case None =>

      case Some()
    }

  }*/

  var currenText = ""

  /**
   * Writes the data unit to the output stream, then pass it on
   */
  override def streamOut(du: DataUnit): Unit = {

    // Fetch Prefixes from data unit context
    //-----------------
    du("prefixes") match {
      case Some(mapObject) if (mapObject.isInstanceOf[Map[_, _]]) =>

        this.namespacePrefixesMap = this.namespacePrefixesMap ++ mapObject.asInstanceOf[Map[String, String]]
      case Some(mapObject) =>
      case None =>
    }

    // Write
    //-----------
    var documentElement = false

    //-- Create output if none
    if (this.output == null) {

      this.output = new ByteArrayOutputStream()

    }

    //-- Create Event Writer
    if (this.eventWriter == null) {

      var of = XMLOutputFactory.newInstance()
      of.setProperty("javax.xml.stream.isRepairingNamespaces", true);

      this.eventWriter = this.indenting match {
        case true => new IndentingXMLStreamWriter(of.createXMLStreamWriter(this.output));
        case false => of.createXMLStreamWriter(this.output)
      }

      /*this.eventWriter.setNamespaceContext(new NamespaceContext with AsJavaConverters {
        def getNamespaceURI(prefix: String) = {

          namespacePrefixesMap.find {
            case (ns, p) => p == prefix
          } match {
            case Some((ns, _)) => ns
            case other => null
          }

        }

        def getPrefix(ns: String) = {
          namespacePrefixesMap.find {
            case (n, p) => ns == n
          } match {
            case Some((_, p)) => p
            case other => null
          }
        }

        def getPrefixes(ns: String) = getPrefix(ns) match {
          case null => asJavaIterator(List[String]().toIterator)
          case found => asJavaIterator(List(found).toIterator)
        }
      })
      namespacePrefixesMap.find {
        case ((ns, "")) => true
        case other => false

      } match {
        case Some((ns, p)) =>
          this.eventWriter.setDefaultNamespace(ns)
        case other =>
      }*/

      // Begin document
      documentElement = true
      this.eventWriter.writeStartDocument()

    }

    //-- Output Element
    //-------------------------
    if (du.element != null) {

      //println(s"Stax: Start Element ${du.element.name}")
      du.element.ns match {
        case "" => this.eventWriter.writeStartElement(du.element.name)
        case null => this.eventWriter.writeStartElement(du.element.name)
        case _ =>

          getPrefixForNamespace(du.element.ns) match {
            case "" =>
              // this.eventWriter.writeStartElement("", du.element.name, du.element.ns)
              this.eventWriter.writeStartElement(du.element.ns, du.element.name)
            // this.eventWriter.writeStartElement(du.element.name)
            case prefix =>
              this.eventWriter.writeStartElement(getPrefixForNamespace(du.element.ns), du.element.name, du.element.ns)
          }

      }

      //-- NS if documentElement not done
      if (documentElement) {

        namespacePrefixesMap.foreach {
          case (ns, "") =>

            // this.eventWriter.writeDefaultNamespace(ns)
            this.eventWriter.writeNamespace("", ns)
          case (ns, p) =>
            this.eventWriter.writeNamespace(p, ns)
        }

      }

      //-- Close already if non hierarchical and set the value if some
      if (!du.hierarchical) {
        //  println(s"Stax: Closing already!")

        //-- With text content
        (du.value, du("cdata")) match {
          case (null, _) =>
          case (value, Some(true)) =>
            this.eventWriter.writeCData(du.value)

          case (value, _) =>
            //this.eventWriter.writeCharacters(du.value.toCharArray(),currenText.length(),du.value.length)
            //currenText = du.value
            this.eventWriter.writeCharacters(du.value)
        }

        this.eventWriter.writeEndElement()
        //currenText = ""
      }

    } //-- Output Attribute
    //----------------------------
    else if (du.attribute != null) {

      // println("Stax: attribute " + du.attribute)

      // try {
      du.attribute.ns match {
        case "" =>
          //println(s"--> Attribute ${du.attribute.name} / ${du.value}")
          this.eventWriter.writeAttribute(du.attribute.name, du.value)
        case null =>

          this.eventWriter.writeAttribute(du.attribute.name, du.value)

        case _ =>
          //println(s"--> NS Attribute ${du.attribute.name} / ${du.value}")
          this.eventWriter.writeAttribute(getPrefixForNamespace(du.attribute.ns), du.attribute.ns, du.attribute.name, du.value)
      }
      // } catch {
      //  case e : Throwable => println(s"--> Failed Attribute ${du.attribute.name} on ${this.eventWriter}")
      //}

    } //-- Close Element
    //--------------
    else if (du.isHierarchyClose) {

      // If some data are provided with the close, then write them
      //-------------------
      (du.value, du("cdata")) match {
        case (null, _) =>
        case (value, Some(true)) => this.eventWriter.writeCData(du.value)
        case (value, _) => this.eventWriter.writeCharacters(du.value)
      }

      //println("StaxIO Closing")
      //try {
      this.eventWriter.writeEndElement()
      //} catch {
      //case e : Throwable =>
      // }
    } // Output Simple value
    //----------
    else if (du.value != null) {

      (du.value, du("cdata")) match {
        case (null, _) =>
        case (value, Some(true)) => this.eventWriter.writeCData(du.value)
        case (value, _) => this.eventWriter.writeCharacters(du.value)
      }

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
    require(this.xmlInput != null || this.xmlNode != null, "An XML Input Reader or DOM Node must be provided")

    // Prepare XML Source
    //------------------------

    // Prepare input
    //-----------------
    var reader = this.xmlInput match {
      case null =>

        var writer = new StringWriter();
        var transformer = TransformerFactory.newInstance().newTransformer();
        transformer.transform(new DOMSource(this.xmlNode), new StreamResult(writer));
        var xml = writer.toString();

        XMLInputFactory.newInstance().createXMLStreamReader(new StringReader(xml))
      case other =>
        XMLInputFactory.newInstance().createXMLStreamReader(this.xmlInput)
    }

    while (reader.hasNext()) {
      reader.next();

      // Send Start Element
      //----------------------------
      if (reader.isStartElement()) {

        //-- Prepare data unit
        var du = new DataUnit
        du.hierarchical = true
        du.element = new xelement_base()
        du.element.name = reader.getLocalName()

        //-- NS
        reader.getNamespaceURI() match {
          case ns if (ns != null && ns != "") => du.element.ns = ns
          case _ =>
        }

        //-- Text value
        if (reader.hasText()) {
          //du.value = reader.getText()
        }

        //-- send
        logFine[StAXIOBuffer](s"Produced element DataUnit: " + du.element.name);
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
            logFine[StAXIOBuffer](s"Produced attribute DataUnit: " + du.attribute.name);
            this.streamIn(du)
          }
        }

      } // End Element
      //---------------
      else if (reader.isEndElement()) {

        logInfo[StAXIOBuffer]("Sending End element for: " + reader.getName())

        // Just send an empty data unit with hiearchical = false
        var closeDU = new DataUnit
        closeDU.setHierarchyClose
        this.streamIn(closeDU)

      } // Text
      //-------------------
      else if (reader.isCharacters()) {

        // println("Sending characters: "+reader.getText())

        // Send a value only event
        var du = new DataUnit
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
   * Creates a StAXIOBuffer with initial content to the provided string, ready to be streamed in
   */
  def apply(str: String) = {

    new StAXIOBuffer(new StringReader(str))

  }

  def apply(url: URL) = {

    new StAXIOBuffer(url)

  }

  def apply(in: InputStream) = {
    new StAXIOBuffer(new InputStreamReader(in))
  }

  def apply(in: Node) = {
    new StAXIOBuffer(xmlNode = in)
  }

  /**
   * Streams out to a buffer
   */
  def writeToOutputStream(in: ElementBuffer, out: OutputStream, indenting: Boolean = false, prefixes: Map[String, String] = Map[String, String]()) = {

    var io = new StAXIOBuffer
    io.namespacePrefixesMap = prefixes
    io.indenting = indenting
    io.output = out
    in.appendBuffer(io)
    in.streamOut()
    io

    io.outputBytesWritten

  }

  /**
   * Streams out an ElementBuffer to a string
   */
  def apply(in: ElementBuffer, indenting: Boolean = false, ns: Map[String, String] = Map()): String = {

    var io = new StAXIOBuffer
    io.indenting = indenting
    io.namespacePrefixesMap = ns
    in.appendBuffer(io)
    in.streamOut()

    return new String(io.output.asInstanceOf[ByteArrayOutputStream].toByteArray())

  }

  def streamOut(in: ElementBuffer, indenting: Boolean = false, ns: Map[String, String] = Map()): StAXIOBuffer = {

    var io = new StAXIOBuffer
    io.indenting = indenting
    io.namespacePrefixesMap = ns
    in.appendBuffer(io)
    in.streamOut()

    io

  }

}
