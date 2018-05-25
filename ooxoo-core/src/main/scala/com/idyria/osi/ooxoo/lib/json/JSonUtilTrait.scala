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
package com.idyria.osi.ooxoo.lib.json

import com.idyria.osi.ooxoo.core.buffers.structural.ElementBuffer
import java.io.CharArrayWriter
import java.io.StringReader
import com.idyria.osi.ooxoo.core.buffers.structural.Buffer

trait JSonUtilTrait extends Buffer {
  
  
  def toJSONString = {
    
    var io = new JsonIO(outputArray = new CharArrayWriter)
    
    this.appendBuffer(io)
    this.streamOut()
    this.cleanIOChain
    
    io.finish
    

    
    
  }
  
  def fromJSONString(str:String) = {
    
    var io = new JsonIO(stringInput = new StringReader(str))
    
    this.appendBuffer(io)
    io.streamIn
    
    this.cleanIOChain
    
    this
  }
}
