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
package com.idyria.osi.ooxoo.core.buffers.structural

import scala.annotation.Annotation
import scala.annotation.StaticAnnotation
import scala.reflect.runtime.universe._
import com.idyria.osi.ooxoo.core.utils.ScalaReflectUtils

import java.lang.reflect._

class xelement_base(var name: String = null,var ns: String = "")  {


  //def name() : String = name

  //def ns() : String = namespace

}

object xelement_base {

    /**
      Try to find annotation on class hiearchy
    */
   def apply(baseClass: Class[_]) : xelement_base = {

      //println("Searching for xelement on baseclass: "+baseClass.getSimpleName)

      //baseClass.getClasses.foreach {
     //     cl => println("-> "+cl)
     // }

     // println("Superclass: "+baseClass.getSuperclass())
     //println(s"CL: "+baseClass.getSimpleName);
      var firstName = baseClass.getSimpleName.replaceAll("\\$.*", "")
      var cl : Class[_] = baseClass
      do {

          // Get Annotation and instanciate base object
          //--------------------
          var annot = cl.getAnnotation(classOf[xelement])
          //println(s"Annot: ${annot.name()}")
          if (annot!=null) {

            // Name: annotation content, or Object name
            //---------
            var name = annot.name() match {
              
              case "" => firstName
               case null => firstName
              case aname if (aname.trim().length==0) =>

                //println(s"Found xelement on ${cl.getSimpleName}, with name: '${aname.trim().length}'")
                
                // Class Name, name can have some funny $annon$...$1 constructs
                // So we need to filter
                var regexp = """(?:.*\$)?([a-zA-Z][\w]+)(?:\$[0-9]+)?\z""".r
                regexp.findFirstMatchIn(cl.getSimpleName) match {
                  case Some(m) => m.group(1)
                  
                  // Ifn o Name, always return first name
                  case None    =>
                    //println("Could not match in class name filter")
                    //cl.getSimpleName
                    firstName
                }

             
              case aname => aname
            }

            // NS: annotation content
            //---------

            return new xelement_base(name=name,ns=annot.ns())
          }

          // Next superclass ?
          cl = cl.getSuperclass

      } while ( cl != null)
    
    

    return null

  }

  def apply(source: AnyRef) : xelement_base = this(source.getClass)

   def apply(source: Field) : xelement_base = {

    // Get Annotation and instanciate base object
    //--------------------
    var annot = source.getAnnotation(classOf[xelement])
    if (annot!=null) {

      // Name: annotation content, or Object name
      //---------
      var name = annot.name() match {
        case aname if (aname.length==0) => source.getName
        case aname => aname
      }

      // NS: annotation content
      //---------

      return new xelement_base(name=name,ns=annot.ns())
    }

    return null

  }


}
