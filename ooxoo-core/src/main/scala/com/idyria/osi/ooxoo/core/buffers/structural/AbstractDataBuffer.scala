/**
 * #%L
 * Core runtime for OOXOO
 * %%
 * Copyright (C) 2006 - 2017 Open Design Flow
 * %%
 * This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */
package com.idyria.osi.ooxoo.core.buffers.structural

import scala.beans.BeanProperty
import com.idyria.osi.tea.logging.TLogSource
import com.idyria.osi.ooxoo.core.buffers.datatypes.LongBuffer
import com.idyria.osi.tea.listeners.ListeningSupport
import scala.reflect.ClassTag

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
abstract class AbstractDataBuffer[DT: ClassTag] extends BaseBufferTrait with TLogSource with ListeningSupport {

  var data: DT = _
  def dataToString: String
  def dataFromString(str: String): DT

  type dataType = DT

  // Value Update
  //----------
  def onDataUpdate(cl:  => Unit) = {
    //println(s"Registering data update on: "+hashCode())
    this.on("data.update")(cl)
  }

  def triggerDataUpdate = {
    //println(s"triggering data update on: "+hashCode())
    this.@->("data.update")
  }

  // Data Set
  //------------------

  /**
   * Updates Internal value, and progagates
   */
  def set(data: DT) = {

    // Set
    this.data = data
    try {
      triggerDataUpdate
    } finally {
      // Propagate
      this.nextBuffer match {
        case null =>
        case other =>
          this.push
      }
    }

  }

  // Push/Pull
  //-----------------
  override def pushRight(du: DataUnit) = {
    importDataUnit(du)
    super.pushRight(du)

  }

  override def pushLeft(du: DataUnit) = {
    importDataUnit(du)
    super.pushLeft(du)

  }

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
      case null ?
      case base ? du.element = base
    }
    xattribute_base(this) match {
      case null ?
      case base ? du.attribute = base
    }

    du
  }

  /**
   * Create data unit using string conversion
   */
  override def importDataUnit(du: DataUnit): Unit = {

    this.dataFromString(du.value) match {
      case null =>
      case res => this.data = res
    }

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
        case elt if (elt != null && du.attribute != null) =>

        //-- Eat
        case _ =>
          this.importDataUnit(du)
        //this.set(this.dataFromString(du.value))
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

  def baseTypesToBuffer(cl: Class[_]): AbstractDataBuffer[_] = {

    cl match {
      case long if (classOf[scala.Long] == long) => new LongBuffer
      case long if (classOf[Long] == long) => new LongBuffer
      case long if (classOf[Long].isAssignableFrom(long)) => new LongBuffer
      case long if (cl.getCanonicalName() == "scala.Long") => new LongBuffer
      case _ => throw new RuntimeException("Cannot Prepare Data buffer for type: " + cl.getCanonicalName())
    }

  }

}


