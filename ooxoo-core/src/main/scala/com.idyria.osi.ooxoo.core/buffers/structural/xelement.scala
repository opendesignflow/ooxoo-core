/**
 *
 */
package com.idyria.osi.ooxoo.core.buffers.structural

import scala.annotation.Annotation
import scala.annotation.StaticAnnotation
import scala.reflect.runtime.universe._
import com.idyria.osi.ooxoo.core.utils.ScalaReflectUtils

/**
 * @author rleys
 *
 */
class xelement(var name: String = null,var namespace: String = null) extends StaticAnnotation {
  
 
  
}
object xelement {

  def isPresent(test: AnyRef): Boolean = {

    var tt = scala.reflect.runtime.universe.manifestToTypeTag(scala.reflect.runtime.currentMirror, Manifest.singleType(test))

    //tt.tpe.typeSymbol.annotations.find( a => a.tpe.erasure == manifest[xelement].)

    //!.tpe.typeSymbol.annotations.find( a => a.tpe.isInstanceOf[xelement]).isEmpty

    !tt.tpe.typeSymbol.annotations.find {
      a =>
        //println(s"Testing annot: ${a.tpe.typeSymbol.fullName}")
        a.tpe == scala.reflect.runtime.universe.typeOf[xelement]
      //a.tpe.typeSymbol.fullName.equals(xelement.getClass().getCanonicalName())

    }.isEmpty

  }

  def isPresent(symbol: Symbol): Boolean = {

    symbol.annotations.find(a => (a.tpe.erasure == typeOf[xelement] || a.tpe.erasure == typeOf[xattribute])) match {
      case None => false
      case _ => true
    }

  }

  def instanciate(source: scala.reflect.api.Universe#Symbol) : xelement = {
    
    source.annotations.find(a => a.tpe.erasure == typeOf[xelement]) match {
      
      case Some(a) => instanciate(a, source)
      case _ => return null
    }
    
  }
  
  def instanciate(a: scala.reflect.api.Universe#Annotation,source: scala.reflect.api.Universe#Symbol): xelement = {

    // Create
    //---------
    var elt = new xelement
    
    // Use args to create
    //---------------------
    
    //-- Construct
    a.scalaArgs.foreach( _ match {
        
        case arg  if (arg.toString.startsWith("\"") && elt.name==null) =>
        	elt.name = arg.toString.replaceAll("name=", "").replaceAll("\"", "").trim
    	 case arg  if (arg.toString.startsWith("ns=")) =>
        	elt.namespace = arg.toString.replaceAll("ns=", "").replaceAll("\"", "").trim
    	 case arg => 
      })
     
       // Fix Name
      //------------
      if (elt.name == null) {
        elt.name = source.name.decoded.trim()
      }
   
    
   
    elt

  }

  def get(test: AnyRef): List[xelement] = {

    var tt = scala.reflect.runtime.universe.manifestToTypeTag(scala.reflect.runtime.currentMirror, Manifest.singleType(test))

    var annotations = tt.tpe.typeSymbol.annotations.filter(_.tpe == scala.reflect.runtime.universe.typeOf[xelement])

    
    
    var res = annotations.map { instanciate(_,tt.tpe.typeSymbol) }

    res
  }

}