/**
 *
 */
package com.idyria.osi.ooxoo.core.utils

import scala.annotation.Annotation
import scala.reflect.runtime.universe._
import com.idyria.osi.ooxoo.core.buffers.datatypes.XSDStringBuffer

import java.lang.reflect._

import com.idyria.osi.tea.logging.TLog

trait ReflectUtilsTrait {

  var resolvedFields: Option[Iterable[Field]] = None

  def getFields(source: AnyRef): Iterable[Field] = {

    resolvedFields match {
      case Some(fields) => fields
      case None =>

        ScalaReflectUtils.cachedFields(getClass) match {
          case Some(fields) => 
            resolvedFields = Some(fields)
            fields
          case None =>

            var allFields = List[Field]()
            var currentClass: Class[_] = source.getClass
            while (currentClass != null) {
              for (field <- (currentClass.getFields()))
                allFields = allFields :+ field
              for (field <- (currentClass.getDeclaredFields()))
                allFields = allFields :+ field
              currentClass = currentClass.getSuperclass()
            }

            ScalaReflectUtils.cacheMap += (getClass -> allFields)
            resolvedFields = Some(allFields)
            allFields

        }
    }

    /* var allFields = List[Field]()
    var currentClass: Class[_] = source.getClass
    while (currentClass != null) {
      for (field <- (currentClass.getFields()))
        allFields = allFields :+ field
      for (field <- (currentClass.getDeclaredFields()))
        allFields = allFields :+ field
      currentClass = currentClass.getSuperclass()
    }

    /*println("On "+source)
    allFields.foreach {
      f => println("  -> "+f.getName)
    }*/

    allFields*/

  }

  /**
   * Returns all the fields of the source that have the provided annotation types
   */
  def getAnnotatedFields[AT <: java.lang.annotation.Annotation](source: AnyRef, annotationClass: Class[AT]): Iterable[Field] = {

    // Get Fields
    //--------------------
    var fields = this.getFields(source);

/*
 * #%L
 * Core runtime for OOXOO
 * %%
 * Copyright (C) 2008 - 2014 OSI / Computer Architecture Group @ Uni. Heidelberg
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

    var res = fields.filter {
      field => field.getAnnotation(annotationClass) != null
    }

    //    res.foreach {
    //      field =>
    //        TLog.logFine("Res Available: " + field.toString())
    //    }

    res

    //return null

  }

  def getFieldValue[T <: Any](source: AnyRef, field: Field): T = {

    // get Field and reutrn
    //-----------------
    field.setAccessible(true)
    return field.get(source).asInstanceOf[T]

  }

  def instanciateFieldValue[T <: Any](source: AnyRef, field: Field): T = {

    // get Type, and see if it is a subclass
    //--------------
    var fieldType = field.getType

    // println(s"Trying ot instancicate for field ${field.getName} of type ${field.getType}, with super: "+fieldType.getSuperclass())

    fieldType.getEnclosingClass match {

      // Enclosed class
      case eClass if (eClass != null) =>

        //println(s"Type is an embedded type, with enclosing: ${eClass}")

        // fieldType.getConstructors().foreach {
        //   c => println("---> available constructor: "+c)
        //}
        var obj = fieldType.getDeclaredConstructor(eClass).newInstance(source).asInstanceOf[T]
        field.setAccessible(true)
        field.set(source, obj)
        return obj

      // Not an enclosing class
      case null =>
        var obj = field.getType.newInstance.asInstanceOf[T]
        field.setAccessible(true)
        field.set(source, obj)
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

/**
 * @author rleys
 *
 */
object ScalaReflectUtils {

  var cacheMap = scala.collection.mutable.Map[Class[_], Iterable[Field]]()

  def cachedFields(cl: Class[_]): Option[Iterable[Field]] = {

    cacheMap.get(cl)
  }

  def getFields(source: AnyRef): Iterable[Field] = {

    var allFields = List[Field]()
    var currentClass: Class[_] = source.getClass
    while (currentClass != null) {
      for (field <- (currentClass.getFields()))
        allFields = allFields :+ field
      for (field <- (currentClass.getDeclaredFields()))
        allFields = allFields :+ field
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
  def getAnnotatedFields[AT <: java.lang.annotation.Annotation](source: AnyRef, annotationClass: Class[AT]): Iterable[Field] = {

    // Get Fields
    //--------------------
    var fields = this.getFields(source);

    var res = fields.filter {
      field => field.getAnnotation(annotationClass) != null
    }

    //    res.foreach {
    //      field =>
    //        TLog.logFine("Res Available: " + field.toString())
    //    }

    res

    //return null

  }

  def getFieldValue[T <: Any](source: AnyRef, field: Field): T = {

    // get Field and reutrn
    //-----------------
    field.setAccessible(true)
    return field.get(source).asInstanceOf[T]

  }

  def instanciateFieldValue[T <: Any](source: AnyRef, field: Field): T = {

    // get Type, and see if it is a subclass
    //--------------
    var fieldType = field.getType

    // println(s"Trying ot instancicate for field ${field.getName} of type ${field.getType}, with super: "+fieldType.getSuperclass())

    fieldType.getEnclosingClass match {

      // Enclosed class
      case eClass if (eClass != null) =>

        //println(s"Type is an embedded type, with enclosing: ${eClass}")

        // fieldType.getConstructors().foreach {
        //   c => println("---> available constructor: "+c)
        //}
        var obj = fieldType.getDeclaredConstructor(eClass).newInstance(source).asInstanceOf[T]
        field.setAccessible(true)
        field.set(source, obj)
        return obj

      // Not an enclosing class
      case null =>
        var obj = field.getType.newInstance.asInstanceOf[T]
        field.setAccessible(true)
        field.set(source, obj)
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
