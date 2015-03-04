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
package com.idyria.osi.ooxoo.model

import scala.language.implicitConversions

import com.idyria.osi.ooxoo.core.buffers.datatypes._
import com.idyria.osi.ooxoo.core.buffers.structural._
import com.idyria.osi.tea.listeners.ListeningSupport

/**
 * This trait contains all the language Wrappers and Conversions used in Model Builder.
 * It is separated to lighten real model management in ModelBuilder from convienience language
 */
trait ModelBuilderLanguage extends ListeningSupport {

  // Typing
  //------------------------------

  /**
   * Types map is used to map type strings to actual Buffer implementation
   */
  var typesMap = Map[String, Class[_ <: Buffer]](
      
    ("string" -> classOf[XSDStringBuffer]), 
    ("htmlstring" -> classOf[HTMLStringBuffer]),
    
    ("cdata" -> classOf[CDataBuffer]),
    ("int" -> classOf[IntegerBuffer]),
    ("integer" -> classOf[IntegerBuffer]),
    ("long" -> classOf[LongBuffer]),
    ("float" -> classOf[FloatBuffer]),
    ("datetime" -> classOf[DateTimeBuffer]),
    ("time" -> classOf[DateTimeBuffer]),
    ("date" -> classOf[DateTimeBuffer]),
    ("boolean" -> classOf[BooleanBuffer]),
    ("bool" -> classOf[BooleanBuffer]),
    ("regexp" -> classOf[RegexpBuffer]),
    ("uri" -> classOf[URIBuffer]),
    ("url" -> classOf[URIBuffer]),

    ("map" -> classOf[StringMapBuffer]))

  def getType(str: String): Class[_ <: Buffer] = {

    this.typesMap.get(str) match {
      case Some(resClass) => resClass
      case None => throw new RuntimeException(s"""ModelBuilderLanguage: Specified type "$str" did not map to a registered Class""")
    }
  }

  //-- Init

  // Element Language
  //-----------------------

  class IsWordElementWrapper(var left: Element) {

    def multiple(typeStr: String) = {

      @->("element.start", left)

      left.classType = getType(typeStr.toLowerCase()).getCanonicalName()
      left.maxOccurs = 10

      @->("element.end", left)

    }

    def multiple(right: => Any): Element = {

      @->("element.start", left)

      left.maxOccurs = 10

      right

      @->("element.end", left)

      left

    }

    def multipleOf(right: Element): IsWordElementWrapper = {

      @->("element.start", left)

      left.maxOccurs = 10

      left.classType = right.name
      //left.instanceOfElement = right

      @->("element.end", left)

      this

    }

    /**
     * The right string is used either:
     *
     * - As Type information if the type has not been set
     * - A Description if the type has already been set.
     *
     * This allows following syntax:
     *
     * "Element" ofType("string") is ("An element contanining certain informations")
     *
     */
    def is(right: String): IsWordElementWrapper = {

      left.classType match {

        // Type
        //--------------
        case null =>

          @->("element.start", left)

          left.classType = getType(right.toLowerCase()).getCanonicalName

          @->("element.end", left)

        // Description
        //-------------------
        case _ =>
          left.description = right
      }

      this

    }

    /**
     * Set type of element based on string
     */
    def ofType(right: String): IsWordElementWrapper = {

      @->("element.start", left)

      //left.classType = getType(right.toLowerCase()).getCanonicalName
      left.imported = true

      right.contains(".") match {
        case true => left.classType = right
        case false => left.classType = getType(right.toLowerCase()).getCanonicalName()
      }

      @->("element.end", left)

      this

    }
    
    /**
     * Set type of element based on string
     */
    def default(right: String): IsWordElementWrapper = {

      //left.im
      left.default = right

      this

    }

    /**
     * Set Enumeration type
     */
    def enum(values: String*): IsWordElementWrapper = {

      @->("element.start", left)

      left.classType = classOf[EnumerationBuffer].getCanonicalName
      values.foreach(left.enumerationValues += _)
      @->("element.end", left)

      this

    }

    def and: IsWordElementWrapper = this

    def and(str: String): IsWordElementWrapper = {

      left.description = str
      this
    }

    def is(right: => Any): Element = { 

      //println("in is definition for Element")

      @->("element.start", left)

      right

      @->("element.end", left)

      left
    }

    def as(right: Class[_ <: Buffer]): Element = {

      @->("element.start", left)

      left.classType = right.getCanonicalName

      @->("element.end", left)

      left
    }

    def as(right: String): Element = {

      @->("element.start", left)

      left.classType = getType(right.toLowerCase()).getCanonicalName

      @->("element.end", left)

      left

    }

    // Documentation
    //------------------

    /**
     * Alias to withDocumentation
     */
    def means(str: String) = withDocumentation(str)

    /**
     * Set documentation on element
     */
    def withDocumentation(str: String): IsWordElementWrapper = {

      left.description = str
      this
    }

  }
  implicit def elementToIsWordWrapping(str: String): IsWordElementWrapper = new IsWordElementWrapper(str)
  implicit def elementToIsWordWrapping(elt: Element): IsWordElementWrapper = new IsWordElementWrapper(elt)

  // Attribute Language
  //------------------------------
  class IsWordAttributeWrapper(var left: Attribute) {

    def attribute(right: => Unit) = is(right)
    def attribute(right: String) = is(right)
    def attribute(right: Class[_ <: Buffer]) = is(right)

    def is(right: => Unit) = {

      @->("attribute.add", left)

      right

    }

    /**
     * Set type of attribute based on string
     */
    def ofType(right: String): IsWordAttributeWrapper = {

      @->("attribute.add", left)

      // Search for type in internal map
      //--------------------------
      left.classType = getType(right.toLowerCase()).getCanonicalName

      this

    }

    /**
     * Set Enumeration type
     */
    def enum(values: String*): IsWordAttributeWrapper = {

      @->("attribute.add", left)

      left.classType = classOf[EnumerationBuffer].getCanonicalName
      values.foreach(left.enumerationValues += _)

      this

    }

    def is(right: Class[_ <: Buffer]) = {

      @->("attribute.add", left)
      left.classType = right.getCanonicalName
    }

    /**
     * Description
     */
    def and(str: String): IsWordAttributeWrapper = {

      left.description = str
      this
    }

    /**
     * Description
     */
    def means(str: String): IsWordAttributeWrapper = {

      left.description = str
      this
    }

    /**
     * Description
     */
    def is(right: String): IsWordAttributeWrapper = {

      left.description = right
      this

    }
  }

  class AttributeWordAttributeWrapper(var left: Attribute) {

    def attribute(right: => Unit) = {

      @->("attribute.add", left)

      right

    }

    /**
     * Set type of attribute based on string
     */
    def attribute(right: String) = {

      @->("attribute.add", left)

      // Search for type in internal map
      //--------------------------
      left.classType = getType(right.toLowerCase()).getCanonicalName

    }

    def attribute(right: Class[_ <: Buffer]) = {

      @->("attribute.add", left)

      left.classType = right.getCanonicalName

    }
  }

  implicit def attributeToIsWordWrapping(attr: Attribute): IsWordAttributeWrapper = new IsWordAttributeWrapper(attr)
  implicit def attributeStrToAttributeWordWrapping(attr: String): AttributeWordAttributeWrapper = new AttributeWordAttributeWrapper(attr)
}
