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
package com.idyria.osi.ooxoo.model.out.scala

import java.util.UUID
import com.idyria.osi.ooxoo.core.buffers.datatypes._
import com.idyria.osi.ooxoo.core.buffers.datatypes.id.UUIDBuffer
import com.idyria.osi.ooxoo.core.buffers.structural._
import com.idyria.osi.ooxoo.model._
import javax.json.bind.annotation.{JsonbProperty, JsonbTransient}
import org.atteo.evo.inflector.English

import scala.beans.{BeanProperty, BooleanBeanProperty}

/**
 * This Producer creates scala class implementations for the models
 *
 */
class JSONBProducer extends ModelProducer {

  this.outputType = "scala"

  // Name Cleaning
  //-------------------

  val forbiddenKeyWords = List("for", "trait", "class", "package", "var", "val", "def", "private", "final", "match", "case", "object", "type", "lazy", "extends", "with", "wait", "synchronized")

  /**
   * Returns a scala friendly name from base name, without reserved keywords etc...
   */
  def cleanName(name: String, cleanForbidden: Boolean = true): String = {

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
    if (cleanForbidden) {
      forbiddenKeyWords.contains(res) match {
        case true => res =
          //res + "_"
          s"""`$res`"""
        case false =>
      }
    }


    // Replace - with _
    res.replace('-', '_')

  }

  def attributeFieldName(n: String) = cleanName(n)

  def elementFieldName(n: String) = cleanName(n + "")

  def elementFieldSetterName(n: String) = cleanName(n + "_")

  def elementFieldGetterName(n: String) = cleanName(n)


  /**
   * Makes the name plural
   */
  def makePlural(name: String): String = {

    name match {
      case name if (name.length() == 1) => name
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
      case name if (element.traitSeparateFromObject != null) =>
        //println(s"Element has a separate class definitnion: " + element.traitSeparateFromObject)
        element.traitSeparateFromObject
      case null => element.name
      case _ => element.className
    }

    val r = canonicalClassName(model, name, element)
    //println("Resolved: "+r)
    r
  }

