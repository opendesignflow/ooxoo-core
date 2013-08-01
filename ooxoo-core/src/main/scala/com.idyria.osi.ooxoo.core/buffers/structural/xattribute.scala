/**
 *
 */
package com.idyria.osi.ooxoo.core.buffers.structural

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
    
    //-- Construct
    a.scalaArgs.foreach( _ match {
        
        case arg  if (arg.toString.startsWith("\"") && attr.name==null) =>
        	attr.name = arg.toString.replaceAll("name=", "").replaceAll("\"", "").trim
    	 case arg  if (arg.toString.startsWith("ns=")) =>
        	attr.namespace = arg.toString.replaceAll("ns=", "").replaceAll("\"", "").trim
    	 case arg => 
      })
      
      // Fix Name
      //------------
      if (attr.name == null) {
        attr.name = source.name.decoded.trim()
      }
    /*
    var argStr = a.scalaArgs.mkString(",")
    
    // Constant
    if (argStr.startsWith("\"")) {
      attr.name = argStr.replaceAll("\"", "").trim()
  	} else {
        // Use symbol name
	     attr.name = source.name.decoded.trim()
     }*/
    attr

  }
  
  
  
}