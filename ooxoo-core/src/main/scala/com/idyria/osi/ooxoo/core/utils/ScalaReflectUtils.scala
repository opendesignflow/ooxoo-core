/**
 *
 */
package com.idyria.osi.ooxoo.core.utils

import scala.annotation.Annotation
import scala.reflect.runtime.universe._
import com.idyria.osi.ooxoo.core.buffers.datatypes.XSDStringBuffer

import java.lang.reflect._

import org.odfi.tea.logging.TLog
import com.idyria.osi.ooxoo.core.buffers.structural.xelement
import com.idyria.osi.ooxoo.core.buffers.structural.xattribute
import com.idyria.osi.ooxoo.core.buffers.structural.xelement_base
import com.idyria.osi.ooxoo.core.buffers.structural.xattribute_base
import com.idyria.osi.ooxoo.core.buffers.structural.AbstractDataBuffer
import jakarta.persistence.Transient
import com.idyria.osi.ooxoo.core.buffers.structural.Buffer
import com.idyria.osi.ooxoo.core.buffers.datatypes.IntegerBuffer

trait ReflectUtilsTrait {

  @Transient
  var resolvedFields: Option[Iterable[Field]] = None

  def listElementAndAttributeFields = {
    getFields(this).collect {
      
      case f if (f.getAnnotationsByType(classOf[xelement]).size>0) => 
        
        var elementbase = xelement_base(f)
        (elementbase.name,f)
       case f if (f.getAnnotationsByType(classOf[xattribute]).size>0) => 
         var attributeBase = xattribute_base(f)
        (attributeBase.name,f)
         
    }
  }
  
  def listDataElementAndAttributeFields = listElementAndAttributeFields.filter {
    
    case (name,field) => classOf[AbstractDataBuffer[_]].isAssignableFrom(field.getType)
  }
 
  /**
   * List all Fields Recursively
   */
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
 * Copyright (C) 2006 - 2017 Open Design Flow
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
    
    // Support basic types readout
    //-------------------
   val res =  field.get(source) match {
      case i : Integer => IntegerBuffer(i)
      case other => other
    
    }
    return res.asInstanceOf[T]
    //return field.get(source).asInstanceOf[T]

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
        var obj = field.getType.getDeclaredConstructor().newInstance().asInstanceOf[T]
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
        var obj = field.getType.getDeclaredConstructor().newInstance().asInstanceOf[T]
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
