/**
 *
 */
package com.idyria.osi.ooxoo3.core.buffers.structural

import scala.beans.BeanProperty
import com.idyria.osi.ooxoo3.core.buffers.structural.io.IOBuffer

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
					var data : DT = null ) extends BaseBuffer {

  
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
  
  
  /**
   * Fetchin data: 
   * 	- Convert from string to local type
   *  	- 
   */
  override def fetchIn(du : DataUnit) = {
    
    // If we have a hierarchy close data unit -> remove end IO buffer because we are done here
    //----------------------------
    if (du.attribute==null && du.element==null && du.hierarchical==false && du.value==null) {
      println("---- End of hierarchy for data buffer ("+this.getClass()+") -> remove IO chain");
      println("---- BCBefore: "+this.printForwardChain)
      if (this.lastBuffer.isInstanceOf[IOBuffer])
    	  this.lastBuffer.remove
	  println("---- BCAfter: "+this.printForwardChain)
      
    }
    
    // Otheerwise, if we have a value, -> import data from string
    //------------
    if (du.value!=null) {
      this.dataFromString(du.value)
    }
    
    // Let parent do the job
    super.fetchIn(du)
    
  }
  
  
  
}