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
package org.odfi.ooxoo.model

import org.odfi.ooxoo.core.buffers.datatypes.{BooleanBuffer, IntegerBuffer, XSDStringBuffer}
import org.odfi.ooxoo.core.buffers.structural
import org.odfi.ooxoo.core.buffers.structural.io.sax.StAXIOBuffer
import org.odfi.ooxoo.core.buffers.structural._

import java.io.ByteArrayOutputStream
import scala.language.implicitConversions
import scala.reflect.ClassTag

/**
 * Contains the methods and tree stacking utilities to be able to define an XML model in a convienient way.
 *
 * The model can then be exported to documentation or Source files
 *
 */
@xelement(name = "Model")
trait ModelBuilder extends ElementBuffer with ModelBuilderLanguage {

  // General Config
  //------------------

  //var defaultElementBufferClass : Class[ _<:ElementBuffer] = classOf[ElementBuffer]

  // Producers configuration
  //-------------------------------

  def producers: producers = {

    getClass.getAnnotation(classOf[producers])

  }

  // Element Creation/Editing
  //-----------------------

  //-- Element stack and current
  var elementsStack = scala.collection.mutable.Stack[Element]()

  /**
   * When an element is added
   */
  onWith("element.start") {

    (elt: Element) =>

      //println("Inside element.start for "+elt.name)

      // - If there is a current element, add new element to it, otherwise it is a top element
      // - If the target element lists already contains this one, just stack
      elementsStack.headOption match {

        case Some(top) if (top.elements.contains(elt)) =>

        case Some(top) =>
          elt.parent = top;

          top.elements += elt

          resolveElementBaseType(elt)

        case None if (topElements.contains(elt)) => topElements += elt
        case None => topElements += elt
      }

      // Stack element
      elementsStack.push(elt)

  }

  onWith("element.end") {( elt: Element) =>

    // println("Inside element.end for "+elt.name)

    // Unstack element
    if (elementsStack.size > 0) {

      elementsStack.pop()
    }
  }

  def resolveElementBaseType(elt: Element) = {

    elt.parent match {
      case null =>
      case parent if (parent.isHierarchyParent) =>
        elt.classType = parent.name
      case other =>
    }

  }

  /**
   * Set class Type of current element to full string class name
   */
  def classType(classType: String): Unit = {

    elementsStack.headOption match {
      case Some(element) => element.classType = classType
      case None => throw new RuntimeException("Cannot call classType() outside of an element")
    }

  }

  /**
   * Set class type of current element to another element definitions one
   */
  def classType(element: Element): Unit = classType(element.classType.toString)

  /**
   * Create a new Element in current, which has the direct class type names and such of the provided className
   * Per default, the last part of the qualified class name is used as element name
   */
  def importElement(className: String): Element = {

    elementsStack.headOption match {
      case Some(element) =>

        // Create new
        var newElement = new Element(className.split("""\.""").last, this)
        newElement.classType = className
        newElement.imported = true

        this.@->("element.start", newElement)
        this.@->("element.end", newElement)

        newElement

      case None => throw new RuntimeException("Cannot call importElement() outside of an element")
    }

  }

  def importElement(classF: Class[_ <: ElementBuffer]): Element = importElement(classF.getCanonicalName)

  /**
   * Create a new Element in current, which has the direct class type names and such of the provided className
   * Per default, the last part of the qualified class name is used as element name
   */
  def importElement(element: Element): Element = {

    elementsStack.headOption match {
      case Some(parent) =>

        // Create new
        var newElement = new Element(element.name, this)
        newElement.classType = element.name
        newElement.importSource = element
        newElement.imported = true
        newElement.maxOccurs = element.maxOccurs

        this.@->("element.start", newElement)
        this.@->("element.end", newElement)

        newElement

      case None => throw new RuntimeException("Cannot call importElement() outside of an element")
    }

  }
  
  def importElements(wrapper:IsWordElementWrapper) = {
  
      wrapper.left.elements.foreach {
          elt => 
              importElement(elt)
      }
      
  }
  

  /**
   * Set class type of current element to the one matching a standard type
   */
  def ofType(str: String): Unit = {

    str.contains(".") match {
      case true => classType(str)
      case false => classType(getType(str.toLowerCase()).getCanonicalName())
    }

  }

  def ofType(baseType: Element): Unit = {

    elementsStack.headOption match {
      case Some(element) => element.importSource = baseType
      case None => throw new RuntimeException("Cannot call ofType() outside of an element")
    }

  }

  def withTrait(traitType: String): Unit = {

    elementsStack.headOption match {
      case Some(element) => element.traits += traitType
      case None => throw new RuntimeException("Cannot call withTrait() outside of an element")
    }

  }

  def withTrait(traitClass: Class[_]): Unit = {
    withTrait(traitClass.getCanonicalName)
  }

