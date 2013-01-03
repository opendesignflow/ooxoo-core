/**
 *
 */
package com.idyria.osi.ooxoo3.core.buffers.datatypes

class QName(localPart:String) extends javax.xml.namespace.QName(localPart) {
  
  
  
  
}

/**
 * @author rleys
 *
 */
object QName {

  implicit def convertStringToQName(str: String) : QName = new QName(str)
  
}