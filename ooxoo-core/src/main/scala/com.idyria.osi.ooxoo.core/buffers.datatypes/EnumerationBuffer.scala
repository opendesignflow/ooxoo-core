package com.idyria.osi.ooxoo.core.buffers.datatypes

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
    
    du.value match {
      case null => 
      case _ => this.withName(_)
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
  
  
  def unapply[FT <: Enumeration](value: FT#Value) : Boolean = {
    
    println("in unapply")
    
    this.selectedValue match {
      case null => false
      case v if (v.toString()==value.toString()) => true
      case _ => false
    }
    
  }
  
}