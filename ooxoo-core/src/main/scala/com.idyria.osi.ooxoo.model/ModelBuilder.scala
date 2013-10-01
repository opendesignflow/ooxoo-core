package com.idyria.osi.ooxoo.model

import com.idyria.osi.ooxoo.core.buffers.datatypes._
import com.idyria.osi.ooxoo.core.buffers.structural._
import com.idyria.osi.ooxoo.core.buffers.structural.io.sax._
import scala.language.implicitConversions
import java.io.ByteArrayOutputStream

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

        //println("Inside element.start for "+elt.name)

        // - If there is a current element, add new element to it, otherwise it is a top element
        // - If the target element lists already contains this one, just stack
        elementsStack.headOption match {

            case Some(top) if(top.elements.contains(elt)) =>

            case Some(top) => 
                    elt.parent=top;
                    top.elements += elt

            case None if(topElements.contains(elt)) => topElements+=elt
            case None => topElements+=elt
        }

        // Stack element
        elementsStack.push(elt)

    }

    onWith("element.end") { elt : Element => 

       // println("Inside element.end for "+elt.name)
 
        // Unstack element
        if (elementsStack.size > 0) {

            elementsStack.pop
        }
    }
    
    def classType(classType: String) : Unit  = {
      
      elementsStack.headOption match {
            case Some(element) => element.classType = classType
            case None => throw new RuntimeException("Cannot call classType() outside of an element")
        }
      
    }
    
    def classType(element: Element) : Unit = classType(element.classType.toString)

    def withTrait(traitType: String) = {

        elementsStack.headOption match {
            case Some(element) => element.traits+=traitType
            case None => throw new RuntimeException("Cannot call withTrait() outside of an element")
        }

    }

    def withTrait(traitType: Element) = {

        elementsStack.headOption match {
            case Some(element) => element.traits+=traitType.name
            case None => throw new RuntimeException("Cannot call withTrait() outside of an element")
        }

    }

    def isTrait = {

        elementsStack.headOption match {
            case Some(element) => element.isTrait = true
            case None => throw new RuntimeException("Cannot call isTrait() outside of an element")
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


    implicit val defaultDesc = { "" } 
    def attribute(name: String)(implicit desc: String) : Attribute  = {

        // Create Attribute 
        var attr = new Attribute(name)
        @->("attribute.add",attr)
        attr

        // Set Description
        attr.description = desc

        attr
    }

    def -@(name:String)(implicit desc: String) : Attribute  = attribute(name)(desc)

    def attribute(attr: IsWordAttributeWrapper)(implicit desc: String) : Attribute  = {

        // Set Description
        attr.left.description = desc

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

    // General Descriptions and such
    //---------------------

    /**
        Sets the description of current element
    */
    def withDescription(desc: String) : Unit = {

        this.elementsStack.headOption match {

            case Some(element)  => element.description = desc
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
        new String(io.output.asInstanceOf[ByteArrayOutputStream].toByteArray)

    }

}





object ModelBuilder {



}

// Common
//--------------------
trait Common {

    @xattribute(name="name")
    var name :XSDStringBuffer = null

    @xelement(name="Description")
    var description : XSDStringBuffer = null 

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

    def depth : Int = {

        var res = 0
        var current = this 
        while(current.parent!=null) {
            res += 1 
            current = current.parent
        }
        res 
    }

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

    def apply(desc: String) = {
        this.description = desc
    }

    /*def :+ (desc: String) = {

        println("Setting Description on element") 
        this.description = desc
    }*/

    // Sub Elements
    //-------------------

    @xelement(name="Element")
    var elements = XList { du => 
            var elt = new Element(du.element.name)
            elt.parent = this
            elt
    }

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
