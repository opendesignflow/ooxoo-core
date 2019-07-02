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
package com.idyria.osi.ooxoo.core.buffers.structural

import scala.collection.mutable.MutableList
import scala.reflect.runtime.universe._
import com.idyria.osi.ooxoo.core.buffers.structural.io.IOBuffer
import com.idyria.osi.ooxoo.core.utils.ScalaReflectUtils
import scala.reflect._
import java.lang.reflect.ParameterizedType
import scala.language.implicitConversions
import com.idyria.osi.tea.logging._
import com.idyria.osi.ooxoo.core.buffers.structural.io.IOTransparentBuffer
import scala.collection.mutable.ListBuffer
import scala.collection.mutable.AbstractBuffer
import scala.collection.mutable.ArrayBuffer
import com.idyria.osi.ooxoo.core.buffers.id.ElementWithID

/**
  *
  * This List if a vertical buffer type to contain a list of buffers (like a list of subelements)
  *
  * @author rleys
  *
  */
class XList[T <: Buffer](

                            val createBuffer: DataUnit => T, var containerBuffer: Option[ElementBuffer] = None, implicit val ctag: ClassTag[T]
                        ) extends ArrayBuffer[T] with BaseBufferTrait with HierarchicalBuffer with TLogSource with IOTransparentBuffer {

    var currentBuffer: Buffer = null

    //var localTag = classTag[T]

    /*def -=(b:Buffer) : Boolean = {
    this.contains(b) match {
      case true =>  super.-
      case false => false
    }
  }*/

    // Create
    //----------------
    def add: T = {

        val res = createInstance
        /*if (res.isInstanceOf[VerticalBufferWithParentReference[ElementBuffer]] && containerBuffer.isDefined) {
      res.asInstanceOf[VerticalBufferWithParentReference[ElementBuffer]].parentReference = Some(containerBuffer.get)
    }*/

        this += res

        res
    }

    def addWith(cl: T => Any): T = {
        val res = add
        cl(res)
        res
    }

    def addFirst: T = {
        val res = createInstance
        res +=: this

        res
    }

    def addFirstWith(cl: T => Any): T = {
        val res = add
        cl(res)
        res
    }

    /**
      * Just create an instance of contained element
      */
    def createInstance = {

        val res = createBuffer(null)

        if (res.isInstanceOf[VerticalBufferWithParentReference[ElementBuffer]] && containerBuffer.isDefined) {
            res.asInstanceOf[VerticalBufferWithParentReference[ElementBuffer]].parentReference = Some(containerBuffer.get)
        }

        res
    }

    /**
      * Added object is rolledback if an error happens
      * Error is kept through
      */
    def addRollbackOnError[RT](cl: T => RT): T = {
        val newobj = add
        try {
            cl(newobj)
            newobj
        } catch {
            case e: Throwable =>
                this -= newobj
                throw e
        }
    }

    // Accessors
    //----------------
    def get(index: Int): Option[T] = {
        if (index < 0 || index >= size) {
            None
        } else
            Some(this (index))
    }

    def getAllOfType[CT <: Buffer](implicit tag: ClassTag[CT]): List[CT] = {

        this.collect {
            case x if (tag.runtimeClass.isInstance(x)) => x.asInstanceOf[CT]
        }.toList
    }

    /**
      * Look for an element of type ElementWithID which has its eid field set to searched id
      */
    def findByEId[CT <: T](id: String) = ctag.runtimeClass match {
        case withId if (classOf[ElementWithID].isAssignableFrom(withId)) =>

            getAllOfType[ElementWithID].find {

                e => id != null && e.eid != null && e.eid.toString == id
            } match {
                case Some(found) => Some(found.asInstanceOf[CT])
                case None => None
            }
        case other => None
    }

    def findOrCreateByEId(id: String) = ctag.runtimeClass match {
        case withId if (classOf[ElementWithID].isAssignableFrom(withId)) =>
            findByEId[T](id) match {
                case Some(v) => v.asInstanceOf[T]
                case None =>
                    val added = this.add
                    added.asInstanceOf[ElementWithID].eid.set(id)
                    added
            }
        case other => sys.error(s"Cannot use findOrCreateByEid if contained element ${ctag.runtimeClass} is not an ElementWithID")
    }

    def createUniqueIDElementOrError(id: String)(error: String) = ctag.runtimeClass match {
        case withId if (classOf[ElementWithID].isAssignableFrom(withId)) =>

            findByEId[T](id) match {
                case Some(v) =>

                    sys.error(error)
                case None =>

                    val added = this.add

                    added.asInstanceOf[ElementWithID].eid.set(id)

                    added
            }
        case other => sys.error(s"Cannot use findOrCreateByEid if contained element ${ctag.runtimeClass} is not an ElementWithID")
    }

    def addUniqueEIDElement[IT <: ElementWithID](elt: IT)(implicit ctag: ClassTag[IT]) = {
        find(_ == elt) match {
            case Some(ft) => elt
            case None if (elt.eid != null && findByEId(elt.eid.toString).isDefined) =>
                sys.error(s"Cannot add Element with eid ${elt.eid} and type ${ctag.runtimeClass} because a different instance already exists")
            case None =>
                println(s"Adding with eid")
                elt.ensureGenericRandomID
                this += elt.asInstanceOf[T]
                elt
        }
    }

    def ensureElement[CT <: T](implicit tag: ClassTag[CT]): CT = {
        this.find {
            elt => tag.runtimeClass.isInstance(elt)
        } match {
            case Some(elt) => elt.asInstanceOf[CT]
            case None =>
                val n = tag.runtimeClass.newInstance().asInstanceOf[CT]
                this += n
                n
        }
    }

    override def streamOut(du: DataUnit) = {

        //println(s"Streamout in XList for ${size} elements")

        lockIO
        this.foreach {

            content =>

                this.getIOChain match {
                    case Some(ioChain) =>

                        //println("Calling streamout on element: " + value.hashCode())
                        content.appendBuffer(ioChain)

                    case None =>
                }

                //println(s"Goiung to streamout xlist content of type (${content.getClass}), with: ${du.element} and ${du.attribute} ")

                // If No xelement / attribute annotation, try to take from content
                //--------------
                if (du.element == null && du.attribute == null) {

                    xelement_base(content) match {

                        //-- Any Element
                        case null if (content.isInstanceOf[AnyElementBuffer]) =>

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
                        case null if (content.isInstanceOf[AnyAttributeBuffer]) =>

                            du.attribute = new xattribute_base
                            du.attribute.name = content.asInstanceOf[AnyAttributeBuffer].name
                            du.attribute.ns = content.asInstanceOf[AnyAttributeBuffer].ns
                            // du.value = content.asInstanceOf[AnyAttributeBuffer].text
                            du.hierarchical = false

                            content.streamOut(du)

                            // Reset
                            du.attribute = null
                            du.hierarchical = false

                        //--Element Buffer not annotated
                        case null if (content.isInstanceOf[ElementBuffer]) =>

                            du.element = new xelement_base
                            du.element.name = content.getClass.getSimpleName.split("\\$") match {
                                case splitted if (splitted.size > 1) => splitted.last
                                case other => other.head.replace("$", "")
                            }

                            //du.value = content.asInstanceOf[AnyElementBuffer].text
                            du.hierarchical = true

                            //println("Element will be: "+du.element.name )

                            content.streamOut(du)

                            // Reset
                            du.element = null
                            du.hierarchical = false

                        //-- Error because only Any* Objects are allowed not to be annotated
                        case null =>
                            throw new RuntimeException(s"Cannot streamout content of type (${content.getClass}) in list that has no xelement/xattribute definition")
                        case annot =>

                            // Set element annotation and hierarchical to open element
                            du.element = annot

                            //-- If this is not a vertical buffer, it must never be hirarchical
                            content match {
                                case e: VerticalBuffer => du.hierarchical = true
                                case _ => du.hierarchical = false
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
            case Some(ioChain) =>

                //println("Calling streamout on element: " + value.hashCode())
                buffer.appendBuffer(ioChain)

            case None =>
        }

        buffer <= du


    }

    override def toString: String = "XList"

    // Moving around
    //-------------

    def up(elt:T) = {
        indexOf(elt) match {
            case -1 =>
            case 0 =>
            case index =>
                this -= elt
                this.insert(index-1,elt)
        }
    }

    def down(elt:T) = {
        indexOf(elt) match {
            case last if(last==size-1) =>
            case index =>
                this -= elt
                this.insert(index+1,elt)
        }
    }

    def top(elt:T) = {
        this -= elt
        this.insert(0,elt)

    }

    def bottom(elt:T) = {
        this -= elt
        this.insert(size-1,elt)
    }


}

object XList {

    def apply[T <: Buffer : ClassTag](implicit ctag: ClassTag[T]): XList[T] = {
        return new XList[T](du => ctag.runtimeClass.newInstance().asInstanceOf[T], None, ctag)
    }

    /**
      * Creates an XList from a closure that does not take any DataUnit as input (if useless like in most cases)
      */
    def apply[T <: Buffer](cl: => T)(implicit ctag: ClassTag[T]): XList[T] = {

        var realClosure: (DataUnit => T) = {
            du => cl
        }

        return new XList[T](realClosure, None, ctag)

    }

    /**
      * Creates an XList from a closure that does not take any DataUnit as input (if useless like in most cases)
      */
    def apply[T <: Buffer](cl: => T, parent: ElementBuffer)(implicit ctag: ClassTag[T]): XList[T] = {

        var realClosure: (DataUnit => T) = {
            du => cl
        }

        return new XList[T](realClosure, Some(parent), ctag)

    }

    /**
      * Creates an XList from a closure that does not take any DataUnit as input (if useless like in most cases)
      */
    def apply[T <: Buffer](cl: DataUnit => T)(implicit ctag: ClassTag[T]): XList[T] = {

        var realClosure: (DataUnit => T) = {
            du => cl(du)
        }

        return new XList[T](realClosure, None, ctag)

    }

}