  def withTrait(traitType: Element): Unit = {

    elementsStack.headOption match {
      case Some(element) => element.traits += traitType.name
      case None => throw new RuntimeException("Cannot call withTrait() outside of an element")
    }

  }

  def withTrait[T](implicit tag: ClassTag[T]): Unit = {
    withTrait(tag.runtimeClass)
  }

  def withAttachedInstance[T](implicit tag: ClassTag[T]) : Unit = {
       withTrait(classOf[AttachedImplementation[_]].getCanonicalName + "["+tag.runtimeClass.getCanonicalName+"]")
  }
  def withAttachedInstance(cn:String) : Unit = {
       withTrait(classOf[AttachedImplementation[_]].getCanonicalName + "["+cn+"]")
  }
  
  def isTrait: Unit = {
    isTrait(false)
  }
  def isTrait(changeName: Boolean = false): Unit = {

    elementsStack.headOption match {
      case Some(element) =>
        element.makeTrait(changeName)
      case None => throw new RuntimeException("Cannot call isTrait() outside of an element")
    }

  }

  def requestContainerReference = {
    elementsStack.headOption match {
      case Some(element) =>
        element.requestContainerRelation = true
      //withTrait(classOf[VerticalBufferWithParentReference[_]].getCanonicalName+s"[${element.parent.g}]")
      case None => throw new RuntimeException("Cannot call requestContainerReference() outside of an element")
    }
  }

  /**
   * Makes current element a trait, the implementation will be a custom class with the hierarchical name of the current
   * element
   *
   * Example: A is {
   * 			B is {
   * 				makeTraitAndUseSameNameImplementation
   * 			}
   * 	}
   *
   * Produces: ABTrait
   * A Uses: AB
   * User implements: AB extends BTrait
   */
  def makeTraitAndUseCustomImplementation = {
    elementsStack.headOption match {
      case Some(element) =>
        element.makeTraitAndUseCustomImplementation
      case None => throw new RuntimeException("Cannot call makeTraitAndUseCustomImplementation() outside of an element")
    }
  }

  /**
   * Makes current element a trait, the implementation will be a custom class with the  name of the current
   * element, not the hierarchical name
   *
   * Example: A is {
   * 			B is {
   * 				makeTraitAndUseSameNameImplementation
   * 			}
   * 	}
   *
   * Produces: BTrait
   * A Uses: B
   * User implements: B extends BTrait
   */
  def makeTraitAndUseSameNameImplementation = {
    elementsStack.headOption match {
      case Some(element) =>
        element.makeTraitAndUseCustomImplementation
        element.staticClassName = true
      case None => throw new RuntimeException("Cannot call makeTraitAndUseSameNameImplementation() outside of an element")
    }
  }

  def any = {

    elementsStack.headOption match {
      case Some(element) => withTrait(classOf[AnyContent].getCanonicalName)
      case None => throw new RuntimeException("Cannot call any() outside of an element")
    }

  }

  // Attribute Creation/Editing
  //---------------------


  def attribute(name: String) : Attribute = {

    // Create Attribute
    var attr = new Attribute(name)
    @->("attribute.add", attr)
    attr

    // Set Description
    //attr.description = desc

    attr
  }

  def -@(name: String): Attribute = attribute(name)

  def attribute(attr: IsWordAttributeWrapper): Attribute = {

    // Set Description
    //attr.left.description = desc

    attr.left

  }

  onWith("attribute.add") {
    (attribute: Attribute) =>

      // Add only if not already added
      // Fail if no elements on stack
      //-----------
      this.elementsStack.headOption match {

        case Some(element) if (!element.attributes.contains(attribute)) => element.attributes += attribute
        case Some(element) =>
        case None =>
          throw new RuntimeException("Cannot create attribute outside an element")

      }

  }

  // General Descriptions and such
  //---------------------

  /**
   * Sets the description of current element
   */
  def withDescription(desc: String): Unit = {

    this.elementsStack.headOption match {

      case Some(element) => element.description = desc
      case None =>
        throw new RuntimeException("Cannot set description outside an element")

    }

  }

  // Multiple
  //-------------------
  /*def multiple(strType : String) : Class[ _ <: Buffer] = {

        this.multiple()
        getType(strType)
    }
    def multiple() = {

        println("calling simple multiple")

        this.elementsStack.headOption match {
            case Some(element) => element.maxOccurs = 10
            case None =>
        }
    }
    def multiple(elt:IsWordElementWrapper) = {
        elt.left.maxOccurs = 10
    }*/

  // Utilities
  //-----------------
  def toXML: String = {

    println(s"Top Elements count ${topElements.size}")

    // Create StaxIO
    //-------------------
    var io = new StAXIOBuffer()
    this - io

    // Streamout
    //--------------
    this.streamOut {
      du =>
        du("prefixes" -> Map((org.odfi.ooxoo.core.ns -> "ooxoo")))
        du
    }
    //this.streamOut()

    // Return res
    new String(io.output.asInstanceOf[ByteArrayOutputStream].toByteArray)

  }

}

