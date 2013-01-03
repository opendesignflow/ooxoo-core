/**
 *
 */
package com.idyria.osi.ooxoo3.core.buffers.structural

import scala.annotation.Annotation
import scala.annotation.StaticAnnotation
import scala.reflect.runtime.universe._
import com.idyria.osi.ooxoo3.core.utils.ScalaReflectUtils

/**
 * @author rleys
 *
 */
class xelement(var name: String = null) extends StaticAnnotation {

  var namespace: String = null

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
    var argStr = a.scalaArgs.mkString(",")
    
    // Constant
    if (argStr.startsWith("\"")) {
      elt.name = argStr.replaceAll("\"", "").trim
  	} else {
        // Use symbol name
	     elt.name = source.name.decoded.trim
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