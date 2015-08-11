/*
 * #%L
 * Core runtime for OOXOO
 * %%
 * Copyright (C) 2008 - 2014 OSI / Computer Architecture Group @ Uni. Heidelberg
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */
package com.idyria.osi.ooxoo.core.buffers.structural

 
  
trait ElementBuffer extends VerticalBuffer  {

 
  /**
    Creates a DataUnit representing an Element
  */
  override def createDataUnit : DataUnit = {

    // Get Element annotation
    //------------------
    var element = xelement_base(this)
    if (element==null) {
      throw new IllegalArgumentException(s"Could not find xelement annotation on ElementBuffer ${getClass().getCanonicalName()}")
    	logFine(s"Could not find xelement annotation on ElementBuffer ${getClass().getCanonicalName()}")

    }

    if (element.name==null || element.name=="") {
      throw new IllegalArgumentException(s"xelement annotation on ElementBuffer ${getClass().getCanonicalName()} did not reutnr any name")
    }


    // Create Empty Data Unit
    //------------------
    var du = new DataUnit
    du.element = element
    du.hierarchical = true
    du
  }
  
  /**
   * Make sure elementbuffer declares a hierarchical data unit
   */
  override def streamOut(du:DataUnit) = {
    
    
   // println("Entered Streamout of ElementBuffer")
    
    // Get Element annotation
    //------------------
    var element = xelement_base(this)
    if (element==null) {
      //throw new IllegalArgumentException(s"Could not find xelement annotation on ElementBuffer ${getClass().getCanonicalName()}")
    //	logFine(s"Could not find xelement annotation on ElementBuffer ${getClass().getCanonicalName()}")

    } else {
      du.element = element
    }

   // if (element.name==null || element.name=="") {
     // throw new IllegalArgumentException(s"xelement annotation on ElementBuffer ${getClass().getCanonicalName()} did not reutnr any name")
   // }
    
    
    du.hierarchical = true
    
    //VerticalBuffer.streamOut(du)
    super.streamOut(du)
  }


}
