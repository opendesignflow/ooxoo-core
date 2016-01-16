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
package com.idyria.osi.ooxoo.model.out.scala

import com.idyria.osi.ooxoo.model._
import com.idyria.osi.ooxoo.core.buffers.structural._
import com.idyria.osi.ooxoo.core.buffers.datatypes.XSDStringBuffer
import com.idyria.osi.ooxoo.core.buffers.datatypes.EnumerationBuffer
import com.idyria.osi.ooxoo.core.buffers.datatypes.BooleanBuffer
import com.idyria.osi.ooxoo.core.buffers.datatypes.IntegerBuffer
import com.idyria.osi.ooxoo.core.buffers.datatypes.DoubleBuffer
import org.atteo.evo.inflector.English
import com.idyria.osi.ooxoo.core.buffers.datatypes.CDataBuffer

/**
 * This Producer creates scala class implementations for the models
 *
 */
class ScalaProducer extends ModelProducer {

  this.outputType = "scala"

  // Name Cleaning
  //-------------------

  val forbiddenKeyWords = List("for", "trait", "class", "package", "var", "val", "def", "private", "final", "match", "case", "object", "type", "lazy", "extends", "with")

  /**
   * Returns a scala friendly name from base name, without reserved keywords etc...
   */
  def cleanName(name: String): String = {

    // Trim and Lower case first character
    // If all letters are capital, keep it this way
    var res = name.trim().find { c => c.isLower } match {
      case None => name.trim()
      case _ => name.trim().zipWithIndex.map {
        case (c, 0) => c.toLower;

        case (c, i) => c
      }.mkString
    }

    // Prefix with _ is the name is a keyword
    forbiddenKeyWords.contains(res) match {
      case true ⇒ res = res + "_"
      case false ⇒
    }

    res
  }

  /**
   * Makes the name plural
   */
  def makePlural(name: String): String = {

    name match {
      case name if (name.matches(".*s")) => name
      case _ => English.plural(name)
    }

    /*
    name match {
      case name if (name.matches(".*[aeiou]s")) => name+"es"
      case name if (name.matches(".*s")) => name
      case name if (name.matches(".*e")) => name+"s"
      case _ => name+"es"
    }*/
  }

  
  def canonicalClassName(model: Model, element: Element): String = {
    
    var name: String = element.className match {
      case null => element.name
      case name if (element.traitSeparateFromObject != null) => element.traitSeparateFromObject
      case _ => element.className
    }
    
    canonicalClassName(model,name,element)
  }
  
  /**
   * Creates a hierarchical CanonicalName for a class
   */
  def canonicalClassName(model:Model, basename: String, element: Element): String = {


    var name = element.className match {
      case null => element.name
      case _ => element.className
    }

    model.splitName(name) match {
      case (sNs, sName) ⇒ name = sName
    }

    // enumeration and name "Value" are incompatible
    val enumerationBufferClass = classOf[EnumerationBuffer].getCanonicalName()
    var className = (element.classType, name.toString) match {
      case (enumerationBufferClass, "Value") => "_Value"
      case _ => name
    }

    // Merge ClassName with its parents
    // If element is its own parent (recursion), then start on parent
    var current = element.parent match {
      case p if (p != null && p.name.toString == element.name.toString) => p
      case _ => element
    }
    var parentNames = ""
    while (current.parent != null) {

      var currentName = current.parent.className match {
        case null => current.parent.name
        case _ => current.parent.className
      }
      parentNames = s"${model.splitName(currentName.toString)._2}$parentNames"
      current = current.parent
    }

    s"$parentNames$className"

  }

  /**
   * The output package
   *
   * Model parameter: scalaProducer.targetPackage
   */
  var targetPackage: String = ""

  def writeEnumerationValues(localName: String, base: Common, out: Writer) = {

    // Declare Values
    //--------------------
    out << ""
    out << s"type ${localName} = Value"
    out << s"val ${base.enumerationValues.map(_.toString).mkString(",")} = Value"

    // Create Selection methods
    //-------------------------
    out << base.enumerationValues.map {
      value =>

        s"""def select$value : Unit = this select this.$value"""

    }.mkString("\n")

  }