  /**
   * Creates a hierarchical CanonicalName for a class
   */
  def canonicalClassName(model: Model, basename: String, element: Element): String = {

    // Name: If canonical, return as is, otherwise merge with parent
    //------------
    val finalName = basename.contains(".") match {
      case true =>
        basename
      case false if (element.staticClassName == true) =>
        basename
      case false =>

        var name = basename

        model.splitName(name) match {
          case (sNs, sName) => name = sName
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

        s"$targetPackage.$parentNames$className"
    }

    // Clean
    finalName.replace('-', '_')

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

    println("Producing JSONB Interface...")

    // Try to find Target Package from model
    //------------------
    this.targetPackage = model.parameter("scalaProducer.targetPackage") match {
      case Some(p) => p
      case None => model.getClass().getPackage().getName()
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
        case (sNs, sName) =>
          namespace = sNs;
          name = sName
      }

      // Class Name: Use Canonical Function, with our class name as base
      //-----------------
      var className = canonicalClassName(model, element.className, element).split("\\.").last

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
      out <<
        s"""package $targetPackage
            """

      //-- Import
      out <<
        s"""
import ${classOf[JsonbProperty].getCanonicalName}
import ${classOf[JsonbTransient].getCanonicalName}
import ${classOf[BeanProperty].getCanonicalName}
import ${classOf[BooleanBeanProperty].getCanonicalName}
import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose
import scala.language.implicitConversions
import scala.jdk.CollectionConverters._
            """

      //-- Class Definition
      /* (namespace, name) match {
           case ("", name)        => out << s"""@xelement(name="$name")"""
           case (namespace, name) => out << s"""@xelement(name="$name",ns="$namespace")"""
       }*/

      if (element.requestContainerRelation && element.parent != null) {

        element.traits += classOf[VerticalBufferWithParentReference[_]].getCanonicalName + s"[${canonicalClassName(model, element.parent.className, element.parent).stripSuffix("Trait")}]"
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
        case null => "Object" //element.classType
        case source => canonicalClassName(model, source)

      }

      out <<
        s"""$classOrTrait ${className} extends $classType $traits {
            """

      //-- Attributes
      //---------------------------
      out.indent
      element.attributes.foreach { attribute =>

        //--- Annotation
        var resolvedName = model.splitName(attribute.name)
        var localName = resolvedName match {
          case ("", name) =>

            out << s"""@JsonbProperty("${name}")"""
            out << s"""@SerializedName("${name}")"""
            out << s"""@Expose"""
            out << s"""@BeanProperty"""

            name
          case (namespace, name) =>

            out << s"""@JsonbProperty("${name}")"""
            out << s"""@SerializedName("${name}")"""
            out << s"""@Expose"""
            out << s"""@BeanProperty"""
            name
        }

        //-- Type
        val attributeType = JSONBProducer.typeMapping(attribute.classType)

        //-- Field
        attribute.maxOccurs match {

          case count if (count > 1) =>

            out <<
              s"""var ${attributeFieldName(makePlural(resolvedName._2))} = new java.util.ArrayList[$attributeType]()
                        """

          // Attribute Needs Subclassing: Enumeration
          //---------------
          case _ if (attribute.classType.toString == classOf[EnumerationBuffer].getCanonicalName()) =>

            out << s"""var ${attributeFieldName(resolvedName._2)} = new ${attributeType} {"""

            writeEnumerationValues(localName, attribute, out)

            out << s"""}"""

          // Normal Attribute
          //-------------------
          case _ =>

            var defaultValue = attribute.default match {
              case null if (JSONBProducer.typeIsNative(attributeType)) => "_"
              case null => "null"
              case str if (str.toString == "_build_") => s"${attributeType}.build"
              case str if (str.toString == "_instance_") => s"${attributeType}()"
              case defaultValue =>

                s"""$defaultValue"""
              //s"""${resolvedType}.convertFromString("$defaultValue")"""
            }

            out <<
              s"""var ${attributeFieldName(resolvedName._2)} : ${attributeType} = $defaultValue
                        """

        }

      }
      out.outdent

      // Sub Elements in Hierarchy case are inheriting present type, so don't write them as structural children
      if (element.isHierarchyParent == false) {

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

        //-- Sub Element
        //---------------------------
        out.indent
        element.elements.foreach { element =>

          // ResolvedType if imported of not
          // !! If the Element has a different class name and target object, use the target object!
          //-----------------
          var resolvedType = element.imported.data.booleanValue() match {

            case true if (element.importSource == null) =>
              JSONBProducer.typeMapping(model.splitName(element.classType.toString)._2)

            case true if (element.importSource != null) =>

              JSONBProducer.typeMapping(s"${canonicalClassName(model, element.importSource)}")

            // Resolved Type is in the targetpackage, and is the canonical name of the subelement
            case _ =>
              JSONBProducer.typeMapping(s"${canonicalClassName(model, element)}")

          }

          // Annotation
          //----------
          var resolvedName = model.splitName(element.name)
          resolvedName match {
            case ("", name) =>

              out << s"""@Expose"""
              if (resolvedType.endsWith("Boolean")) {
                out << s"""@BooleanBeanProperty"""
              } else {
                out << s"""@BeanProperty"""
              }


            case (namespace, name) =>


              out << s"""@Expose"""
              if (resolvedType.endsWith("Boolean")) {
                out << s"""@BooleanBeanProperty"""
              } else {
                out << s"""@BeanProperty"""
              }
          }


          // Element definition
          //---------------
          element.maxOccurs match {

            case count if (count > 1) =>

              val fieldNameNotCleaned = makePlural(resolvedName._2)
              val fieldName = cleanName(makePlural(resolvedName._2))
              val fieldNameUpperFirst = fieldNameNotCleaned.take(1).toUpperCase + fieldNameNotCleaned.drop(1).mkString
              val fieldNameSingularUpperFirst = resolvedName._2.take(1).toUpperCase + resolvedName._2.drop(1).mkString
              out << s"""@JsonbProperty("${makePlural(resolvedName._2)}")"""
              out << s"""@SerializedName("${makePlural(resolvedName._2)}")"""
              out <<
                s"""var ${fieldName} = new java.util.ArrayList[$resolvedType]()
                        """
              if (!element.nativeType) {
                out <<
                  s"""def add${fieldNameSingularUpperFirst} = {val r = new $resolvedType; ${fieldName}.add(r);r}
                      """
              }

              out <<
                s"""def ${cleanName(makePlural(resolvedName._2), false)}AsScala = ${fieldName}.asScala.toList
                      """

            case _ =>

              val fieldName = elementFieldName((resolvedName._2))
              var realFieldName = cleanName((resolvedName._2))

              // Default value
              var defaultValue = element.default match {
                case null if (JSONBProducer.typeIsNative(resolvedType)) => "_"
                case null => "null"
                case str if (str.toString == "_build_") => s"${resolvedType}.build"
                case str if (str.toString == "_instance_") => s"${resolvedType}()"
                case defaultValue =>
                  s"""$defaultValue"""
              }


              out << s"""@JsonbProperty("${resolvedName._2}")"""
              out << s"""@SerializedName("${resolvedName._2}")"""
              out <<
                s"""var ${fieldName} : $resolvedType = $defaultValue
                """

              // Automatic Element creation: Yes per default only if the element has children it self
              // Or The default value was set
              var (getterContent) = element.elements.size match {
                case _ if (element.default != null) =>
                  s"""$fieldName"""

                case all =>
                  resolvedType match {
                    case native if (JSONBProducer.typeIsNative(resolvedType)) =>
                      s"$fieldName"
                    case other =>
                      val constructor = JSONBProducer.constructorMapping(resolvedType)
                      s"$fieldName match {case null => { $fieldName = $constructor; $fieldName } case v => v }"
                  }


              }

              // Add Utilities onlu for non native types
              //----------------
              if (!JSONBProducer.typeIsNative(resolvedType)) {
                //-- Add Only auto creating Getter
                out << s"""@JsonbTransient"""
                out <<
                  s"""def ${cleanName(resolvedName._2, false)}OrCreate : $resolvedType =  $getterContent
                        """

                //-- Add "Option" getter to test presence of element
                resolvedType match {
                  case native if (JSONBProducer.typeIsNative(resolvedType)) =>

                    out << s"""@JsonbTransient"""
                    out <<
                      s"""def ${cleanName(resolvedName._2, false)}Option : Option[$resolvedType] = Some($fieldName)
                        """
                  case other =>

                    out << s"""@JsonbTransient"""
                    out <<
                      s"""def ${cleanName(resolvedName._2, false)}Option : Option[$resolvedType] = $fieldName match { case null => None; case defined => Some(defined) }
                        """

                }
              }


          }
        }
        out.outdent

        // Extra Elements
        //-----------------------


      }
      //-- End of class
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
          /*out <<
            s"""
def apply(url : java.net.URL) = {

  
}

"""*/

          //-- Add From String factory
          /*out <<
            s"""

def apply(xml : String) = {
  

  
}
"""*/
        }
        try {

          val typesMap = Map(

            classOf[String] -> "String",
            classOf[String] -> "String",
            classOf[Int] -> "Int",
            classOf[Double] -> "Double",
            classOf[Boolean] -> "Boolean"
          )

          //-- Add Conversion from base data type
          var classType = Thread.currentThread.getContextClassLoader().loadClass(element.classType.toString)
          classOf[AbstractDataBuffer[_]].isAssignableFrom(classType) match {


            case true =>

              var baseDataType = typesMap.collectFirst {
                case (implClass, baseType) if (implClass.isAssignableFrom(classType)) => baseType
              } match {

                // Found base type for this Base data type
                case Some(baseType) =>

                // Convert from string does not make sense for String type
                //if (baseType != "String")
                // out << s"implicit def convertFromString(data: String) : $objectName =  { var res = new $objectName ; res.data = res.dataFromString(data); res; } "

                // Not found, just ouput a warning comment
                case None =>

                // out << s"// Object could from a base type conversion as class derives AbstractDataBuffer, but base type mapping is missing in scala producer. Please report by specififying the companion class definition"

              }

            case false =>
          }

        } catch {
          case e: Throwable =>
            e.printStackTrace()
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
    try {
      model.topElements.foreach { elt =>
        writeElement(elt)
      }
    } catch {
      case e: Throwable => e.printStackTrace()
    }

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

object JSONBProducer {

  val typesMap = Map(
    classOf[XSDStringBuffer].getCanonicalName -> "String",
    classOf[IntegerBuffer].getCanonicalName -> "Int",
    classOf[LongBuffer].getCanonicalName -> "Long",
    classOf[DoubleBuffer].getCanonicalName -> "Double",
    classOf[BooleanBuffer].getCanonicalName -> "Boolean",
    classOf[BinaryBuffer].getCanonicalName -> "Array[Byte]",
    classOf[DateTimeBuffer].getCanonicalName -> "java.time.Instant",
    classOf[JSONBuffer].getCanonicalName -> "javax.json.JsonObject",
    classOf[JSONVBuffer].getCanonicalName -> "javax.json.JsonValue",
    classOf[UUIDBuffer].getCanonicalName -> classOf[UUID].getCanonicalName
  )

  val nativeTypes = List("Double", "Long", "Integer", "Float", "Boolean","Int","javax.json.JsonObject", "javax.json.JsonValue")

  def typeMapping(input: String) = {

    typesMap.get(input) match {
      case Some(mappedType) =>
        mappedType
      case None =>
        input
    }

  }

  def typeIsNative(t: String) = {

    //println("Checking type: "+t+" -> "+nativeTypes.find( test => test == t).isDefined)
    nativeTypes.find( test => test == t).isDefined
  }

  def constructorMapping(input: String) = {
    input match {
      case arr if (arr.contains("Array[")) =>
        s"new ${input}(0)"
      case instant if (instant == "java.time.Instant") =>
        s"java.time.Instant.now()"
      case other =>
        s"new ${input}()"
    }
  }
}