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

package org.odfi.ooxoo.model

import org.odfi.ooxoo.core.buffers.datatypes.XSDStringBuffer
import org.odfi.ooxoo.core.buffers.structural.{ElementBuffer, XList, xelement}

import java.io.File 

/**
    Generic trait to represent an XML Model

*/
trait Model {

    var defaultElementBufferClass : Class[ _<:ElementBuffer] = classOf[ElementBuffer]
  
    /**
        Global namespace of model
    */
    var ns : XSDStringBuffer = null

    /**
        Model name
    */  
    var name : XSDStringBuffer = null

    /**
        The source file of this model, is possible to determine at runtime
    */
    var sourceFile : File = null


    //-- Top Elements list
    @xelement(name="Element")
    var topElements = XList{ du => new Element(du.element.name,Model.this)}

    // Parameters
    //-------------------------
    var parameters = Map[String,String]()

    /**
        Register a parameter value
    */
    def parameter( parameter : Tuple2[String,String]) : Unit = {
        parameters = parameters + parameter
    }

    /**
        Return an option for the named parameter
    */
    def parameter(parameterName: String) : Option[String] = this.parameters.get(parameterName)

    // Namespace maps
    //------------------------
    var namespaces = Map[String,String]()

    def namespace( prefixToNs : Tuple2[String,String]) = {
        namespaces = namespaces + prefixToNs
    }

    /**
        Return the namespace for the name, or None if the name does not reference any namespace
    */
    def namespace( name : String) : Option[String] = {

        var splitted = name.split(":")
        if (splitted.size > 1 ) {
            this.namespaces.get(splitted(0))
        } else {
            this.namespaces.get("")
        }

    }

    def splitName( fullName : String) : Tuple2[String,String] = {

        var namespace = ""
        var name = fullName
        this.namespace(fullName) match {
            case Some(foundNs) => 
              namespace = foundNs;
              name = fullName.split(":") match {
                case arr if (arr.length==1) => fullName
                case arr => arr(1)
              }
            case None =>
        }
        (namespace,name)
    }

    // Produce
    //--------------------
    def produce(producer: ModelProducer,out: Writer) = {
        producer.produce(this,out)
    }

}

object Model {


}
