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
/**
 *
 */
package com.idyria.osi.ooxoo.core.buffers.structural

import scala.annotation.StaticAnnotation
import scala.reflect.runtime.universe._

import java.lang.reflect._


class xattribute_base(var name: String = null,var ns: String = null) {


  //def name() : String = name

  //def ns() : String = namespace

}

object xattribute_base {


  def apply(source: AnyRef) : xattribute_base = {

    // Get Annotation and instanciate base object
    //--------------------
    var annot = source.getClass.getAnnotation(classOf[xattribute])
    if (annot!=null) {

      // Name: annotation content, or Object name
      //---------
      var name = annot.name() match {
        case aname if (aname.length==0) =>

          // Class Name, name can have some funny $annon$...$1 constructs
          // So we need to filter
          var regexp = """(?:.*\$)?([a-zA-Z][\w]+)(?:\$[0-9]+)?\z""".r
          regexp.findFirstMatchIn(source.getClass.getSimpleName) match {
            case Some(m) => m.group(1)
            case None    =>
              //println("Could not match in class name filter")
              source.getClass.getSimpleName
          }

        case aname => aname
      }

      // NS: annotation content
      //---------

      return new xattribute_base(name=name,ns=annot.ns())
    }

    return null


  }

  def apply(source: Field) : xattribute_base = {

    // Get Annotation and instanciate base object
    //--------------------
    var annot = source.getAnnotation(classOf[xattribute])
    if (annot!=null) {

      // Name: annotation content, or Object name
      //---------
      var name = annot.name() match {
        case aname if (aname.length==0) => source.getName
        case aname => aname
      }

      // NS: annotation content
      //---------

      return new xattribute_base(name=name,ns=annot.ns())
    } else {
      throw new RuntimeException("Could not find xattribute annotation on field")
    }

  }



}
