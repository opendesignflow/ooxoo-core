package com.idyria.osi.ooxoo.model

import com.idyria.osi.ooxoo.core.buffers.datatypes._
import com.idyria.osi.ooxoo.core.buffers.structural._

import scala.language.implicitConversions

/**
   Contains the methods and tree stacking utilities to be able to define an XML model in a convienient way.

   The model can then be exported to documentation or Source files

*/
class ModelBuilder extends Model with ModelBuilderLanguage {

    /*class IsWordElementWrapper( var left: Element) {

        def is(right: => Unit) = {

            println("in is definition for Element")
            right

        }
    }
    implicit def elementToIsWordWrapping(str: String) :  IsWordElementWrapper = new IsWordElementWrapper(str)*/

    // Element Creation/Editing
    //--------------------

    //-- Element stack and current
    var elementsStack = scala.collection.mutable.Stack[Element]()
    //var current

    /**
        When an element is added
    */
    onWith("element.start") {

    elt : Element => 

        // If there is a current element, add new element to it
        elementsStack.headOption match {
            case Some(top) => top.elements += elt
            case _ =>
        }

        // Stack element
        elementsStack.push elt

    }

    on("element.end") {

        // Unstack element
        if (elementsStack.size > 0) {

            elementsStack.pop
        }
    }

    // Attribute Creation/Editing
    //---------------------




}





object ModelBuilder {



}

// Element Model
//-----------------------
@xelement
class Element(var name : QName ) {

    // Sub Elements
    //-------------------

    @xelement
    var elements = XList { du => new Element(du)}

    @xelement
    var attributes = XList { du => new Attribute(du)}

}
object Element {

    implicit def stringToElement(str: String) :  Element = new Element(str)

}

// Attribute Model
//-------------------------
class Attribute(var name : QName ) {

    var type : Class[_ <: Buffer] = classOf[XSDStringBuffer]

}
object Attribute {

    implicit def stringToAttribute(str: String) :  Element = new Element(str)
}
