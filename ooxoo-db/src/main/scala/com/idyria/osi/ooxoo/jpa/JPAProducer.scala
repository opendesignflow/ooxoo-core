/*
 * #%L
 * Core runtime for OOXOO
 * %%
 * Copyright (C) 2006 - 2017 Open Design Flow
 * %%
 * This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.GeneratedValue
import javax.persistence.Column
import javax.persistence.OneToMany
import javax.persistence.ManyToOne
import javax.persistence.ManyToMany
import javax.persistence.CascadeType

/**
 * This Producer creates scala class implementations for the models
 *
 */
class JPAProducer extends ModelProducer {

  this.outputType = "scala"

  // Name Cleaning
  //-------------------

  val forbiddenKeyWords = List("for", "trait", "class", "package", "var", "val", "def", "private", "final", "match", "case", "object", "type", "lazy", "extends", "with", "wait", "synchronized")

  val ooxooToPlainMap = Map(
    classOf[XSDStringBuffer] -> "String",
    classOf[IntegerBuffer] -> "Int",
    classOf[BooleanBuffer] -> "Boolean",
    classOf[DoubleBuffer] -> "Double")

  def xtypeToJPA(xtype: String) = {
    ooxooToPlainMap.collectFirst {
      case (c, p) if (c.getCanonicalName == xtype) => p
    } match {
      case Some(p) => p
      case None => xtype
    }
  }

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
      case true => res = res + "_"
      case false =>
    }

    // Replace - with _
    res.replace('-', '_')

  }

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
   * Model parameter: jpaProducer.targetPackage
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

    println("INSIDE JPA Producer")

    // Try to find Target Package from model
    //------------------
    this.targetPackage = model.parameter("jpaProducer.targetPackage") match {
      case Some(p) => p
      case None => model.getClass().getPackage().getName() + ".jpa"
    }

    //-- Convert Target  Package to Folder path and create as well
    var targetPackagePath = this.targetPackage.replace(".", "/")

    //-- get Package File Writer
    val packageFileWriter = out.getWriterForFile("./" + targetPackagePath + "/package.scala")

    packageFileWriter << s"package ${this.targetPackage.split('.').dropRight(1).mkString(".")}"
    packageFileWriter << ""
    packageFileWriter << s"""package object ${this.targetPackage.split('.').last} {

    def registerModels = {
      com.idyria.osi.ooxoo.hibernate.OOXOOHibernate.registerPackage(getClass.getPackage)

"""

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

      // Register
      //--------------
      packageFileWriter << s""" com.idyria.osi.ooxoo.hibernate.OOXOOHibernate.registerModel("$targetPackage.$className")  """

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
import ${classOf[Entity].getCanonicalName}
import ${classOf[Id].getCanonicalName}
import ${classOf[GeneratedValue].getCanonicalName}
import ${classOf[Column].getCanonicalName}
import ${classOf[OneToMany].getCanonicalName}
import ${classOf[ManyToOne].getCanonicalName}
import ${classOf[ManyToMany].getCanonicalName}
import ${classOf[CascadeType].getCanonicalName}
import scala.language.implicitConversions
            """ 

      //-- Class Definition
      (namespace, name) match {
        case ("", name) => out << s"""@xelement(name="$name")"""
        case (namespace, name) => out << s"""@xelement(name="$name",ns="$namespace")"""
      }

      // Entity
      out << s"""@Entity"""

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
        case null => element.classType
        case source => canonicalClassName(model, source)

      }

      out << s"""$classOrTrait ${className} extends $classType $traits {
            """

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

        //-- Attributes
        //---------------------------
        out.indent
        element.attributes.foreach { attribute =>

          //--- Annotation
          var resolvedName = model.splitName(attribute.name)
          var localName = resolvedName match {
            case ("", name) =>

              out << s"""@xattribute(name="$name")"""
              name
            case (namespace, name) =>

              out << s"""@xattribute(name="$name",ns="$namespace")"""
              name
          }

          //-- Persistence
          if (attribute.idKey.toBool) {
            out << s"""@Id"""
          }
          if (attribute.generated.toBool) {
            out << s"""@GeneratedValue"""
          }
          out << s"""@Column(name="${resolvedName._2}")"""

          //-- Field
          attribute.maxOccurs match {

            case count if (count > 1) =>

              out << s"""var ${cleanName(makePlural(resolvedName._2))} = XList ({ new ${attribute.classType}},Some(this))
                        """

            // Attribute Needs Subclassing: Enumeration
            //---------------
            case _ if (attribute.classType.toString == classOf[EnumerationBuffer].getCanonicalName()) =>

              out << s"""var ${cleanName(resolvedName._2)} = new ${attribute.classType} {"""

              writeEnumerationValues(localName, attribute, out)

              out << s"""}"""

            // Normal Attribute
            //-------------------
            case _ =>

              out << s"""var __${cleanName(resolvedName._2)} : ${xtypeToJPA(attribute.classType)} = _
                        """

              out << s"""def ${cleanName(resolvedName._2)}_=(v:${xtypeToJPA(attribute.classType)}) = __${cleanName(resolvedName._2)} = v
                        """

              attribute.default match {
                case null => out << s"""def ${cleanName(resolvedName._2)} : ${xtypeToJPA(attribute.classType)} = __${cleanName(resolvedName._2)} 
                        """
                case defaultValue => out << s"""def ${cleanName(resolvedName._2)} : ${xtypeToJPA(attribute.classType)} = __${cleanName(resolvedName._2)} match {case null => __${cleanName(resolvedName._2)} = ${xtypeToJPA(attribute.classType)}.convertFromString("${defaultValue}");__${cleanName(resolvedName._2)} case v => v }
                        """

              }

          }

        }
        out.outdent

        //-- Sub Element
        //---------------------------
        out.indent
        element.elements.foreach { element =>

          // Annotation
          var resolvedName = model.splitName(element.name)
          resolvedName match {
            case ("", name) =>
              out << s"""@xelement(name="$name")"""

            case (namespace, name) =>

              out << s"""@xelement(name="$name",ns="$namespace")"""
          }

          //-- Persistence
          if (element.maxOccurs>1) {
            out << s"""@OneToMany(cascade = Array(CascadeType.ALL), orphanRemoval = true)"""
          } else {
            out << s"""@Column(name="${resolvedName._2}")"""
          }
          

          // ResolvedType if imported of not
          // !! If the Element has a different class name and target object, use the target object!
          //-----------------
          var resolvedType = element.imported.data.booleanValue() match {

            case true if (element.importSource == null) =>
              model.splitName(element.classType.toString)._2

            case true if (element.importSource != null) =>

              s"${canonicalClassName(model, element.importSource)}"

            // Resolved Type is in the targetpackage, and is the canonical name of the subelement
            case _ => s"${canonicalClassName(model, element)}"

          }

          // Convert to JPA
          resolvedType = xtypeToJPA(resolvedType)

          // Element definition
          //---------------
          element.maxOccurs match {

            case count if (count > 1) =>

              out << s"""var ${cleanName(makePlural(resolvedName._2))} :  java.util.List[$resolvedType] = new java.util.LinkedList[$resolvedType]()
                        """

            case _ =>

              // Default value
              var defaultValue = element.default match {
                case null => "null"
                case defaultValue =>

                  s"""${resolvedType}.convertFromString("$defaultValue")"""
              }

              /*out << s"""var __${cleanName(resolvedName._2)} : $resolvedType = $defaultValue
                        """*/
              out << s"""var ${cleanName(resolvedName._2)} : $resolvedType = _
              """

              // Automatic Element creation: Yes per default only if the element has children it self
              // Or The default value was set
              var (getterContent) = element.elements.size match {
                case _ if (element.default != null) =>
                  s"""__${cleanName(resolvedName._2)} match {case null => __${cleanName(resolvedName._2)} = ${resolvedType}.convertFromString("${element.default}");__${cleanName(resolvedName._2)} case v => v }"""

                case all =>
                  s"__${cleanName(resolvedName._2)}"
                /*
                case _ if (element.default != null) =>
                  s"""__${cleanName(resolvedName._2)} match {case null => __${cleanName(resolvedName._2)} = ${resolvedType}.convertFromString("${element.default}");__${cleanName(resolvedName._2)} case v => v }"""

                case _ if (element.traits.find(t => t.toString == classOf[AnyContent].getCanonicalName).isDefined) =>
                  s"__${cleanName(resolvedName._2)} match {case null => __${cleanName(resolvedName._2)} = new $resolvedType();__${cleanName(resolvedName._2)} case v => v }"

                case size if (size > 0 || element.attributes.size > 0) =>
                  s"__${cleanName(resolvedName._2)} match {case null => __${cleanName(resolvedName._2)} = new $resolvedType();__${cleanName(resolvedName._2)} case v => v }"

                case 0 =>
                  s"__${cleanName(resolvedName._2)}"*/
              }

              /*out << s"""def ${cleanName(resolvedName._2)}_=(v:$resolvedType) = __${cleanName(resolvedName._2)} = v
                        """*/
            
              /*out << s"""def ${cleanName(resolvedName._2)} : $resolvedType = __${cleanName(resolvedName._2)} match {case null => __${cleanName(resolvedName._2)} = $resolvedType();__${cleanName(resolvedName._2)} case v => v }
                        """*/

             /*out << s"""def ${cleanName(resolvedName._2)} : $resolvedType = $getterContent
                        """*/

              //-- Add "Option" getter to test presence of element
              out << s"""def ${cleanName(resolvedName._2)}Option : Option[$resolvedType] = ${cleanName(resolvedName._2)} match {case obj if(obj==null) => None; case defined => Some(defined) }
                        """

          }
        }
        out.outdent

        // Extra Elements
        //-----------------------
        /*out.indent
      if(element.requestContainerRelation) {

        out << s"var parentContainer"
      }
      out.outdent*/

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
          out << s"""
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

          //-- Add From String factory
          out << s"""

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
            case true =>

              var baseDataType = typesMap.collectFirst {
                case (implClass, baseType) if (implClass.isAssignableFrom(classType)) => baseType
              } match {

                // Found base type for this Base data type
                case Some(baseType) =>

                  // Convert from string does not make sense for String type
                  //if (baseType != "String")
                  out << s"implicit def convertFromString(data: String) : $objectName =  { var res = new $objectName ; res.data = res.dataFromString(data); res; } "

                // Not found, just ouput a warning comment
                case None =>

                  out << s"// Object could from a base type conversion as class derives AbstractDataBuffer, but base type mapping is missing in scala producer. Please report by specififying the companion class definition"

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

    // Finish package
    packageFileWriter << s"""}
}"""
    packageFileWriter.finish

  }
}
