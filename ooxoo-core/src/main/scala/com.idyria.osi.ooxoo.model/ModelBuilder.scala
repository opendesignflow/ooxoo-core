package com.idyria.osi.ooxoo.model

import com.idyria.osi.ooxoo.core.buffers.datatypes._
import com.idyria.osi.ooxoo.core.buffers.structural._
import com.idyria.osi.ooxoo.core.buffers.structural.io.sax._

import scala.language.implicitConversions

/**
   Contains the methods and tree stacking utilities to be able to define an XML model in a convienient way.

   The model can then be exported to documentation or Source files

*/
@xelement(name="Model")
class ModelBuilder extends ElementBuffer with Model with ModelBuilderLanguage {


    // Producers configuration
    //-------------------------------

    def producers : producers =  {

        getClass.getAnnotation(classOf[producers])

    }


    // Element Creation/Editing
    //-----------------------
    
    //-- Element stack and current
    var elementsStack = scala.collection.mutable.Stack[Element]()
    

    /**
        When an element is added
    */
    onWith("element.start") {

    elt : Element => 

        println("Inside element.start for "+elt.name)

        // If there is a current element, add new element to it, otherwise it is a top element
        elementsStack.headOption match {
            case Some(top) => elt.parent=top;top.elements += elt
            case None => topElements+=elt
        }

        // Stack element
        elementsStack.push(elt)

    }

    onWith("element.end") { elt : Element => 

        println("Inside element.end for "+elt.name)

        // Unstack element
        if (elementsStack.size > 0) {

            elementsStack.pop
        }
    }

    def withTrait(traitType: String) = {

        elementsStack.headOption match {
            case Some(element) => element.traits+=traitType
            case None => throw new RuntimeException("Cannot call trait() outside of an element")
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

    def attribute(name: String) : Attribute  = {

        var attr = new Attribute(name)
        @->("attribute.add",attr)
        attr
    }

    def attribute(attr: IsWordAttributeWrapper) : Attribute  = {

        attr.left
        
    }

    onWith("attribute.add") {
        attribute : Attribute =>

        // Add only if not already added
        // Fail if no elements on stack
        //-----------
        this.elementsStack.headOption match {

            case Some(element) if (!element.attributes.contains(attribute)) => element.attributes+=attribute
            case Some(element) => 
            case None => 
                throw new RuntimeException("Cannot create attribute outside an element")

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
    def toXML : String = {

        println(s"Top Elements count ${topElements.size}")

        // Create StaxIO
        //-------------------
        var io = new StAXIOBuffer()
        this - io

        // Streamout
        //--------------
        this.streamOut {
            du => 
                du("prefixes" -> Map( (com.idyria.osi.ooxoo.core.ns -> "ooxoo")  ))
                du
        }
        //this.streamOut()

        // Return res
        new String(io.output.toByteArray)

    }

}





object ModelBuilder {



}

// Common
//--------------------
trait Common {

    @xattribute(name="name")
    var name :XSDStringBuffer = null

    @xattribute(name="minOccurs")
    var minOccurs = IntegerBuffer(1)

    @xattribute(name="maxOccurs")
    var maxOccurs = IntegerBuffer(1)

    @xattribute(name="ClassType")
    var classType : XSDStringBuffer = null


}

// Element Model
//-----------------------
@xelement(name="Element")
class Element(
    inputName : String
       ) extends ElementBuffer with Common {

    // Related Type
    //------------------

    var parent : Element = null

    /**
        If set, this element is just instanciating the defined Element, so no need to write it out as oyn type
    */
    var instanceOfElement : Element = null

    // Defaults
    //-------------
    this.classType = classOf[ElementBuffer].getCanonicalName
    this.name = inputName

    // Description
    //-----------------------
    @xattribute(name="isTrait")
    var isTrait : BooleanBuffer = false

    // Sub Elements
    //-------------------

    @xelement(name="Element")
    var elements = XList { du => new Element(du.element.name)}

    @xelement(name="Attribute")
    var attributes = XList { du => new Attribute(du.element.name)}

    @xelement(name="Trait")
    var traits = XList{ new XSDStringBuffer }

}
object Element {

    implicit def stringToElement(str: String) :  Element = {


        new Element(str)
  

    }

}

// Attribute Model
//-------------------------
@xelement(name="Attribute")
class Attribute( inputName : String ) extends ElementBuffer with Common {

    // Defaults
    //-------------
    this.classType = classOf[XSDStringBuffer].getCanonicalName
    this.name = inputName

}
object Attribute {

    implicit def stringToAttribute(str: String) :  Attribute = new Attribute(str)
}
