/**
 *
 */
package com.idyria.osi.ooxoo.core.buffers.structural

import scala.collection.mutable.MutableList
import scala.reflect.runtime.universe._
import com.idyria.osi.ooxoo.core.buffers.structural.io.IOBuffer
import com.idyria.osi.ooxoo.core.utils.ScalaReflectUtils
import scala.reflect.ClassTag
import java.lang.reflect.ParameterizedType

/**
 * 
 * This List if a vertical buffer type to contain a list of buffers (like a list of subelements)
 * @author rleys
 *
 */
class  XList[T <: Buffer] (
				
				
				val createBuffer:  Unit  => T
    
			) extends MutableList[T] with BaseBufferTrait {

  var currentBuffer : Buffer = null
  
  
  
  /**
   * Must be for stream out
   */
  def createDataUnit : DataUnit = {
    
    null
  }
  
  
  
  /**
   * Repeat for all elements in the set
   */
  override def pushOut(cl: DataUnit => DataUnit) = {
    
    
    this.foreach {
      
      content => 
        
        content.appendBuffer(this.lastBuffer)
        content -> cl
      
    }
    
    
  }
  
  /**
   * Fetching for XLIst:
   * 
   * - Instanciate a new Buffer of the provided T type
   * - Fetchin the du to it
   * - Detect selection finish by presence of IO buffer at the end of its chain
   * - Fetchin all du to last created, until a hierarchy close comes and no current is selected
   * 
   */
  override def fetchIn(du: DataUnit) =  {
    
    
    println(s"IN LIST FETCHIN...............: ${du.value}");
    
    // If there is a current -> stream in
    //--------------------
    if (this.currentBuffer!=null) {
      //println(s"---- Giving to buffer");
      //this.currentBuffer <= du
    }
    // If there is no current -> instanciate and streaming
    //----------------------
    else {
      
      this.currentBuffer = this.createBuffer()
      this+=this.currentBuffer.asInstanceOf[T]
      
      // Add I/O Buffer
      //---------
      println("---- Chain before: "+this.printForwardChain);
      this.currentBuffer.appendBuffer(this.lastBuffer.asInstanceOf[IOBuffer].cloneIO)
      
      println("---- XLIST: Created Buffer instance");
      println("---- Chain now: "+this.printForwardChain);
      println("---- Buffer Chain now: "+this.currentBuffer.printForwardChain);
      
      //-- Streamin
      this.currentBuffer <= du
    }
    
    // If end buffer has no IO anymore -> it is not the currentBuffer anymore
    //--------------
    if (!this.currentBuffer.lastBuffer.isInstanceOf[IOBuffer]) {  
      
    	println("---- XLIST: Current Buffer has stopped receiving events, we should too");
    	this.currentBuffer = null
    //if (du.attribute==null && du.element==null && du.hierarchical==false) {
    	
    	// Remove IO buffer from XList
    	//---------------
    	var lastb = this.lastBuffer
    	if (lastb!=null && lastb.isInstanceOf[IOBuffer]) {
    	  
    	  println("---- Chain now: "+this.printForwardChain);
    	  this.lastBuffer.remove
    	  println("---- Chain now: "+this.printForwardChain);
    	  
    	  // Replay Event because it should be treated by the container of this XList
    	  //-----------
    	  if(lastb.getPreviousBuffer!=null) {
    	     println("---- Replaying to: "+lastb.getPreviousBuffer.getClass());
    	    lastb.getPreviousBuffer <= du
    	  }
    	}
    
    	
    	  
    	  
      
    	  
    	  
      
    } 
   
    
  }
  
  override def toString : String = "XList"
  
  
  
}
object XList {
  
	def apply[T <: Buffer] (cl:   => T ) : XList[T] = {
	  
	    var realClosure : (Unit => T) = {
	      t => cl
	    }
	  
		return new XList[T](realClosure)
	  
	}
  
	implicit def convertClosuretoXList[T <: Buffer] (cl: Unit  => T) : XList[T] = new XList[T](cl)
  
}
