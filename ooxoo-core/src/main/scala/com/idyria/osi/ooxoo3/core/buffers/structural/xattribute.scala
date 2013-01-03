/**
 *
 */
package com.idyria.osi.ooxoo3.core.buffers.structural

import scala.annotation.StaticAnnotation
import scala.reflect.runtime.universe._

/**
 * @author rleys
 *
 */
class xattribute(var name : String = null) extends StaticAnnotation {

  var namespace : String = null
  
  /**
   * Contains a reference to the buffer the annotation is attached to
   */
  var attachedBuffer : Buffer = null
  
}

object xattribute {
  
  
  def instanciate(source: scala.reflect.api.Universe#Symbol) : xattribute = {
    
    source.annotations.find(a => a.tpe.erasure == typeOf[xattribute]) match {
      
      case Some(a) => instanciate(a, source) 
      case _ => return null
    }
    
  }
  
  def instanciate(a: scala.reflect.api.Universe#Annotation,source: scala.reflect.api.Universe#Symbol): xattribute = {

    // Create
    //---------
    var attr = new xattribute
    
//    println("--> Instanciating xattribute for symbol: "+source.name.decoded)
    
    // Use args to create
    //---------------------
    var argStr = a.scalaArgs.mkString(",")
    
    // Constant
    if (argStr.startsWith("\"")) {
      attr.name = argStr.replaceAll("\"", "").trim()
  	} else {
        // Use symbol name
	     attr.name = source.name.decoded.trim()
     }
    attr

  }
  
  
  
}