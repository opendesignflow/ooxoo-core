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
import com.idyria.osi.ooxoo.core.buffers.structural.BaseBufferTrait
import com.idyria.osi.ooxoo.core.buffers.structural.DataUnit


abstract class EnumerationBuffer extends Enumeration with BaseBufferTrait {
 
  var selectedValue : Value = null
  
  /**
   * Ensure DU value has our local enum selection
   */
  override def streamOut(du:DataUnit) = {
    
    selectedValue match {
      case null => 
        
         du.value = this(0).toString
        
      case _ => 
        
         du.value = selectedValue.toString
    }
    
   
    
    super.streamOut(du)
    
  }
  
  override def streamIn(du:DataUnit) = {
    
    //println(s"******** Streamin Enum buffer (${getClass.getSimpleName()}): ${du.value} // ${du.attribute}**********")
    
    // If we have a hierarchy close data unit -> remove end IO buffer because we are done here
    //----------------------------
    if (du.isHierarchyClose) {
      this.cleanIOChain
    }
    
    // Record value
    //---------------------
    du.value match {
      case null => 
      case v => this.selectedValue = this.withName(v)
    }
    
    
    super.streamIn(du)
    
  }
  
  /*def select(value: T#Value) : Unit = {
    this.selectedValue = value
  }*/
  
  def select[FT <: Enumeration](value: FT#Value) : Unit = {
    
  // 
    this.selectedValue = this.withName(value.toString())
    
  }
  
  override def toString() = {
    selectedValue match {
      case null => 
        
         this(0).toString
        
      case _ => 
        
         selectedValue.toString
    }
  }
  
  /*def ==(value: FT#Value) : Boolean = {
    this.toString
  }*/
  
  def unapply[FT <: Enumeration](value: FT#Value) : Boolean = {
    
    //println("in unapply")
    
    this.selectedValue match {
      case null => false
      case v if (v.toString()==value.toString()) => true
      case _ => false
    }
    
  }
  
}