  def produce(model: Model, out: Writer) = {

    // Try to find Target Package from model
    //------------------
    this.targetPackage = model.parameter("scalaProducer.targetPackage") match {
      case Some(p) ⇒ p
      case None ⇒ model.getClass().getPackage().getName()
    }

    //-- Convert Target  Package to Folder path and create as well
    var targetPackagePath = this.targetPackage.replace(".", "/")

    def writeElement(element: Element): Unit = {

      // If Element is an instance of another element, oder is imported don't write out
      //------------
      if (element.instanceOfElement != null || element.imported.data == true) {
        return
      }

      // Check Name
      //-------------------
      var namespace = ""
      var name = element.name
      model.splitName(element.name) match {
        case (sNs, sName) ⇒ namespace = sNs; name = sName
      }
      /*model.namespace(element.name) match {
                case Some(foundNamespace) => 
                    namespace = foundNamespace
                    name = element.name.split(":")(1)
                case None =>
            }*/

      // Class Name: Use Canonical Function, with our class name as base
      //-----------------
      var className = canonicalClassName(model, element.className,element)

      /*// enumeration and name "Value" are incompatible
      val enumerationBufferClass = classOf[EnumerationBuffer].getCanonicalName()
      var className = (element.classType, name.toString) match {
        case (enumerationBufferClass, "Value") => "_Value"
        case _                                 => name
      }

      // Merge ClassName with its parents
      
      var current = element
      var parentNames = ""
      while (current.parent != null) {
        parentNames = s"${model.splitName(current.parent.name.toString)._2}$parentNames"
        current = current.parent
      }

      className = s"$parentNames$className"*/

      // If Type has already been written, don't overwrite it
      //-----------------------
      var fileName = "./" + targetPackagePath + "/" + className + ".scala"
      if (out.fileWritten(fileName)) {
        return
      }

      // Write File
      //-----------------------

      out.file(fileName)

      //-- Package
      out << s"""package $targetPackage
            """

      //-- Import
      out << s"""
import ${classOf[ElementBuffer].getCanonicalName}
import ${classOf[XList[_]].getCanonicalName}
import ${classOf[xattribute].getCanonicalName}
import ${classOf[xelement].getCanonicalName}
import scala.language.implicitConversions
            """

      //-- Class Definition
      (namespace, name) match {
        case ("", name) ⇒ out << s"""@xelement(name="$name")"""
        case (namespace, name) ⇒ out << s"""@xelement(name="$name",ns="$namespace")"""
      }

      //-- Imported Traits
      var traits = element.traits.filterNot(t => t.toString == element.classType.toString) match {
        case traitsList if (traitsList.size > 0) => traitsList.map(model.splitName(_)._2).mkString(" with ", " with ", " ")
        case _ => ""
      }

      // var parents = for( p <- current.parent if(current.parent!=null))

      //-- End of class start
      var classOrTrait = "class"
      if (element.isTrait) {
        classOrTrait = "trait"
      }

      // ClassType : 
      //   - The set classtype
      //   - If imported, create classType from source
      //------------------
      var classType = element.importSource match {
        case null => element.classType
        case source => canonicalClassName(model, source)

      }

      out << s"""$classOrTrait ${className} extends $classType $traits {
            """

      //-- Enumeration
      //-------------------------
      out.indent
      element.enumerationValues.size match {
        case 0 =>
        case _ =>

          // Declare Values
          //--------------------
          out << ""
          out << s"type ${className} = Value"
          out << s"val ${element.enumerationValues.map(_.toString).mkString(",")} = Value"

          // Create Selection methods
          //-------------------------
          out << element.enumerationValues.map {
            value =>

              s"""def select$value : Unit = this select this.$value"""

          }.mkString("\n")

      }
      out.outdent

      //-- Attributes
      //---------------------------
      out.indent
      element.attributes.foreach { attribute ⇒

        //--- Annotation
        var resolvedName = model.splitName(attribute.name)
        var localName = resolvedName match {
          case ("", name) ⇒

            out << s"""@xattribute(name="$name")"""
            name
          case (namespace, name) ⇒

            out << s"""@xattribute(name="$name",ns="$namespace")"""
            name
        }

        //-- Field
        attribute.maxOccurs match {

          case count if (count > 1) ⇒

            out << s"""var ${cleanName(makePlural(resolvedName._2))} = XList { new ${attribute.classType}}
                        """

          // Attribute Needs Subclassing: Enumeration
          //---------------
          case _ if (attribute.classType.toString == classOf[EnumerationBuffer].getCanonicalName()) =>

            out << s"""var ${cleanName(resolvedName._2)} = new ${attribute.classType} {"""

            writeEnumerationValues(localName, attribute, out)

            out << s"""}"""

          // Normal Attribute
          //-------------------
          case _ ⇒

            // Default value
            var defaultValue = attribute.default match {
              case null => "null"
              case defaultValue => s"""${attribute.classType}.convertFromString("$defaultValue")"""
            }

            out << s"""var __${cleanName(resolvedName._2)} : ${attribute.classType} = $defaultValue
                        """
            out << s"""def ${cleanName(resolvedName._2)}_=(v:${attribute.classType}) = __${cleanName(resolvedName._2)} = v
                        """
            /*out << s"""def ${cleanName(resolvedName._2)} : ${attribute.classType} = __${cleanName(resolvedName._2)} match {case null => __${cleanName(resolvedName._2)} = ${attribute.classType}();__${cleanName(resolvedName._2)} case v => v }
                        """*/
            out << s"""def ${cleanName(resolvedName._2)} : ${attribute.classType} = __${cleanName(resolvedName._2)} 
                        """
        }

      }
      out.outdent

      //-- Sub Element
      //---------------------------
      out.indent
      element.elements.foreach { element ⇒

        // Annotation
        var resolvedName = model.splitName(element.name)
        resolvedName match {
          case ("", name) ⇒
            out << s"""@xelement(name="$name")"""

          case (namespace, name) ⇒

            out << s"""@xelement(name="$name",ns="$namespace")"""
        }

        // ResolvedType if imported of not
        // !! If the Element has a different class name and target object, use the target object!
        //-----------------
        var resolvedType = element.imported.data.booleanValue() match {
          
          case true if (element.importSource==null)=> 
            model.splitName(element.classType.toString)._2
            
         case true if (element.importSource!=null)=> 
           
            s"$targetPackage.${canonicalClassName(model, element.importSource)}"
           
          // Resolved Type is in the targetpackage, and is the canonical name of the subelement
          case _ => s"$targetPackage.${canonicalClassName(model, element)}"

        }
        // Element definition
        //---------------
        element.maxOccurs match {

          case count if (count > 1) ⇒

            out << s"""var ${cleanName(makePlural(resolvedName._2))} = XList { new $resolvedType}
                        """

          case _ ⇒

            // Default value
            var defaultValue = element.default match {
              case null => "null"
              case defaultValue => s"""${resolvedType}.convertFromString("$defaultValue")"""
            }

            out << s"""var __${cleanName(resolvedName._2)} : $resolvedType = $defaultValue
                        """

<<<<<<< HEAD:ooxoo-core/src/main/scala/com.idyria.osi.ooxoo.model.out.scala/ScalaProducer.scala
            // setter
=======
            // Automatic Element creation: Yes per default only if the element has children it self
            var getterContent = element.elements.size match {
              case 0 => s"__${cleanName(resolvedName._2)}"
              case _ => s"__${cleanName(resolvedName._2)} match {case null => __${cleanName(resolvedName._2)} = $resolvedType();__${cleanName(resolvedName._2)} case v => v }"
            }

>>>>>>> origin/master:ooxoo-core/src/main/scala/com/idyria/osi/ooxoo/model/out/scala/ScalaProducer.scala
            out << s"""def ${cleanName(resolvedName._2)}_=(v:$resolvedType) = __${cleanName(resolvedName._2)} = v
                        """
            /*out << s"""def ${cleanName(resolvedName._2)} : $resolvedType = __${cleanName(resolvedName._2)} match {case null => __${cleanName(resolvedName._2)} = $resolvedType();__${cleanName(resolvedName._2)} case v => v }
                        """*/
<<<<<<< HEAD:ooxoo-core/src/main/scala/com.idyria.osi.ooxoo.model.out.scala/ScalaProducer.scala
            // getter
            out << s"""def ${cleanName(resolvedName._2)} : $resolvedType = __${cleanName(resolvedName._2)}
           """

            // getter with creator
            out << s""" def ${cleanName(resolvedName._2)}(create:Boolean) : $resolvedType = ${cleanName(resolvedName._2)} match {
             case null if (create) => 
               this.${cleanName(resolvedName._2)} = new $resolvedType()
               this.${cleanName(resolvedName._2)}
             case _ => this.${cleanName(resolvedName._2)}
           }
                      
           """
=======
            out << s"""def ${cleanName(resolvedName._2)} : $resolvedType = $getterContent
                        """
>>>>>>> origin/master:ooxoo-core/src/main/scala/com/idyria/osi/ooxoo/model/out/scala/ScalaProducer.scala
        }
      }

      //-- End of class
      out.outdent
      out << s"""}"""

      // Object Singleton Definition
      //-----------------------------
      if (!element.isTrait) {

        var objectName = element.traitSeparateFromObject match {
          case null => className
          case targetName => targetName
        }
        out << s"object ${objectName} {"
        out << ""
        out.indent

<<<<<<< HEAD:ooxoo-core/src/main/scala/com.idyria.osi.ooxoo.model.out.scala/ScalaProducer.scala
      //-- Add URL constructor factory if type is not abstract
      //----------------

      //-- Add An Automatic conversion from base type if it is a base type
      //---------------
      if (!element.isTrait) {

        //-- Add From URL Factory
        out << s"""
=======
        //-- Add Simple constructor factory if type is not abstract
        //----------------
        if (!element.isTrait) {
          out << s"def apply() = new $objectName"
          out << ""
        }

        //-- Add URL constructor factory if type is not abstract
        //----------------

        //-- Add An Automatic conversion from base type if it is a base type
        //---------------
        if (!element.isTrait || element.traitSeparateFromObject != null) {

          //-- Add From URL Factory
          out << s"""
>>>>>>> origin/master:ooxoo-core/src/main/scala/com/idyria/osi/ooxoo/model/out/scala/ScalaProducer.scala
def apply(url : java.net.URL) = {
  
  // Instanciate
  var res = new $objectName
  
  // Set Stax Parser and streamIn
  var io = com.idyria.osi.ooxoo.core.buffers.structural.io.sax.StAXIOBuffer(url)
  res.appendBuffer(io)
  io.streamIn
  
  // Return
  res
  
}

"""
<<<<<<< HEAD:ooxoo-core/src/main/scala/com.idyria.osi.ooxoo.model.out.scala/ScalaProducer.scala
        //-- Add From String factory
        out << s"""
=======
          //-- Add From String factory
          out << s"""
>>>>>>> origin/master:ooxoo-core/src/main/scala/com/idyria/osi/ooxoo/model/out/scala/ScalaProducer.scala
def apply(xml : String) = {
  
  // Instanciate
  var res = new $className
  
  // Set Stax Parser and streamIn
  var io = com.idyria.osi.ooxoo.core.buffers.structural.io.sax.StAXIOBuffer(xml)
  res.appendBuffer(io)
  io.streamIn
  
  // Return
  res
  
}
"""
        }
        try {

          val typesMap = Map(

            classOf[XSDStringBuffer] -> "String",
            classOf[CDataBuffer] -> "String",
            classOf[IntegerBuffer] -> "Int",
            classOf[DoubleBuffer] -> "Double",
            classOf[BooleanBuffer] -> "Boolean")

          var classType = Thread.currentThread.getContextClassLoader().loadClass(element.classType.toString)
          classOf[AbstractDataBuffer[_]].isAssignableFrom(classType) match {

            //-- Add Conversion from base data type
            case true ⇒

              var baseDataType = typesMap.collectFirst {
                case (implClass, baseType) if (implClass.isAssignableFrom(classType)) ⇒ baseType
              } match {

                // Found base type for this Base data type
                case Some(baseType) ⇒

<<<<<<< HEAD:ooxoo-core/src/main/scala/com.idyria.osi.ooxoo.model.out.scala/ScalaProducer.scala
                out << s"implicit def convertFromBaseDataType(data: $baseType) : $className =  { var res = new $className ; res.data = data; res; } "

                // Convert from string does not make sense for String type
                if (baseType != "String")
                  out << s"implicit def convertFromString(data: String) : $className =  { var res = new $className ; res.dataFromString(data); res; } "
=======
                  out << s"implicit def convertFromBaseDataType(data: $baseType) : $objectName =  { var res = new $objectName ; res.data = data; res; } "
>>>>>>> origin/master:ooxoo-core/src/main/scala/com/idyria/osi/ooxoo/model/out/scala/ScalaProducer.scala

                  // Convert from string does not make sense for String type
                  if (baseType != "String")
                    out << s"implicit def convertFromString(data: String) : $objectName =  { var res = new $objectName ; res.dataFromString(data); res; } "

                // Not found, just ouput a warning comment
                case None ⇒

                  out << s"// Object could from a base type conversion as class derives AbstractDataBuffer, but base type mapping is missing in scala producer. Please report by specififying the companion class definition"

              }

            case false ⇒
          }

        } catch {
          case e: Throwable ⇒
        }

        //-- EOF Object
        out.outdent
        out << "}"

      }

      // Output Sub Elements
      //---------------------
      out.finish
      element.elements.foreach(writeElement(_))
    }

    // Start on top elements
    //----------------------------
    model.topElements.foreach { writeElement(_) }

    // Try to copy source File to output if available
    //-----------------
    if (model.sourceFile != null && model.sourceFile.exists && model.parameter("scalaProducer.copyModelToOutput") != None) {

      var fileName = model.name
      if (fileName == null)
        fileName = model.sourceFile.getName

      // Write out package definition
      out.file("./" + targetPackage + "/" + fileName + ".scala")
      out << s"package ${targetPackage}"

      // Add Model Builder import
      out << s"import ${classOf[Model].getPackage.getName}._"

      // Write out File 
      out << model.sourceFile

      out.finish
    }

  }
}
