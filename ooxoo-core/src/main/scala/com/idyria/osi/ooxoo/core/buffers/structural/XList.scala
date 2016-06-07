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

import scala.collection.mutable.MutableList
import scala.reflect.runtime.universe._
import com.idyria.osi.ooxoo.core.buffers.structural.io.IOBuffer
import com.idyria.osi.ooxoo.core.utils.ScalaReflectUtils
import scala.reflect.ClassTag
import java.lang.reflect.ParameterizedType
import scala.language.implicitConversions
import com.idyria.osi.tea.logging._
import com.idyria.osi.ooxoo.core.buffers.structural.io.IOTransparentBuffer
import scala.collection.mutable.ListBuffer
import scala.collection.mutable.AbstractBuffer
import scala.collection.mutable.ArrayBuffer

/**
 *
 * This List if a vertical buffer type to contain a list of buffers (like a list of subelements)
 * @author rleys
 *
 */
class XList[T <: Buffer](

    val createBuffer: DataUnit ⇒ T) extends ArrayBuffer[T] with BaseBufferTrait with HierarchicalBuffer with TLogSource with IOTransparentBuffer {

  var currentBuffer: Buffer = null

  /*def -=(b:Buffer) : Boolean = {
    this.contains(b) match {
      case true =>  super.-
      case false => false
    }
  }*/

  // Create
  //----------------
  def add: T = {
    val res = createBuffer(null)
    this += res
    res
  }

  // Accessors
  //----------------
  def get(index: Int): Option[T] = {
    Some(this(index))
  }

  def getAllOfType[CT <: Buffer](implicit tag: ClassTag[CT]): List[CT] = {

    this.collect {
      case x if (tag.runtimeClass.isInstance(x)) => x.asInstanceOf[CT]
    }.toList
  }

  override def streamOut(du: DataUnit) = {

    //println(s"Streamout in XList for ${size} elements")

    lockIO
    this.foreach {

      content ⇒

        this.getIOChain match {
          case Some(ioChain) ⇒

            //println("Calling streamout on element: " + value.hashCode())
            content.appendBuffer(ioChain)

          case None ⇒
        }

        //println(s"Goiung to streamout xlist content of type (${content.getClass}), with: ${du.element} and ${du.attribute} ")

        // If No xelement / attribute annotation, try to take from content
        //--------------
        if (du.element == null && du.attribute == null) {

          xelement_base(content) match {

            //-- Any Element
            case null if (content.isInstanceOf[AnyElementBuffer]) ⇒

              du.element = new xelement_base
              du.element.name = content.asInstanceOf[AnyElementBuffer].name
              du.element.ns = content.asInstanceOf[AnyElementBuffer].ns
              //du.value = content.asInstanceOf[AnyElementBuffer].text
              du.hierarchical = true

              //println("Element will be: "+du.element.name )

              content.streamOut(du)

              // Reset
              du.element = null
              du.hierarchical = false

            //-- Any Attribute
            case null if (content.isInstanceOf[AnyAttributeBuffer]) ⇒

              du.attribute = new xattribute_base
              du.attribute.name = content.asInstanceOf[AnyAttributeBuffer].name
              du.attribute.ns = content.asInstanceOf[AnyAttributeBuffer].ns
              // du.value = content.asInstanceOf[AnyAttributeBuffer].text
              du.hierarchical = false

              content.streamOut(du)

              // Reset
              du.attribute = null
              du.hierarchical = false

            //-- Error because only Any* Objects are allowed not to be annotated
            case null ⇒ throw new RuntimeException(s"Cannot streamout content of type (${content.getClass}) in list that has no xelement/xattribute definition")
            case annot ⇒

              // Set element annotation and hierarchical to open element
              du.element = annot

              //-- If this is not a vertical buffer, it must never be hirarchical
              content match {
                case e: VerticalBuffer ⇒ du.hierarchical = true
                case _ ⇒ du.hierarchical = false
              }

              content.streamOut(du)

              // Reset
              du.element = null
              du.hierarchical = false
          }

        } else {
          content.streamOut(du)
        }

      //content.lastBuffer.remove

      /*else if (du.element!=null || du.attribute!=null) {

            content -> du
          }*/

    }
    // EOF Each element

    // Clean IO Chain
    unlockIO
    cleanIOChain

  }

  override def streamIn(du: DataUnit) = {

    // Pass To New Buffer
    //-------------------------

    //-- Create
    var buffer = this.createBuffer(du)
    this += buffer

    //-- Stream in
    this.getIOChain match {
      case Some(ioChain) ⇒

        //println("Calling streamout on element: " + value.hashCode())
        buffer.appendBuffer(ioChain)

      case None ⇒
    }

    buffer <= du

    /*logFine(s"IN LIST streamIn...............: ${du.element}");


    if (du.element!=null) {

      logFine(s"  xlist element: ${du.element.name} (current: $currentBuffer) ");

    }

    // If there is a current -> stream in
    //--------------------
    if (this.currentBuffer!=null) {
      //logFine(s"---- Giving to buffer");
      //this.currentBuffer <= du
    }
    // If there is no current -> instanciate and streaming
    //----------------------
    else {

      this.currentBuffer = this.createBuffer(du)
      this+=this.currentBuffer.asInstanceOf[T]

      // Add I/O Buffer
      //---------
      logFine("---- Chain before: "+this.printForwardChain);
      if(this.lastBuffer.isInstanceOf[IOBuffer])
        this.currentBuffer.appendBuffer(this.lastBuffer.asInstanceOf[IOBuffer].cloneIO)

      logFine("---- XLIST: Created Buffer instance");
      logFine("---- Chain now: "+this.printForwardChain);
      logFine("---- Buffer Chain now: "+this.currentBuffer.lastBuffer.printBackwardsChain);

      //-- Streamin
      this.currentBuffer <= du
    }

    

    // If end buffer has no IO anymore -> it is not the currentBuffer anymore
    //--------------
    if (this.currentBuffer!=null && !this.currentBuffer.lastBuffer.isInstanceOf[IOBuffer]) {

      logFine("---- XLIST: Current Buffer has stopped receiving events, we should too");

      this.currentBuffer = null
    //if (du.attribute==null && du.element==null && du.hierarchical==false) {

      // Remove IO buffer from XList
      //---------------
      var lastb = this.lastBuffer

      logFine(s"   ----> lastb $lastb");

      if (lastb!=null && lastb.isInstanceOf[IOBuffer]) {

        logFine("    ---- Chain now: "+this.printForwardChain);
        
        this.lastBuffer.remove
        
        logFine("    ---- Chain now: "+this.printForwardChain);

        // Replay Event because it should be treated by the container of this XList
        //-----------
        if(lastb.getPreviousBuffer!=null) {
            
           logFine("---- Replaying to: "+lastb.getPreviousBuffer.getClass());
          
            lastb.getPreviousBuffer <= du
        }
    }

    }*/

  }

  override def toString: String = "XList"

}
object XList {

  def apply[T <: Buffer](implicit tag: ClassTag[T]): XList[T] = {
    return new XList[T](du => tag.runtimeClass.newInstance().asInstanceOf[T])
  }

  /**
   * Creates an XList from a closure that does not take any DataUnit as input (if useless like in most cases)
   */
  def apply[T <: Buffer](cl: ⇒ T): XList[T] = {

    var realClosure: (DataUnit ⇒ T) = {
      du ⇒ cl
    }

    return new XList[T](realClosure)

  }

  /**
   * Creates an XList from a closure that does not take any DataUnit as input (if useless like in most cases)
   */
  def apply[T <: Buffer](cl: DataUnit ⇒ T): XList[T] = {

    var realClosure: (DataUnit ⇒ T) = {
      du ⇒ cl(du)
    }

    return new XList[T](realClosure)

  }

  //implicit def convertClosuretoXList[T <: Buffer](cl: ⇒ T): XList[T] = XList[T](cl)
  //implicit def convertDataUnitClosuretoXList[T <: Buffer](cl: DataUnit ⇒ T): XList[T] = XList[T](cl)

}
