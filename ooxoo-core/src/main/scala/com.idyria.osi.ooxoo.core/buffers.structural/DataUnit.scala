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
/**
 *
 */
package com.idyria.osi.ooxoo.core.buffers.structural

import com.idyria.osi.ooxoo.core.buffers.datatypes.QName
import scala.beans.BeanProperty




/**
 *
 * This class  represents a data unit, mening a value represented by a field/object instance in the object hierarchy
 * It contains the basic placeholders to convert the holded data from the data type to XML and reverse.
 *
 * It also contains the element/attribute annotation needed to recognise the structural type
 *
 * @author rleys
 *
 */
class DataUnit {

  /**
   * The annotation for an element
   */
  @BeanProperty
  var element : xelement_base = null

  /**
   * The annotation for an attribute
   */
  @BeanProperty
  var attribute : xattribute_base = null

  /**
   * The string representation of this atomic value
   */
  @BeanProperty
  var value : String = null

  /**
   * false: This data unit is a single data piece (always false for attributes)
   * true: This data unit is opening a hierarchical level (which will be explicitly closed)
   */
  @BeanProperty
  var hierarchical : Boolean = false


  // DU Context
  //------------------------

  var contextMap = Map[String,Any]()

  /**
    Add a new value to the DataUnit context map
    Usage:

      var du = new DataUnit
      du("key" -> value)
  */
  def apply( tuple : (String,Any)) = contextMap = contextMap + tuple

  /**
    Get the context value matching provided key
  */
  def apply( key : String ) = contextMap.get(key)


  // Utilities
  //----------------------

  def isHierarchyClose = (this.attribute==null && this.element==null && this.hierarchical==true)

  /**
   * Sets to condition of hierarchy closre : Hierarchical with no element/attribute datas
   */
  def setHierarchyClose = {
    this.attribute = null
    this.element = null
    this.hierarchical = true
  }
  
  // Merge operation
  //--------

  def +(du:DataUnit) : DataUnit = {

    // Copy Attributes and merge context maps
    //---------
    if (du.element!=null)
      this.element = du.element

    if (du.attribute!=null)
      this.attribute = du.attribute

    if (du.value!=null)
      this.value = du.value

    this.contextMap = du.contextMap ++ this.contextMap

    this
  }

  //def +=(du: DataUnit) . DataUnit

}

object DataUnit {


  def apply() : DataUnit = new DataUnit

  /**
   * Returns a DataUnit configured for hierarchy close
   */
  def closeHierarchy : DataUnit = {
    
    var du = new DataUnit
    du.setHierarchyClose
    du
    
  }
  

}
