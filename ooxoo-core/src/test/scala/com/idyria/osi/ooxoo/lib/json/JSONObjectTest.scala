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

import org.scalatest.FunSuite
import com.idyria.osi.ooxoo.core.buffers.structural.AnyElementBuffer
import org.odfi.tea.logging.TeaLogging
import org.odfi.tea.logging.TLogSource

class JSONObjectTest extends FunSuite with TLogSource {
  
  
  test("Basic JSONObject test") {
    
    tlogEnableFull[JsonIO]
    
    val top = new JSONObject {
      
      "top" :: {
         
       // println("Top creation")
        "a" :: {
           //println("A creation")
          "c" :: "Hello"
        }
        
        "b" :: {
          
           "d" *:: List("Hello",0,1,3.0)
          
          //"d" [0,1]
        }
      }
    }
    
    //val top = new AnyElementBuffer with JSonUtilTrait
    //top.name = "hello"
    val result = top.toJSON
    println("Result: "+result)
    
     assertResult(""""top":{"a":{"c":"Hello"},"b":{}}""")(result)
    
  }
}
