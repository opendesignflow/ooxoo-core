/*-
 * #%L
 * Core runtime for OOXOO
 * %%
 * Copyright (C) 2006 - 2017 Open Design Flow
 * %%
 * This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */
package com.idyria.osi.ooxoo.core.buffers.id

import com.idyria.osi.ooxoo.core.buffers.structural.ElementBuffer
import com.idyria.osi.ooxoo.core.buffers.structural.xattribute

trait ElementWithReferenceID[TT <: ElementWithID] {

  @xattribute(name = "refId")
  var refId: RefIDBuffer[TT] = null
  
  def getReferencedBuffer : Option[TT] = refId match {
    case null => None
    case rid => rid.getReferencedBuffer
  }
  
  def references(id:String) = refId match {
    case null => false
    case other => other.toString == id
  }
  
  def reference(elt: TT) = {
    refId = new RefIDBuffer
    refId.data = elt.eid
    refId.referencedBufferInstance = Some(elt)
  }
}
