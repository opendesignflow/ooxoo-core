package com.idyria.osi.ooxoo.core.buffers.structural



class AnyElementBuffer extends ElementBuffer {

    var name : String = null

}

/*class AnyXList( cl: => AnyElementBuffer) extends XList[AnyElementBuffer](AnyXList) {

}*/

object AnyXList {

    def apply() = {
 
        XList[ElementBuffer] {
            new AnyElementBuffer
        }

    }

}
