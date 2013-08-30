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

  var contextMap = Map[String,AnyRef]()

  /**
    Add a new value to the DataUnit context map
    Usage:

      var du = new DataUnit
      du("key" -> value)
  */
  def apply( tuple : (String,AnyRef)) = contextMap = contextMap + tuple

  /**
    Get the context value matching provided key
  */
  def apply( key : String ) = contextMap.get(key)


  // Utilities
  //----------------------

  def isHierarchyClose = (this.attribute==null && this.element==null && this.value==null)

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

  

}
