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
                    attribute("count") is "string"
                    "timestamp" attribute "string"

                    


                }

                "env:Body" is {

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

                    attribute("name")  
                    attribute("count") is "int"
                    "timestamp" attribute "string"

                }

                "env:Body" is {


                    "StringElement" as "string"

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
