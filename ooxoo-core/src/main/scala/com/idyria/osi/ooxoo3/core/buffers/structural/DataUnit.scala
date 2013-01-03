/**
 *
 */
package com.idyria.osi.ooxoo3.core.buffers.structural

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
  var element : xelement = null
  
  /**
   * The annotation for an attribute
   */
  @BeanProperty
  var attribute : xattribute = null
  
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
  
}