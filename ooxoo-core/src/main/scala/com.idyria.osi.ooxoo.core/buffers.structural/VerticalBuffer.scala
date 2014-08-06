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

import com.idyria.osi.ooxoo.core.utils.ScalaReflectUtils
import com.idyria.osi.tea.logging.TLogSource
import com.idyria.osi.ooxoo.core.buffers.datatypes.QName
import com.idyria.osi.ooxoo.core.buffers.structural.io.IOBuffer
import com.idyria.osi.ooxoo.core.utils.ReflectUtilsTrait

/**
 * Just a type marker
 */
trait HierarchicalBuffer {

}

/**
 *
 * Trait for a buffer to be declared "Vertical", meaning it will produce the result of other sub buffers
 *
 * @author rleys
 *
 */
trait VerticalBuffer extends BaseBufferTrait with HierarchicalBuffer with TLogSource with ReflectUtilsTrait {

  /**
   * This will be true if the element matching this Buffer has been received, and the next one must go to one sub field
   */
  protected var inHierarchy = false

  protected var stackSize = 0

  // Search
  //---------------------------

  /**
   * Search for a Buffer in this object hierarchy that matches the provided search pattern
   */
  def search[T <: Buffer](searchPath: String): Buffer = {

    null

  }

  /**
   * This Override performs the work of a vertical buffer:
   *
   *  - The Push is propagated like a standart buffer
   *  - The local class is inspected for eventual field buffers to also push out to
   *
   */
  override def streamOut(du: DataUnit): Unit = {

    lockIO

    // Normal streamOut (will output this element name and such)
    //----------
    //println("Entered Streamout of Vertical Buffer: " + this.hashCode() + " io: "+this.getIOChain)
    super.streamOut(du)

    // Attributes
    //------------------
    /*var validAttributes = ScalaReflectUtils.getAnnotatedFields(this, classOf[xattribute]).map(ScalaReflectUtils.getFieldValue(this, _).asInstanceOf[Buffer]).filter(_ != null)
    
    validAttributes.foreach {
      attr => 
        
        println("-- Valid attribute: "+attr+ " -> "+" last buffer: "+this.lastBuffer)
        
        
        
    
    }*/

    try {
      this.getAnnotatedFields(this, classOf[xattribute]).filter(ScalaReflectUtils.getFieldValue(this, _) != null).foreach {
        f ⇒

          // println(s"Streamout for attribute "+f.getName)
          try {

            //-- Get value
            var value = this.getFieldValue(this, f).asInstanceOf[Buffer]

            //-- streamOut
            this.getIOChain match {
              case Some(ioChain) ⇒

                //println("Calling streamout on attribute: " + value.hashCode())

                value.appendBuffer(ioChain)
                value.streamOut {

                  du ⇒
                    var attribute = xattribute_base(f)
                    du.attribute = attribute
                    du

                }
              case None ⇒
            }

            //value.lastBuffer.remove

          } catch {
            case e: Throwable ⇒ throw new RuntimeException(s"An error occured while streamOut of attribute ${f.getName} in class ${getClass.getCanonicalName}", e)
          }
      }
    } catch {
      case e: Throwable ⇒

        e.printStackTrace()

    }

    // Sub Elements
    //-------------------
    this.getAnnotatedFields(this, classOf[xelement]).filter(ScalaReflectUtils.getFieldValue(this, _) != null).foreach {
      f ⇒

        //-- Get value
        var value = this.getFieldValue(this, f).asInstanceOf[Buffer]

        //-- streamOut
        this.getIOChain match {
          case Some(ioChain) ⇒

            //println("Calling streamout on element: " + value.hashCode())

            value.appendBuffer(ioChain)
            value streamOut {
              du ⇒

                var element = xelement_base(f)
                du.element = element
                du

            }
          case None ⇒
        }

      //-- streamOut

      /*value.lastBuffer match {
          case e : IOBuffer => e.remove
          case _ => 
        }*/

    }

    // Any
    //-----------------
    this.getAnnotatedFields(this, classOf[any]).filter(this.getFieldValue(this, _) != null).foreach {

      f ⇒

        // println(s"Streamout for any field in ${getClass}")

        //-- Get value
        var value = ScalaReflectUtils.getFieldValue(this, f).asInstanceOf[Buffer]

        //-- streamOut
        this.getIOChain match {
          case Some(ioChain) ⇒

            //println("Calling streamout on element: " + value.hashCode())

            value.appendBuffer(ioChain)
            value.streamOut
          case None ⇒
        }

      //value.lastBuffer.remove

    }

    // Content
    //---------------
    this.getAnnotatedFields(this, classOf[xcontent]).filter(this.getFieldValue(this, _) != null).foreach {

      f ⇒

        // println(s"Streamout for xcontent field in ${getClass}")

        //-- Get value
        var value = this.getFieldValue(this, f).asInstanceOf[Buffer]

        //-- streamOut
        this.getIOChain match {
          case Some(ioChain) ⇒

            //println("Calling streamout on element: " + value.hashCode())

            value.appendBuffer(ioChain)
            value.streamOut
          case None ⇒
        }

      //value.lastBuffer.remove

    }

    // Close hierarchy
    //--------------
    // println("Closing from VBuffer: "+this.lastBuffer)
    var closeDU = new DataUnit
    closeDU.hierarchical = true
    super.streamOut(closeDU)

    // Clean all IO Buffers
    //-----------------------

    //-- Clean
    //println("------ Now closing ")
    unlockIO
    cleanIOChain

  }

