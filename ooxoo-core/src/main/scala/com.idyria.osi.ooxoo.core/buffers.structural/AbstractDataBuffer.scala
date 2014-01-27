/**
 *
 */
package com.idyria.osi.ooxoo.core.buffers.structural

import scala.beans.BeanProperty
import com.idyria.osi.ooxoo.core.buffers.structural.io.IOBuffer
import com.idyria.osi.tea.logging._
import com.idyria.osi.ooxoo.core.buffers.datatypes.LongBuffer

/**
 *
 * This class is a base class for all Buffers that hold a single piece of data
 *
 * For example, all the implementations of default XSD data types are data buffers
 * A data buffer requires the implementation of string de/serialisation for streamOut
 *
 *
 *
 * @author rleys
 *
 */
abstract class AbstractDataBuffer[DT](
    // Variable for local Data
    @BeanProperty() var data: DT = null) extends BaseBufferTrait with TLogSource {

  def dataToString: String
  def dataFromString(str: String): DT

  type dataType = DT

  // Data Set
  //------------------

  /**
   * Updates Internal value, and progagates
   */
  def set(data: DT) = {

    // Set
    this.data = data

    // Propagate
    this.push

  }

  // Propagate
  //-----------------

  // Data Unit
  //---------------------

  /**
   * Create data unit using string conversion
   */
  override def createDataUnit: DataUnit = {

    // Create Empty Data Unit
    //------------------
    var du = new DataUnit
    du.setValue(this.dataToString)

    // Try to add element/attribute content if implementation class has some
    //--------------
    xelement_base(this) match {
      case null ⇒
      case base ⇒ du.element = base
    }
    xattribute_base(this) match {
      case null ⇒
      case base ⇒ du.attribute = base
    }

    du
  }

  /**
   * Create data unit using string conversion
   */
  override def importDataUnit(du: DataUnit): Unit = {

    var res = this.dataFromString(du.value)
    this.data = res

  }

  // Stream
  //--------------

  /**
   * Ensure value is present when streaming out
   */
  override def streamOut(du: DataUnit) = {
    //lockIO
    //println("Entered Streamout of Databuffer: " + this.hashCode() + " io: " + this.getIOChain)

    /* this.foreachNextBuffer {
      b => 
        
        println("--> Next: "+b)
    }*/

    //-- Add Value
    du.setValue(this.dataToString)

    //-- Pass
    //println("Passing to super")
    super.streamOut(du)

    //-- Clean

    //unlockIO
    //cleanIOChain
  }

  /**
   * streamIn data:
   * 	- Convert from string to local type
   *  	-
   */
  override def streamIn(du: DataUnit) = {

    // Import Data
    //----------------------

    if (du.value != null) {

      xelement_base(this) match {

        //-- Element, don't eat if an attribute
        case elt if(elt!=null && du.attribute!=null) =>  
          
        //-- Eat
        case _ => this.set(this.dataFromString(du.value))
      }

      //println("Importing data: "+du.value)

     
    }

    // If we have a hierarchy close data unit -> remove end IO buffer because we are done here
    //----------------------------
    if (du.isHierarchyClose) {
      this.cleanIOChain
    }
    /*if (du.attribute == null && du.element == null && du.hierarchical == false && du.value == null) {
      logFine("---- End of hierarchy for data buffer (" + this.getClass() + ") -> remove IO chain");
      logFine("---- BCBefore: " + this.printForwardChain)
      if (this.lastBuffer.isInstanceOf[IOBuffer])
        this.lastBuffer.remove
      logFine("---- BCAfter: " + this.printForwardChain)

    }*/

    // Let parent do the remaining job
    //-----------------------
    super.streamIn(du)

  }

}

object AbstractDataBuffer {

  // def convertFromStringToAnyDataBuffer[T <: AbstractDataBuffer[_]](str: String) : T

  def baseTypesToBuffer(cl:Class[_]) : AbstractDataBuffer[_] = {
    
   
    cl match {
      case long if (classOf[scala.Long] == long) =>  new LongBuffer
      case long if (classOf[Long] == long) =>  new LongBuffer
      case long if (classOf[Long].isAssignableFrom(long)) =>  new LongBuffer
      case long if (cl.getCanonicalName()=="scala.Long") =>  new LongBuffer
      case _                                     => throw new RuntimeException("Cannot Prepare Data buffer for type: " + cl.getCanonicalName())
    }
    
  }
  
}
