/**
 *
 */
package com.idyria.osi.ooxoo.core.utils

import scala.annotation.Annotation
import scala.reflect.runtime.universe._
import com.idyria.osi.ooxoo.core.buffers.structural.BaseBuffer
import com.idyria.osi.ooxoo.core.buffers.datatypes.XSDStringBuffer

/**
 * @author rleys
 *
 */
object ScalaReflectUtils {

  
  def getFields(source: AnyRef) : Iterable[Symbol] = {
    
     // Get type tag
    //--------------------
    var baseTT = scala.reflect.runtime.universe.manifestToTypeTag(scala.reflect.runtime.currentMirror, Manifest.singleType(source))

    /*baseTT.tpe.members.foreach {

      (f:Any) => println("Available: " + f.toString())

    }*/
    
    // Filter Methods out
    //------------------
    var allFields = Set[Symbol]()
    
    //-- Get over base classes
    baseTT.tpe.baseClasses.foreach {
      symbol => 
        symbol.typeSignature.members.filter(
        		(m: Any) =>
        			m.asInstanceOf[scala.reflect.api.Universe#Symbol].isTerm 
        			&& !m.asInstanceOf[scala.reflect.api.Universe#Symbol].isMethod
        		).foreach(allFields += _.asInstanceOf[Symbol])
    }
    
    baseTT.tpe.members.filter(
      (m: Any) =>
        m.asInstanceOf[scala.reflect.api.Universe#Symbol].isTerm 
        && !m.asInstanceOf[scala.reflect.api.Universe#Symbol].isMethod
     ).foreach(allFields += _.asInstanceOf[Symbol])
    
     allFields
    
  }
  
  /**
   * Returns all the fields of the source that have the provided annotation types
   */
  def getAnnotatedFields[AT <: Annotation](source: AnyRef,annotations: scala.reflect.runtime.universe.Type*) : Iterable[Symbol] = {
    
    
    // Get Fields
    //--------------------
    var fields = this.getFields(source);
    
    var res = fields.filter {
        field => field.annotations.count(a => annotations.count( _ == a.tpe.erasure)>0)>0
    }
    
//    res.foreach {
//      field =>
//        println("Res Available: " + field.toString())
//    }
    
    res
    
    
  }
  
  
  def getFieldValue[T <: Any](source: AnyRef,field: Symbol) : T = {
    
     // Get Instance mirror of source
     var mirror = scala.reflect.runtime.universe.runtimeMirror(Thread.currentThread().getContextClassLoader());
     var instanceMirror = mirror.reflect(source)
     var fieldMirror = instanceMirror.reflectField(field.asTerm)
     fieldMirror.get.asInstanceOf[T]
     
  }
  
  def instanciateFieldValue[T <: Any](source: AnyRef,field: Symbol) : T = {
    
    
    // Get Instance mirror of source
    //-----------
     var mirror = scala.reflect.runtime.universe.runtimeMirror(Thread.currentThread().getContextClassLoader());
     var instanceMirror = mirror.reflect(source)
    
    // Get instance mirror for field
     //--------------------
     var fieldMirror = instanceMirror.reflectField(field.asTerm)
     
     //var fieldInstanceMirror = mirror.reflect(fieldMirror.symbol)
     
     
    
     
     var fieldClassMirror = if (! field.typeSignature.typeSymbol.asClass.isStatic) {
       
       println("This is a subclass")
       var m = mirror.reflect(field.typeSignature.typeSymbol.asClass.owner)
       
       m.symbol.filter{ s => println(s"Found symbol: $s");true}
       
       instanceMirror.reflectClass(field.typeSignature.typeSymbol.asClass)
       
     } else {
      
       mirror.reflectClass(fieldMirror.symbol.typeSignature.typeSymbol.asClass)
     }
     
     // Instanciate type
     //-------------------
     
     //-- Pick constructor, but first alternative (default constructor)
     var constructorSymbol = field.typeSignature.declaration(scala.reflect.runtime.universe.nme.CONSTRUCTOR).asTerm.alternatives.head 
     var constructor = constructorSymbol.asMethod
     
     //-- Mirror field type class and constructor method
     //mirror.reflectClass(field.typeSignature.typeSymbol.asClass)
     //var fieldClassMirror = fieldInstanceMirror.reflectClass(field.typeSignatureIn(site).typeSignature.typeSymbol.asClass)
     //var fieldClassMirror = fieldInstanceMirror.reflectClass(fieldMirror.symbol.typeSignature.typeSymbol.asClass)
     var constructorMirror = fieldClassMirror.reflectConstructor(constructor);
     
     
   
     
     //-- Apply constructor
     
     //println("--> Instanciate: "+constructorMirror());
     
     //-- Return
    fieldMirror.set(constructorMirror())
     fieldMirror.get.asInstanceOf[T]
     
    
    
  }
  
  
  def instanciateType[T <: Any](sourceType: Symbol) : T = {
    
    // Get Instance mirror of source
     var mirror = scala.reflect.runtime.universe.runtimeMirror(Thread.currentThread().getContextClassLoader());
     
     
     // Instanciate type
     //-------------------
     
     //-- Pick constructor, but first alternative (default constructor)
     var constructor = sourceType.typeSignature.declaration(scala.reflect.runtime.universe.nme.CONSTRUCTOR).asTerm.alternatives.head.asMethod
     
     //-- Mirror field type class and constructor method
     var fieldClassMirror = mirror.reflectClass(sourceType.typeSignature.typeSymbol.asClass)
     var constructorMirror = fieldClassMirror.reflectConstructor(constructor);
     
     //-- Apply constructor
     
     //println("--> Instanciate: "+constructorMirror());
     
     //-- Return
     constructorMirror().asInstanceOf[T]
  }
  
  
}