  /**
   * Call up the provided closure to all the subfields that can be fetched in or pushedout
   * @param complete, true allows null fields to be seen, false does not
   */
  /*def propagate(nullFields: Boolean)(cl: (Symbol, Buffer) => Unit) = {


  }*/

  /**
   * Gets a data unit from chain
   */
  override def streamIn(du: DataUnit) = {

    // require(du.attribute != null || du.element != null)

    //println(s"In VBuffer streaming for ${getClass}")

    logFine[VerticalBuffer](s"(${getClass.getSimpleName()}) Got DU " + du)

    (this.inHierarchy, du.isHierarchyClose, du.element, du.attribute) match {

      // Hierarchy Close
      case (_, true, _, _) ⇒

        // println("--- End of "+getClass.getSimpleName())
        logFine[VerticalBuffer](s"(${getClass.getSimpleName()}) -- End of " + getClass.getSimpleName())

        this.inHierarchy = false
        this.cleanIOChain

      // Element
      //---------------

      //-- Top Element
      //--------------------

      // Don't check top element on any class
      case (false, false, element, null) if (this.getClass.isAnnotationPresent(classOf[any])) ⇒ this.inHierarchy = true;



      // Top Element
      case (false, false, element, null) if (!this.getClass.isAnnotationPresent(classOf[any])) ⇒

        // Verify the element matches the expected top one
        //---------------
        /*try {

          var expected = xelement_base(this)
          if (!du.element.name.equals(expected.name)) {
            throw new RuntimeException(s"Vertical buffer on ${VerticalBuffer.this.getClass()} expected an XML element named ${expected.name}, but got: ${du.element.name} instead")
          }

          //  println("--- Start of "+getClass.getSimpleName())

        } catch {
          // No @xelement annotation defined
          case e: java.lang.NullPointerException => throw new RuntimeException(s"Class ${VerticalBuffer.this.getClass()} MUST have an @xelement annotation!");
        }*/

        // Try to import value in case the Element Opening also contains a value
        //-----------------------------
        (du.value, this) match {

          // No value, do nothing
          case (null, _)                      =>

          //-- Call Import data unit if we are a databuffer
          //---------------
          case (_, db: AbstractDataBuffer[_]) ⇒ db.importDataUnit(du)

          //-- Try to find an xcontent class field otherwise and pass it the DU to streamIn
          //---------------
          case (_, _) ⇒

            //logFine[VerticalBuffer]("Trying to set ")
            (this.getXContentField, this.getIOChain) match {
              case (Some(content), Some(ios)) ⇒
              
                content.appendBuffer(ios.cloneIO);
                // Mark DU as hierarchy close, to make sure we won't stay in this subtree (it is only a value)
                du.setHierarchyClose 
              	
                content <= du
                
              case _ ⇒
            }

        }

        // Set hierarchy
        if (du.hierarchical)
          this.inHierarchy = true;

      //-- Some Value
      //---------------------
      case (true, false, null, null) if (du.value != null) ⇒

        logFine[VerticalBuffer](s"(${getClass.getSimpleName()}) -- Found du with only value:  ${du.value} ")

        // Try to import if possible
        //---------------
        this match {

          //-- Call Import data unit if we are a databuffer
          //---------------
          case db: AbstractDataBuffer[_] ⇒ db.importDataUnit(du)

          //-- Try to find an xcontent class field otherwise and pass it the DU to streamIn
          //---------------
          case _ ⇒

            (this.getXContentField, this.getIOChain) match {
              case (Some(content), Some(ios)) ⇒

                logFine[VerticalBuffer](s"(${getClass.getSimpleName()}) ---- Passing to xcontent:  ${du.value} ")

                content.appendBuffer(ios.cloneIO);
                // Mark DU as hierarchy close, to make sure we won't stay in this subtree (it is only a value)
                du.setHierarchyClose 
                content <= du

              //-- Close content buffer because it is only a value

              case _ ⇒
            }

        }
      // if (this.isInstanceOf[AbstractDataBuffer]) 

      //-- Some Element
      //-------------------------
      case (true, false, element, null) ⇒

        // println(s"-- Element: $element // ${du.attribute} // ${du.value}")

        // Proceed to element
        //-----------------------
        this.getElementField(du.element.name) match {

          // Found A Buffer Matching
          //-----------------------------
          case Some(buffer) ⇒

            logFine[VerticalBuffer](s"Found element Buffer to pass in value: ${du.value}")

            //   println(s"Got an XML element for subfield: ${du.element.name}, stack size is now: ${stackSize}");

            // Clone this IO to the buffer
            //--------------
           /* buffer.withIOChain(this) {
        	  buffer <= du
          	}*/
            

            // Stream in DU to element
            //---------------------
           buffer.appendBuffer(this.getIOChain.get.cloneIO);
            buffer <= du

            logFine[VerticalBuffer](s"-------> ${buffer}")

          // Nothing -> Can we stream into any ?
          //---------------
          case None ⇒

            this.getAnyField match {

              // Any
              //------------
              case Some(any) ⇒

                // Clone this IO to the buffer
                //--------------
                any.appendBuffer(this.getIOChain.get.cloneIO);

                // Stream in DU to element
                //---------------------
                any <= du

              // Nothing, need to ignore element, use AnyElement for that
              case None ⇒

                var any = new AnyElementBuffer

                // Clone this IO to the buffer
                //--------------
                any.appendBuffer(this.getIOChain.get.cloneIO);

                // Stream in DU to element
                //---------------------
                any <= du

              //println(s"---> No field instance returned for element ${du.element.name} under ${getClass} <---")
            }

        }

      // Attribute
      //-------------
      case (true, false, null, attribute) ⇒

        logFine[VerticalBuffer](s"-- Attribute: ${attribute.name} , value: ${du.value} ")

        this.getAttributeField(du.attribute.name) match {

          // Normal Streamin
          case Some(buffer) ⇒

            // println(s"Found attribute Buffer to pass in value: ${du.value}")

            // Stream in attribute
          	buffer.withIOChain(this) {
        	  buffer <= du
          	}
          	/*buffer.appendBuffer(this.getIOChain.cloneIO);
            buffer <= du
            buffer.cleanIOChain*/

            logFine[VerticalBuffer](s"-------> Send attribute to ${buffer.getClass()}")

          // Try Nay
          case None ⇒

            this.getAnyField match {

              // Any
              case Some(any) ⇒

                // Stream in DU to element
                any <= du

              case None ⇒
                logFine[VerticalBuffer]("---> No field instance returned for attribute <---")
            }
        }

      case m ⇒ throw new RuntimeException(s"DU input on element: ${getClass.getSimpleName} at the wrong moment: $m")
    }

    // Call parent
    super.streamIn(du)

  }

