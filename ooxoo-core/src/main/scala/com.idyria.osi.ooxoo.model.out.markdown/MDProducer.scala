package com.idyria.osi.ooxoo.model.out.markdown

import com.idyria.osi.ooxoo.model._
import com.idyria.osi.ooxoo.core.buffers.structural._
import com.idyria.osi.ooxoo.core.buffers.datatypes._

/**
    This Producer creates scala class implementations for the models

*/
class MDProducer extends ModelProducer {

    this.outputType = "markdown"


 

    def produce(model: Model, out : Writer) = {

        out.file(s"./${model.name}.md")

        def writeElementDescription(element: Element) : Unit = {

            // Title and description
            //--------------
            var titleLevel = (0 to element.depth).map { i => "#"}.mkString
            element.maxOccurs match {
                case occurences if(occurences>1) => out << s"$titleLevel ${element.name} +" << ""
                case _ =>                           out << s"$titleLevel ${element.name}" << ""
            }
            

            // Description
            if (element.description!=null) {
                out << s"${element.description}" << ""
     
            }

            // Write XML
            //--------------
            out.indent
            writeElement(element)
            out.outdent

            // Content
            //-------------------------

            //-- Import All Elements and attributes from the imported traits
            var elements = List[Element]() ++ element.elements
            var attributes = List[Attribute]() ++ element.attributes

            element.traits.foreach { _ match {

                // Trait Is defined in top elements
                case usedTrait if (model.topElements.exists( elt => elt.name.toString.matches(s"(.+:)?${usedTrait}") )) => 

                    var traitElement = model.topElements.find(_.name.toString.matches(s"(.+:)?${usedTrait}")).get
                    
                    elements = elements ++ traitElement.elements
                    attributes = attributes ++ traitElement.attributes
               

                // It is a general trait defined somewhere else
                case usedTrait => 
                    //out << s"Uses Trait: $usedTrait"
                    //out << ""

            }     
            } 

            //-- Attributes
            attributes.foreach {

                attr => 
                    
                    // Title
                    var titleLevel = (0 to element.depth+1).map { i => "#"}.mkString
                    out << s"$titleLevel @${attr.name}" << ""
            
                    // Description
                    if (attr.description!=null) {
                        out << s"${attr.description}" << ""
             
                    }

            }

            //-- Elements
            elements.foreach {

                elt => 

                    // Title
                    /*var titleLevel = (0 to element.depth+2).map { i => "#"}.mkString
                    out << s"$titleLevel ${elt.name}"

                    // Description
                    if (elt.description!=null) {
                        out << s"${elt.description}" << ""
             
                    }*/
                    writeElementDescription(elt)
            }

            //-- Sub Elements
           // element.elements.foreach(writeElementDescription(_))
        }

        // Write XML Code Example
        //----------------------------
        def writeElement( element : Element ) : Unit = {

            // Start 
            //----------------  
            var startClosed = false
            element.attributes.size match {
                case 0 => out << s"<${element.name}>" ; startClosed = true
                case _ => out << s"<${element.name}"
            }  
            

            //-- Attributes
            element.attributes.foreach {
                attribute => 
                    out << s"""${attribute.name} = "${attribute.classType}" """
            }

            //-- EOF start
            if (!startClosed)
                out << s">" 
            out.indent
            out << ""

                //-- Classtype
                out << s"Class Type: ${element.classType}"
               out << ""

                //-- Elements from imported traits
                element.traits.foreach { _ match {

                    // Trait Is defined in top elements
                    case usedTrait if (model.topElements.exists( elt => elt.name.toString.matches(s"(.+:)?${usedTrait}") )) => 

                        writeElement(model.topElements.find(_.name.toString.matches(s"(.+:)?${usedTrait}")).get)  
                        out << ""

                    // It is a general trait defined somewhere else
                    case usedTrait => 
                        out << s"Uses Trait: $usedTrait"
                        out << ""

                }     
                }


                //-- Children elements
                element.elements.foreach { elt => 
                        writeElement(elt) 

                }

            //-- Close
             out.outdent
            out << s"</${element.name}>"
   
            
            
        }

        // Start on top elements
        //----------------------------
        out << s"${model.name} Documentation"
        out << "=============================="
        out << "" << ""

  

        model.topElements.foreach {
            element => 

                //out << s"## ${element.name}"
                //out << ""
                writeElementDescription(element)

        }

        

    }
}
