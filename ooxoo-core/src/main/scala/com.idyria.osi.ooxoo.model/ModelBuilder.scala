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

    /*class IsWordElementWrapper( var left: Element) {

        def is(right: => Unit) = {

            println("in is definition for Element")
            right

        }
    }
    implicit def elementToIsWordWrapping(str: String) :  IsWordElementWrapper = new IsWordElementWrapper(str)*/

    // Element Creation/Editing
    //--------------------

    //-- Top Elements list
    @xelement(name="XElement")
    var topElements = XList{ du => new Element(du)}

    //-- Element stack and current
    var elementsStack = scala.collection.mutable.Stack[Element]()
    //var current

    /**
        When an element is added
    */
    onWith("element.start") {

    elt : Element => 

        println("Inside element.start")

        // If there is a current element, add new element to it, otherwise it is a top element
        elementsStack.headOption match {
            case Some(top) => top.elements += elt
            case None => topElements+=elt
        }

        // Stack element
        elementsStack.push(elt)

    }

    on("element.end") {

        // Unstack element
        if (elementsStack.size > 0) {

            elementsStack.pop
        }
    }

    // Attribute Creation/Editing
    //---------------------



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

// Element Model
//-----------------------
@xelement(name="XElement")
class Element(
    inputName : QName
       ) extends ElementBuffer {

    // Sub Elements
    //-------------------

    @xattribute(name="xname")
    var name : QName = inputName

    @xelement(name="XElement")
    var elements = XList { du => new Element(du)}

    @xelement(name="XAttribute")
    var attributes = XList { du => new Attribute(du)}

}
object Element {

    implicit def stringToElement(str: String) :  Element = new Element(str)

}

// Attribute Model
//-------------------------
@xelement(name="Attribute")
class Attribute(var name : QName ) extends ElementBuffer {

    @xattribute(name="ClassType")
    var classType : Class[_ <: Buffer] = classOf[XSDStringBuffer]

}
object Attribute {

    implicit def stringToAttribute(str: String) :  Element = new Element(str)
}
