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

package com.idyria.osi.ooxoo.core.buffers.datatypes

import com.idyria.osi.ooxoo.core.buffers.structural.AbstractDataBuffer
import scala.util.matching.Regex
import scala.language.implicitConversions

/**
 * A Buffer to Set Regular expressions as XML datatype
 */
class RegexpBuffer extends AbstractDataBuffer[Regex] {
  
   def dataFromString(str: String) : Regex = {
     
     data = str.r
     data
     
   }
   
   def dataToString : String = if (data!=null) data.pattern.pattern();


else null

   override def toString : String = this.dataToString
  
}
object RegexpBuffer {
  
  def apply() = new RegexpBuffer
  
  // Conversions
  //-----------------
  
  implicit def convertFromRegexpToRegexpBuffer(r:Regex) : RegexpBuffer = {
    
    var b = new RegexpBuffer
    b.data = r
    b
  }
  
  implicit def convertFromStringToRegexpBuffer(r:String) : RegexpBuffer = this.convertFromRegexpToRegexpBuffer(r.r)
  
  implicit def convertFromRegexpBufferToRegex(b:RegexpBuffer) : Regex = b.data
  
}
