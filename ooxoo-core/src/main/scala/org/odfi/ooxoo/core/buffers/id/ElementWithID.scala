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
package org.odfi.ooxoo.core.buffers.id

import org.apache.commons.lang3.RandomUtils
import org.odfi.tea.random.UniqueLongGenerator
import org.odfi.ooxoo.core.buffers.datatypes.XSDStringBuffer
import org.odfi.ooxoo.core.buffers.structural.{ElementBuffer, xattribute}
import org.odfi.tea.security.TSecUtils

import scala.util.Random

trait ElementWithID extends ElementBuffer {
  
  @xattribute(name="eid")
  var eid : XSDStringBuffer = ""
  
  def hasEID = {
      this.eid!=null && this.eid.toString.length > 0
  }
  
  /**
   * Turn the value into a standard ID string.
   * For example " " is turned to "-"
   */
  def stringToStdEId(value:Any) = {
    value.toString().replaceAll("""[^\w-_.]""", "").replaceAll("""\s+""", "-").toLowerCase()
  }
  
  def generateHexId = {
      this.eid = TSecUtils.hashBytesToHexString(Random.nextString(128).getBytes, "SHA-256",join="")
  }
  
  def ensureGenericRandomID = {
      if (this.eid==null || this.eid.toString.trim.length==0) {
          generateHexId
      }
  }
  
}
