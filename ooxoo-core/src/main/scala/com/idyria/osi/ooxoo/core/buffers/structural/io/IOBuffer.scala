/*
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
/**
 *
 */
package com.idyria.osi.ooxoo.core.buffers.structural.io

import com.idyria.osi.ooxoo.core.buffers.structural.Buffer

/**
 * 
 * IO Buffer are special kind of buffers that are involved only during push/fetch operations, and must be clonable 
 * for duplication over an object hierarchy.
 * Once used, they are removed from chain, to keep the object hierarchy clean for I/O references that may serialise or deserialise
 * from or to wrong places/data
 * 
 * @author rleys
 *
 */
trait IOBuffer extends Buffer {

  /**
   * Clone this IO for propagating over an object hierarchy
   */
  def cloneIO : IOBuffer
  
  
  
  
  
  
}

trait IOTransparentBuffer {
  
}
