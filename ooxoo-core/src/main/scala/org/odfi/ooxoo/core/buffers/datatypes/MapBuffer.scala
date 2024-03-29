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
package org.odfi.ooxoo.core.buffers.datatypes

import org.odfi.ooxoo.core.buffers.structural.{AbstractDataBuffer, BaseBufferTrait, Buffer, DataUnit, ElementBuffer, xattribute, xcontent, xelement}

/**
 * The Map buffer is a map and can be used as such.
 * Its values are written to XML using a special format:
 *
 * <Entry name="">value</Entry>
 *
 * The value must be a Buffer Type
 *
 */
class MapBuffer[T <: Buffer](var valueCreateClosure: (DataUnit => T)) extends scala.collection.mutable.LinkedHashMap[String, T] with BaseBufferTrait {

  
  @xelement(name = "Entry")
  class EntryElement() extends ElementBuffer  {

    @xattribute(name="name")
    var name: XSDStringBuffer = null

    @xcontent
    var value: T = _
    
    /**
     * Override getXContent to map type creation to provided closure
     * 
     */
    override def getXContentField = {
      
      //-- Create
      this.value = valueCreateClosure(null)
      Some(this.value)
      
    }
    

  }

  /**
   *
   * - Just do Normal Streamout of element
   * - Make some extra streamouts for the entries
   *
   */
  override def streamOut(du: DataUnit) : Unit = {

    // Preserve IO chain because we are doint multiple streamouts here
    this.lockIO

    // Normal streamout for base element
    //------------------

    // Ensure hierarchical because we are adding some subelements
    du.hierarchical = true
    super.streamOut(du)

    // Streamout Entries
    //-----------
    this.foreach {
      case (key, value) =>

        //println("Found entry")

        //-- Prepare Element
        val elt = new EntryElement
        elt.name = key
        elt.value = value

        //-- Output
        this.getIOChain match {
          case Some(ioChain) =>

            elt.appendBuffer(ioChain)
            elt.streamOut()

          case None =>
        }

    }

    // Close base element
    //------------------
    super.streamOut(DataUnit.closeHierarchy)

    // Unlock and clean I/O chain
    this.unlockIO
    this.cleanIOChain

  }
  
  var streamInEntries = List[EntryElement]()
  
  override def streamIn(du:DataUnit) = {
    
    (du.isHierarchyClose,du.element) match {
      
      // Close
      //---------------
      case (true,_) => 
        
        //-- Record in map
        streamInEntries.foreach {
          elt => 
            
            //println(s"Read in entry: ${elt.name} + ${elt.value}")
            this(elt.name.toString) = elt.value
            
        }
        
        //-- Clean IOs
        this.cleanIOChain
      
      // Got Entry
      //-----------------
      case (_,element) if(element!=null && element.name=="Entry") => 
      	
        //println("Streamin of element")
        
        //-- Streamin to EntryElement 
        var elt = new EntryElement
        streamInEntries = streamInEntries :+ elt
        this.getIOChain match {
          case Some(ioChain) => 
            elt.appendBuffer(ioChain)
            elt.streamIn(du)
          case _ => 
        }

      case _ =>
        
       // println("Got ignored du")
        
    }
    
  }

}

object MapBuffer {
  
  
  def apply[T <: Buffer](cl: => T) = new MapBuffer({ du => cl})
  
}
class DataMapBuffer[K <: AbstractDataBuffer[_],V <: Buffer](var keyCreateClosure: ( () => K),var valueCreateClosure: (DataUnit => V)) extends scala.collection.mutable.LinkedHashMap[K, V] with BaseBufferTrait {

  
  @xelement(name = "Entry")
  class EntryElement() extends ElementBuffer  {

    @xattribute(name="key")
    var key: K = keyCreateClosure()

    @xcontent
    var value: V = _
    
    /**
     * Override getXContent to map type creation to provided closure
     * 
     */
    override def getXContentField = {
      
      //-- Create
      this.value = valueCreateClosure(null)
      Some(this.value)
      
    }
    

  }

  /**
   *
   * - Just do Normal Streamout of element
   * - Make some extra streamouts for the entries
   *
   */
  override def streamOut(du: DataUnit) = {

    // Preserve IO chain because we are doint multiple streamouts here
    this.lockIO

    // Normal streamout for base element
    //------------------

    // Ensure hierarchical because we are adding some subelements
    du.hierarchical = true
    super.streamOut(du)

    // Streamout Entries
    //-----------
    this.foreach {
      case (key, value) =>

        //println("Found entry")

        //-- Prepare Element
        var elt = new EntryElement
        elt.key = key
        elt.value = value

        //-- Output
        this.getIOChain match {
          case Some(ioChain) =>

            elt.appendBuffer(ioChain)
            elt.streamOut()

          case None =>
        }

    }

    // Close base element
    //------------------
    super.streamOut(DataUnit.closeHierarchy)

    // Unlock and clean I/O chain
    this.unlockIO
    this.cleanIOChain

  }
  
  var streamInEntries = List[EntryElement]()
  
  override def streamIn(du:DataUnit) = {
    
    (du.isHierarchyClose,du.element) match {
      
      // Close
      //---------------
      case (true,_) => 
        
        //-- Record in map
        streamInEntries.foreach {
          elt => 
            
            //println(s"Read in entry: ${elt.name} + ${elt.value}")
            this(elt.key) = elt.value
            
        }
        
        //-- Clean IOs
        this.cleanIOChain
      
      // Got Entry
      //-----------------
      case (_,element) if(element!=null && element.name=="Entry") => 
      	
        //println("Streamin of element")
        
        //-- Streamin to EntryElement 
        var elt = new EntryElement
        streamInEntries = streamInEntries :+ elt
        this.getIOChain match {
          case Some(ioChain) => 
            elt.appendBuffer(ioChain)
            elt.streamIn(du)
          case _ => 
        }

      case _ =>
        
       // println("Got ignored du")
        
    }
    
  }

}

object DataMapBuffer {
  
  def apply[K <: AbstractDataBuffer[_],T <: Buffer](kcl: => K, cl: => T) = new DataMapBuffer[K,T]({() => kcl},{ du => cl})
  
}
