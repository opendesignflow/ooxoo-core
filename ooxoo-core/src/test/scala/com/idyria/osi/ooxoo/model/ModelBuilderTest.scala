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


import com.idyria.osi.ooxoo.core.buffers.datatypes._
import com.idyria.osi.ooxoo.core.buffers.structural.io.sax._
import org.scalatest._

import com.idyria.osi.ooxoo.model.writers._
import com.idyria.osi.ooxoo.model.out.scala._

import scala.language.postfixOps


trait TestTrait {

}

class ModelBuilderTest extends FunSuite with GivenWhenThen {


    test("Create a simple model") {


        var model = new ModelBuilder {

            name="TestModel"

           

             "env:Envelope" is {

                println("Inside Element building")

                "env:Header" is {

                    attribute("name") 

                    -@("name2")

                    attribute("count") is "string"
                    "timestamp" attribute "string"

                    


                }

                "env:Body" is {

                  "TestElement" is {
                    
                    "TestEnum" enum("A","B","C","D")
                    
                  }
                  
                }

               /* "env:Header" {

                }

                "env:Body" {
 
                }*/

            }

        }

        println(s"Resulting Model: ${model.toXML}")

    }


    test("Create Scala test") {

        Given("A Basic Model") 

        var model = new ModelBuilder {

            name="SOAPModel"
            
            namespace("env" -> "superNamespace")

            "env:Envelope" is {

               
                println("Inside Element building")

                "env:Header" is {

                    withTrait("com.idyria.osi.ooxoo.model.TestTrait")

                    withDescription {
                        "SOAP Protocol Header"
                    }

                    attribute("name") {
                        
                            "name description"
                    }

                    attribute("count") is "int"
                    "timestamp" attribute "string"

                }

                "env:Body" is {

  
                    "StringElement" is "string"  and "Just a string element"

                    //"MultipleStringElement" as multiple ("string")

                    "MultipleStringElement"  multiple "string"

                    "MultipleStringElement2" multiple "string"

                    //multiple "MultipleStringElement" as "string"

                    //"MultipleIntElement" as multiple("int")

                    "MultipleSubElement" multiple {

                       // multiple()
                    }
                }

            }

        }

        And("A scala producer")
        var scalaProducer = new ScalaProducer
        scalaProducer.targetPackage = "com.idyria.osi.ooxoo.model.test"

        And("A Stdout writer")
        var writer = new StdoutWriter

        Then("Produce should work")
        scalaProducer.produce(model,writer)

    }

}
