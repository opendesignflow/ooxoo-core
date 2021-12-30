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
package org.odfi.ooxoo.core.buffers.datatypes.id

import org.odfi.ooxoo.core.buffers.structural.AbstractDataBuffer

import java.util.UUID

class UUIDBuffer extends AbstractDataBuffer[UUID] {
  
  // Init
  //this.set(UUID.randomUUID().)
  
  def init = {
    this.set(UUID.randomUUID())
  }
  
  def dataToString: String = {
    this.data.toString()

  }

  /**
   * Set provided string to actual data
   */
  def dataFromString(str: String): UUID = str match {
    case null => null
    case s => 
      this.data = UUID.fromString(str)

      data
  }

  override def toString: String = {
    if (this.data == null)
      super.toString
    this.data.toString()

  }
  
}


object UUIDBuffer {
  
  def apply() = {
    var id = new UUIDBuffer
    id.init
    id
  }
  
}
