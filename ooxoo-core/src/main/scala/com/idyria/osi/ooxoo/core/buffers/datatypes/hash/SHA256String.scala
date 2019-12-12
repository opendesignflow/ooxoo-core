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
package com.idyria.osi.ooxoo.core.buffers.datatypes.hash


import com.idyria.osi.ooxoo.core.buffers.datatypes.XSDStringBuffer
import org.odfi.tea.hash.HashUtils

import scala.language.implicitConversions

/**
 * This buffer does not hash on stream in and out, only when value is set if not
 * streaming in
 * 
 */
class SHA256StringBuffer extends XSDStringBuffer {
  
  
  override def set(data:String) = {
    super.set(HashUtils.hashBytesAsHex(data.getBytes, "SHA-256"))
  }
  
 
  
  override def equals(comp:String) = {
    this.data == comp ||
    this.data == HashUtils.hashBytesAsHex(comp.getBytes, "SHA-256")
  }
}

object SHA256StringBuffer {
  
  implicit def convertFromStringToSHA256Buffer(str:String) = {
    
    var b = new SHA256StringBuffer
    b.set(str)
    b
  }
}
