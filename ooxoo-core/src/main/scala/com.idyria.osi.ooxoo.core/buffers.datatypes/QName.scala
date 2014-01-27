/**
 *
 */
package com.idyria.osi.ooxoo.core.buffers.datatypes

import scala.language.implicitConversions

class QName(localPart:String) extends XSDStringBuffer {

    // QName wrapper
    //-------------
    var qname =  javax.xml.namespace.QName.valueOf(localPart) 

    data = localPart

    def getLocalPart() = qname.getLocalPart

    override def toString = qname.toString

    


}

/**
 * @author rleys
 *
 */
object QName {

  def apply() = new QName("")
  
  implicit def convertStringToQName(str: String) : QName = new QName(str)

}