  /**
   * Get instance of a buffer set on the field that matches the provided Qname
   * @return None if nothing was found
   */
  private def getElementField(name: QName): Option[Buffer] = {

    //logFine[VerticalBuffer]("*Looking for field for element: /"+name.getLocalPart()+"/")

    // Get all xelement annotated fields
    // Filter on annotations not maching name
    this.getAnnotatedFields(this, classOf[xelement]).filter {
      a ⇒
        var xelt = xelement_base(a)
        //logFine[VerticalBuffer]("xelement annotation name:/"+xelt.name+"/");
        xelt != null && (name.getLocalPart().equals(xelt.name) || name.getLocalPart().equals(a.getName()))
    } match {

      // If there is one, get existing value of instanciate
      case x if (!x.isEmpty) ⇒

        // Get Value
        var fieldValue: Buffer = this.getFieldValue(this, x.head)
        if (fieldValue == null)
          fieldValue = this.instanciateFieldValue(this, x.head)
        return Option(fieldValue)

      case _ ⇒
    }

    return None

  }

  /**
   * Finds the attribute field of current class, matching provided name
   * Instanciate or return the reference to the Buffer
   */
  private def getAttributeField(name: QName): Option[Buffer] = {

    logFine[VerticalBuffer]("*Looking for field for attribute: " + name.getLocalPart())

    // Get all xattribute fields, instanciate annotation and filter out the non matching names
    this.getAnnotatedFields(this, classOf[xattribute]).filter {
      f ⇒
        var xattr = xattribute_base(f);
        //        	logFine[VerticalBuffer]("Found field with xattribute annotation, and name:"+xattr.name)
        xattr != null && name.getLocalPart().equals(xattr.name)

    } match {

      // If there is one, get existing value of instanciate
      //-------------
      case x if (!x.isEmpty) ⇒

        var targetField = x.head

        logFine[VerticalBuffer](s"*Found field: $name")

        // Get Value
        var fieldValue: Buffer = this.getFieldValue(this, targetField)

        // Instanciate
        //------------------
        if (fieldValue == null) {

          logFine[VerticalBuffer](s"Instanciating field for attribute: $name")
          fieldValue = this.instanciateFieldValue(this, targetField)

        }

        // Return
        return Option(fieldValue)

      // NOthing -> Ignore
      case _ ⇒ return None

    }
    //return None
  }

