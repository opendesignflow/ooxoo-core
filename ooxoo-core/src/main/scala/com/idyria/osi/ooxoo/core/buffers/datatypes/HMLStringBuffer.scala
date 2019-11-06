/**
 *
 */
package com.idyria.osi.ooxoo.core.buffers.datatypes


import org.apache.commons.lang3.StringEscapeUtils

import scala.language.implicitConversions

/**
 * @author rleys
 *
 */
class HTMLStringBuffer extends XSDStringBuffer {

  def this(str: String) = {
    this();

/*-
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