object ModelBuilder {

}

// Common
//--------------------
trait Common {

  @xattribute(name = "name")
  var name: XSDStringBuffer = null

  @xattribute(name = "className")
  var className: XSDStringBuffer = null

  @xelement(name = "Description")
  var description: XSDStringBuffer = null

  @xattribute(name = "minOccurs")
  var minOccurs = IntegerBuffer(1)

  @xattribute(name = "maxOccurs")
  var maxOccurs = IntegerBuffer(1)

  @xattribute(name = "ClassType")
  var classType: XSDStringBuffer = null

  /**
   * An Imported element is not written to output, its classType is just used as is
   */
  var imported: BooleanBuffer = false

  @xattribute(name = "default")
  var default: XSDStringBuffer = null

  @xelement(name = "EnumerationValues")
  var enumerationValues = XList { new XSDStringBuffer }

  @xelement(name = "IDKey")
  var idKey: BooleanBuffer = false

  @xelement(name = "Generated")
  var generated: BooleanBuffer = false

  /**
   * Sets the appropriate maxOccurs or boolean so that a List gets generated
   */
  def setMultiple : Unit = {

    maxOccurs = 2

  }
  
  def setMultiple(m:Boolean) : Unit = m match {
      case true => maxOccurs = 2
      case false => maxOccurs = 1
  }

}

// Element Model
//-----------------------
@xelement(name = "Element")
class Element(
  inputName: String, var builder: Model) extends ElementBuffer with Common {

  // Structure
  //---------------

  @xelement(name = "Element")
  var elements = structural.XList { du =>
    var elt = new Element(du.element.name, builder)
    elt.parent = this
    elt
  }

  @xelement(name = "Attribute")
  var attributes = structural.XList { du => new Attribute(du.element.name) }

  @xelement(name = "Trait")
  var traits = XList { new XSDStringBuffer }

  // Import Relation
  //-----------------
  var importSource: Element = null

  // Related Type
  //------------------

  /**
   * If true, this element is not really an element but a parent in the children's hierarchy
   */
  @xattribute(name = "hierarchyParent")
  var isHierarchyParent = false

  /**
   * The element needs a reference to its container
   */
  var requestContainerRelation = false

  /**
   *
   */
  var parent: Element = null

  var nativeType : Boolean = false

  def depth: Int = {

    var res = 0
    var current = this
    while (current.parent != null) {
      res += 1
      current = current.parent
    }
    res
  }

  /**
   * If set, this element is just instanciating the defined Element, so no need to write it out as oyn type
   */
  var instanceOfElement: Element = null

  // Defaults
  //-------------
  this.classType = builder.defaultElementBufferClass.getCanonicalName
  this.traits += builder.defaultElementBufferClass.getCanonicalName
  this.name = inputName
  this.className = inputName

  // Description
  //-----------------------

  def apply(desc: String) = {
    this.description = desc
  }

  /*def :+ (desc: String) = {

        println("Setting Description on element")
        this.description = desc
    }*/

  // Sub Elements
  //-------------------

  // Body
  //---------------------
  //var body : String = ""
  //var bodyContent

  // Trait Support
  //--------------------

  @xattribute(name = "isTrait")
  var isTrait: BooleanBuffer = false

  /**
   * This value requests that the trait be produced, with a different name than the specified element
   * The final object however has a different name
   */
  var traitSeparateFromObject: String = null

  /**
   * If set, generator should not change the name in any way
   */
  var staticClassName = false

  /**
   * Set to trait and change name
   */
  def makeTrait(changeName: Boolean = false) = {
    this.isTrait = true
    if (changeName && !this.className.endsWith("Trait")) {
      this.className = this.className.toString + "Trait"
    }
  }

  /**
   * transforms this element in a trait, change its name, but use original name as class instantiation
   */
  def makeTraitAndUseCustomImplementation: Unit = {
    this.traitSeparateFromObject = this.className
    this.makeTrait(true)
  }
  /**
   * transforms this element in a trait, change its name, but use original name as class instantiation
   */
  def makeTraitAndUseCustomImplementation(targetClassName: String): Unit = {
    this.traitSeparateFromObject = targetClassName
    this.makeTrait(true)
  }

}
object Element {

  implicit def stringToElement(str: String): Element = {

    new Element(str, null)

  }

}

// Attribute Model
//-------------------------
@xelement(name = "Attribute")
class Attribute(inputName: String) extends ElementBuffer with Common {

  // Defaults
  //-------------
  this.classType = classOf[XSDStringBuffer].getCanonicalName
  this.name = inputName
  this.className = inputName

}
object Attribute {

  implicit def stringToAttribute(str: String): Attribute = new Attribute(str)
}
