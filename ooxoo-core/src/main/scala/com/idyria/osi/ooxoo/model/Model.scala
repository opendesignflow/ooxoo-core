package com.idyria.osi.ooxoo.model

import com.idyria.osi.ooxoo.core.buffers.datatypes._
import com.idyria.osi.ooxoo.core.buffers.structural._

import java.io.File 

/**
    Generic trait to represent an XML Model

*/
trait Model {

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
    var topElements = XList{ du => new Element(du.element.name)}

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
