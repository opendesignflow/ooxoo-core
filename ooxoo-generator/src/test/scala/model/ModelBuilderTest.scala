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
package model

import org.odfi.ooxoo.core.buffers.datatypes.*
import org.odfi.ooxoo.core.buffers.structural.io.sax.*
import org.odfi.ooxoo.model.ModelBuilder
import org.odfi.ooxoo.model.out.scala.*
import org.odfi.ooxoo.model.writers.*
import org.scalatest.*
import org.scalatest.funsuite.AnyFunSuite

import scala.language.postfixOps


trait TestTrait {

}

class ModelBuilderTest extends AnyFunSuite with GivenWhenThen {


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
                    
                    "TestEnum" valueEnum("A","B","C","D")
                    
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

                    withTrait("org.odfi.ooxoo.model.TestTrait")

                    withDescription {
                        "SOAP Protocol Header"
                    }

                    attribute("name")

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
        scalaProducer.targetPackage = "org.odfi.ooxoo.model.test"

        And("A Stdout writer")
        var writer = new StdoutWriter

        Then("Produce should work")
        scalaProducer.produce(model,writer)

    }

}
