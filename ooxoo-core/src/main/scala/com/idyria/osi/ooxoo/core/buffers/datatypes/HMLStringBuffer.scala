/**
 *
 */
package com.idyria.osi.ooxoo.core.buffers.datatypes

import com.idyria.osi.ooxoo.core.buffers.structural.AbstractDataBuffer
import javax.swing.text.html.HTMLEditorKit
import org.apache.commons.lang3.StringEscapeUtils

/**
 * @author rleys
 *
 */
class HTMLStringBuffer extends XSDStringBuffer {

  def this(str: String) = {
    this();

    dataFromString(str)
  }

  /**
   * Set provided string to actual data
   */
  override def dataFromString(str: String): String = { 
    
    var unescapedString = StringEscapeUtils.unescapeHtml4(str)
    this.data = unescapedString; 
    unescapedString 
   }

  /**
   * To String produces an HTML string
   */
  override def toString: String = {
    if (this.data == null)
      super.toString
    StringEscapeUtils.escapeHtml4(this.data)

  }

}

object HTMLStringBuffer {
  def apply(str: String) = new HTMLStringBuffer(str)
  def apply() = new HTMLStringBuffer

  implicit def convertAnyToHTMLStringBuffer(str: Any): HTMLStringBuffer = new HTMLStringBuffer(str.toString)
  implicit def convertStringToHTMLStringBuffer(str: String): HTMLStringBuffer = new HTMLStringBuffer(str)
  implicit def convertHTMLStringBufferToString(str: HTMLStringBuffer): String = str.toString
}