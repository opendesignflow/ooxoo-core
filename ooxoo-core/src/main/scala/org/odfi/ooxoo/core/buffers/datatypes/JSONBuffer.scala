package org.odfi.ooxoo.core.buffers.datatypes

import org.odfi.ooxoo.core.buffers.structural.AbstractDataBuffer

import javax.json.{Json, JsonObject}
import java.io.StringReader
import java.net.URI



class JSONBuffer extends AbstractDataBuffer[JsonObject]{
  // Constructors
  //-------------------

  def this(str: String) = {
    this();

    dataFromString(str)
  }

  def this(o: JsonObject) = {
    this()
    this.data = o
  }

  def dataToString: String = {
    this.data match {
      case null => ""
      case other => data.toString()
    }

  }

  /**
   * Set provided string to actual data
   */
  def dataFromString(str: String): JsonObject = { this.data = Json.createParser(new StringReader(str)).getObject; data }

  override def toString: String = {
    dataToString

  }

  def equals(comp: JSONBuffer): Boolean = {
    //println("Called equals to xsdstringbuffer")
    this.data.equals(comp.data)
  }



  def compareTo(comp: JSONBuffer): Int = {

    //println("Called compare to to xsdstringbuffer")
    this.data.toString.compareTo(comp.toString)
  }
}

class JSONVBuffer extends JSONBuffer