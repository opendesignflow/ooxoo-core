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
package org.odfi.ooxoo.core.buffers.structural

import org.odfi.ooxoo.core.buffers.datatypes.XSDStringBuffer

import scala.reflect.ClassTag

trait AnyBufferTrait extends Buffer {

  var name: String = null

  var ns: String = null

  @xcontent
  var text: XSDStringBuffer = null
}

@any
class AnyElementBuffer extends ElementBuffer with AnyBufferTrait {

  @any
  var content = AnyXList()

  override def streamIn(du: DataUnit) = {
    this.lockIO
    if (du != null && du.value != null && du.element == null) {
      // println(s"Importing Data inside AnyElement: "+this.name+"-> "+du.value)

      // this.text = du.value
      //du.value=null
    }
    this.unlockIO

    super.streamIn(du)
  }

  /*  override def streamOut(du:DataUnit) = {
     // println(s"Streamout AnyElement with $name , $text, and $du")
      if (text!=null && du!=null) {
        du.value = text
        //println(s"Adding value to hierarchical: ${du.hierarchical} -> ${du.element}")
      }
      super.streamOut(du)
    }*/

}

@any
class AnyAttributeBuffer extends XSDStringBuffer with AnyBufferTrait {


}

/**
 * Trait to add any content variable to a class
 */
trait AnyContent {

  @any
  var content = AnyXList()

  def addContentOfType[T <: ElementBuffer](implicit tag: ClassTag[T]) = {


    val newelt = tag.runtimeClass.getDeclaredConstructor().newInstance().asInstanceOf[T]
    content += newelt
    newelt
  }

}

/*class AnyXList( cl: => AnyElementBuffer) extends XList[AnyElementBuffer](AnyXList) {

}*/

object AnyXList {

  //

  /**
   * Map Containing possible Modeled XML elements that could be instanciated by the XList closure
   * instead of creating generic Any classes
   *
   * Map Format:
   *
   * ( ns -> ElementName) -> Class
   *
   * Arguments:
   * - ns: The namespace of element, or an empty string if no namespace
   * - the name of the XML element
   * - The class implementing ElementBuffer for that element
   *
   * @warning The model class MUST have an empty constructor, otherwise a runtime error will be thrown
   */
  var modelsMap = Map[Tuple2[String, String], (DataUnit => Buffer)]()

  /**
   * Register a new model in the models map
   *
   * Models are registered as namespaced and non-namespaced.
   * Use namespaces in case it is unclear if any collisions happen
   *
   * @throws IllegalArgumentException if cannot determine xelement parameters from class
   */
  def apply[T <: Buffer](cl: Class[T]) = {

    //println("Registering model class in XList")

    // Get name and ns from annotation
    //-------------
    var xelement = xelement_base(cl)
    if (xelement == null) {
      throw new IllegalArgumentException(s"Cannot register XML model class $cl that seems to be missing @xelement annotation")
    }

    // Register
    //------------------
    this.modelsMap = this.modelsMap + ((xelement.ns -> xelement.name) -> { du => cl.getDeclaredConstructor().newInstance() })
    this.modelsMap = this.modelsMap + (("" -> xelement.name) -> { du => cl.getDeclaredConstructor().newInstance() })
  }

  def register[T <: Buffer](implicit tag: ClassTag[T]) = {
    AnyXList(tag.runtimeClass.asInstanceOf[Class[T]])
  }

  /**
   * Register Closure that acts as a factory for a given type
   */
  def register[T <: Buffer](cl: => T)(implicit tag: ClassTag[T]) = {

    // Get name and ns from annotation
    //-------------
    var xelement = xelement_base(tag.runtimeClass.asInstanceOf[Class[T]])
    if (xelement == null) {
      throw new IllegalArgumentException(s"Cannot register XML model class $cl that seems to be missing @xelement annotation")
    }

    // Register
    //------------------
    this.modelsMap = this.modelsMap + ((xelement.ns -> xelement.name) -> { du => cl })
    this.modelsMap = this.modelsMap + (("" -> xelement.name) -> { du => cl })

  }

  def apply() = {

    /**
     * Creating an XList of anyBuffer
     * The Buffers are setup depending on data unit provided
     */
    XList[Buffer] {
      (du: DataUnit) =>


        //println("Got Buffer for this Header content")

        var res: Buffer = null
        (du.element, du.attribute) match {

          // Element
          //------------
          case (element, null) =>

            //println(s"AnyXList got a DU to translate to buffer: ${element.name} ${element.ns}")

            // Is there a registered model for the element =>
            //-------------
            modelsMap.get((element.ns -> element.name)) match {

              //-> Yes
              case Some(builder) =>

                //-- Instanciate
                try {
                  res = builder(du)

                  //println(s"Created from model class: ${res}")

                } catch {
                  case e: Throwable =>
                    e.printStackTrace()
                    throw new RuntimeException(s"The Any content list found a model for element: ${element.ns}:${element.name}, but the model instance could not be created, does it have an empty constructor? if not, you MUST add one ", e)
                }

              //-> No, create a generic element
              case None =>

                var elementBuffer = new AnyElementBuffer
                elementBuffer.name = element.name
                elementBuffer.ns = element.ns
                elementBuffer.text = du.value
                res = elementBuffer
            }



          // Attribute
          //----------------
          case (null, attribute) =>

            var attributeBuffer = new AnyAttributeBuffer
            attributeBuffer.name = attribute.name
            attributeBuffer.ns = attribute.ns match {
              case null => null
              case "" => null
              case ns => ns
            }
            attributeBuffer.text = du.value
            res = attributeBuffer

          //null

          case _ => null
        }

        // Return
        res


    }

  }

}
