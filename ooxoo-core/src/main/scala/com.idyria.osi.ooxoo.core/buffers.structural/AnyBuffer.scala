package com.idyria.osi.ooxoo.core.buffers.structural

import com.idyria.osi.ooxoo.core.buffers.datatypes.XSDStringBuffer


trait AnyBufferTrait extends Buffer {

    var name : String = null
}

@any
class AnyElementBuffer extends ElementBuffer with AnyBufferTrait {

    @any 
    var content = AnyXList()

}

@any
class AnyAttributeBuffer extends XSDStringBuffer with AnyBufferTrait {


}

/*class AnyXList( cl: => AnyElementBuffer) extends XList[AnyElementBuffer](AnyXList) {

}*/

object AnyXList {

    def apply() = {
        
        /**
            Creating an XList of anyBuffer
            The Buffers are setup depending on data unit provided
        */
        XList[Buffer] {
            du : DataUnit => 

                (du.element,du.attribute) match {
                    case (element,null) => 

                        var elementBuffer = new AnyElementBuffer
                        elementBuffer.name = element.name

                        elementBuffer


                    case (null,attribute) =>

                        var attributeBuffer = new AnyAttributeBuffer
                        attributeBuffer.name = attribute.name

                        attributeBuffer

                       //null 

                    case _ => null
                }

                
                 
        }

    }

}
