/**
 *
 */
package com.idyria.osi.ooxoo3.core.buffers.structural

import scala.beans.BeanProperty

/**
 * 
 * This class is a base class for all Buffers that hold a single piece of data
 * 
 * For example, all the implementations of default XSD data types are data buffers
 * A data buffer requires the implementation of string de/serialisation for pushOut
 * 
 * 
 * 
 * @author rleys
 *
 */
abstract class AbstractDataBuffer[DT <: AnyRef] 
				(
				    // Variable for local Data
					@BeanProperty()
					var data : DT ) extends BaseBuffer {

  
  def dataToString : String
  def dataFromString( str : String) : DT
  
  
 /**
  * Create data unit using string conversion
  */
 def createDataUnit: DataUnit = {
    
    // Create Empty Data Unit
    //------------------
    var du = new DataUnit
    du.setValue(this.dataToString)
    du
    
  }
  
  
  
  
  
}