  /**
   * Only returns the first found field marked as @any
   */
  protected def getAnyField: Option[Buffer] = this.getAnnotatedFields(this, classOf[any]).headOption match {

    case Some(field) ⇒

      // Get Value
      var fieldValue: Buffer = this.getFieldValue(this, field)

      // Instanciate
      //------------------
      if (fieldValue == null) {

        fieldValue = this.instanciateFieldValue(this, field)

      }

      // Return
      return Option(fieldValue)
    case None ⇒ None

  }

  /**
   * Only returns the first found field marked as @content, with instanciated type
   */
  protected def getXContentField: Option[Buffer] = this.getAnnotatedFields(this, classOf[xcontent]).headOption match {

    case Some(field) ⇒

      // Get Value
      var fieldValue: Buffer = this.getFieldValue(this, field)

      // Instanciate
      //------------------
      if (fieldValue == null) {

        fieldValue = this.instanciateFieldValue(this, field)

      }

      // Return
      return Option(fieldValue)
    case None ⇒ None

  }

}

object VerticalBuffer {

  /**
   * Returns all the fields that have an annotation
   */
  /*  def allFields(base: AnyRef): Iterable[Symbol] = {

    // Get type tag
    //--------------------
    var baseTT = scala.reflect.runtime.universe.manifestToTypeTag(scala.reflect.runtime.currentMirror, Manifest.singleType(base))

    baseTT.tpe.foreach {

      t => logFine[VerticalBuffer]("Available: " + t.toString())

    }

    baseTT.tpe.members.filter(
      (m: Any) =>
        m.asInstanceOf[scala.reflect.api.Universe#Symbol].isTerm && !m.asInstanceOf[scala.reflect.api.Universe#Symbol].isMethod).filter {
        f =>

          // Filter based on annotation presence
          logFine[VerticalBuffer]("Available: " + f)
          f.annotations.find(a => (a.tpe.erasure == typeOf[xelement] || a.tpe.erasure == typeOf[xattribute])) match {
            case None => false
            case _ => true
          }
        //f.annotations.filter( a => (a.tpe.erasure == typeOf[xelement] || a.tpe.erasure == typeOf[xattribute]))

      }.asInstanceOf[Iterable[Symbol]]

    //typeTag.members.filter(m => m.isTerm && !m.isMethod)

    //scala.reflect.runtime.Mirror.classToType(base.getClass()).members.filter(m => m.isTerm && !m.isMethod)

    /*typeTag.tpe.erasure.members.foreach{

      t : scala.reflect.api.Universe#Symbol => logFine[VerticalBuffer]("Available: "+t.toString())

    }*/

    //List()
  }
*/
} 
