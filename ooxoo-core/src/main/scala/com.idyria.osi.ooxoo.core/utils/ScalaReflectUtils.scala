/**
 *
 */
package com.idyria.osi.ooxoo.core.utils

import scala.annotation.Annotation
import scala.reflect.runtime.universe._
import com.idyria.osi.ooxoo.core.buffers.structural.BaseBuffer
import com.idyria.osi.ooxoo.core.buffers.datatypes.XSDStringBuffer

import java.lang.reflect._

import com.idyria.osi.tea.logging.TLog

/**
 * @author rleys
 *
 */
object ScalaReflectUtils {


  def getFields(source: AnyRef) : Iterable[Field] = {

    var allFields = Set[Field]()
    var currentClass : Class[_]  = source.getClass
    while (currentClass != null) {
      for (field <- (currentClass.getFields()))
       allFields += field
      for (field <- (currentClass.getDeclaredFields()))
        allFields += field
      currentClass = currentClass.getSuperclass()
    }

    /*println("On "+source)
    allFields.foreach {
      f => println("  -> "+f.getName)
    }*/

    allFields


  }

  /**
   * Returns all the fields of the source that have the provided annotation types
   */
  def getAnnotatedFields[AT <: java.lang.annotation.Annotation](source: AnyRef,annotationClass: Class[AT]) : Iterable[Field] = {


    // Get Fields
    //--------------------
    var fields = this.getFields(source);

    var res = fields.filter {
        field => field.getAnnotation(annotationClass)!=null
    }

//    res.foreach {
//      field =>
//        TLog.logFine("Res Available: " + field.toString())
//    }

    res

    //return null


  }


  def getFieldValue[T <: Any](source: AnyRef,field: Field) : T = {

    // get Field and reutrn
    //-----------------
    field.setAccessible(true)
    return field.get(source).asInstanceOf[T]




  }

  def instanciateFieldValue[T <: Any](source: AnyRef,field: Field) : T = {


    // get Type, and see if it is a subclass
    //--------------
    var fieldType = field.getType

   // println(s"Trying ot instancicate for field ${field.getName} of type ${field.getType}, with super: "+fieldType.getSuperclass())

    fieldType.getEnclosingClass match {

      // Enclosed class
      case eClass if(eClass!=null) =>

        //println(s"Type is an embedded type, with enclosing: ${eClass}")

       // fieldType.getConstructors().foreach {
       //   c => println("---> available constructor: "+c)
        //}
        var obj = fieldType.getDeclaredConstructor(eClass).newInstance(source).asInstanceOf[T]
        field.setAccessible(true)
        field.set(source,obj)
        return obj

      // Not an enclosing class
      case null =>
        var obj = field.getType.newInstance.asInstanceOf[T]
        field.setAccessible(true)
        field.set(source,obj)
        return obj
    }

    // Instanciate type
    //----------
    //var obj = field.getType.newInstance

    // Set and return
    //-------------------
    //field.setAccessible(true)
    //field.set(source,obj)

    //return obj.asInstanceOf[T]


  }



}
