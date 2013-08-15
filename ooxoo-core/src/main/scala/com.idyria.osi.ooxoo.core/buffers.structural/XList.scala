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

import scala.language.implicitConversions

import com.idyria.osi.tea.logging.TLog

/**
 *
 * This List if a vertical buffer type to contain a list of buffers (like a list of subelements)
 * @author rleys
 *
 */
class  XList[T <: Buffer] (


				val createBuffer:  DataUnit  => T

			) extends MutableList[T] with BaseBufferTrait with HierarchicalBuffer {

  var currentBuffer : Buffer = null



  override def streamOut(du : DataUnit) = {

      TLog.logFine(s"Streamout in XList for ${size} elements")

      this.foreach {

        content =>

          content.appendBuffer(this.lastBuffer)

          TLog.logFine(s"Goiung to streamout xlist content of type (${content.getClass}), with: ${du.element} and ${du.attribute} ")

          // If No xelement / attribute annotation, try to take from content
          if (du.element==null && du.attribute==null) {

            xelement_base(content) match {
              case null => throw new RuntimeException(s"Cannot streamout content of type (${content.getClass}) in list that has no xelement/xattribute definition")
              case annot => 
 
                // Set element annotation and hierarchical to open element
                du.element = annot
                du.hierarchical = true

                content -> du

                // Reset
                du.element = null
                du.hierarchical = false
            }


          }
          


    }

  }


  

  override def streamIn(du: DataUnit) =  {

    TLog.logFine(s"IN LIST streamIn...............: ${du.element}");
    if (du.element!=null) {

      TLog.logFine(s"  xlist element: ${du.element.name} (current: $currentBuffer) ");

    }

    // If there is a current -> stream in
    //--------------------
    if (this.currentBuffer!=null) {
      //TLog.logFine(s"---- Giving to buffer");
      //this.currentBuffer <= du
    }
    // If there is no current -> instanciate and streaming
    //----------------------
    else {

      this.currentBuffer = this.createBuffer(du)
      this+=this.currentBuffer.asInstanceOf[T]

      // Add I/O Buffer
      //---------
      TLog.logFine("---- Chain before: "+this.printForwardChain);
      if(this.lastBuffer.isInstanceOf[IOBuffer])
        this.currentBuffer.appendBuffer(this.lastBuffer.asInstanceOf[IOBuffer].cloneIO)

      TLog.logFine("---- XLIST: Created Buffer instance");
      TLog.logFine("---- Chain now: "+this.printForwardChain);
      TLog.logFine("---- Buffer Chain now: "+this.currentBuffer.lastBuffer.printBackwardsChain);

      //-- Streamin
      this.currentBuffer <= du
    }

    

    // If end buffer has no IO anymore -> it is not the currentBuffer anymore
    //--------------
    if (this.currentBuffer!=null && !this.currentBuffer.lastBuffer.isInstanceOf[IOBuffer]) {

      TLog.logFine("---- XLIST: Current Buffer has stopped receiving events, we should too");

      this.currentBuffer = null
    //if (du.attribute==null && du.element==null && du.hierarchical==false) {

      // Remove IO buffer from XList
      //---------------
      var lastb = this.lastBuffer

      TLog.logFine(s"   ----> lastb $lastb");

      if (lastb!=null && lastb.isInstanceOf[IOBuffer]) {

        TLog.logFine("    ---- Chain now: "+this.printForwardChain);
        
        this.lastBuffer.remove
        
        TLog.logFine("    ---- Chain now: "+this.printForwardChain);

        // Replay Event because it should be treated by the container of this XList
        //-----------
        if(lastb.getPreviousBuffer!=null) {
            
           TLog.logFine("---- Replaying to: "+lastb.getPreviousBuffer.getClass());
          
            lastb.getPreviousBuffer <= du
        }
    }

    }


  }

  override def toString : String = "XList"



}
object XList {

  /**
    Creates an XList from a closure that does not take any DataUnit as input (if useless like in most cases)
  */
  def apply[T <: Buffer] (cl:   => T ) : XList[T] = {

      var realClosure : (DataUnit => T) = {
        du => cl
      }

    return new XList[T](realClosure)

  }

  /**
    Creates an XList from a closure that does not take any DataUnit as input (if useless like in most cases)
  */
	def apply[T <: Buffer] (cl: DataUnit  => T ) : XList[T] = {

		return new XList[T](cl)

	}

	implicit def convertClosuretoXList[T <: Buffer] (cl:   => T) : XList[T] = XList[T](cl)
  implicit def convertDataUnitClosuretoXList[T <: Buffer] (cl: DataUnit  => T) : XList[T] = XList[T](cl)

}
