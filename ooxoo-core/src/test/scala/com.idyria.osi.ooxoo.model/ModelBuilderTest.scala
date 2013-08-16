package com.idyria.osi.ooxoo.model


import com.idyria.osi.ooxoo.core.buffers.datatypes._
import com.idyria.osi.ooxoo.core.buffers.structural.io.sax._
import org.scalatest._



class ModelBuilderTest extends FunSuite with GivenWhenThen {


    test("Create a simple model") {


        var model = new ModelBuilder {

            name="TestModel"

           

             "env:Envelope" is {

                println("Inside Element building")

                "env:Header" is {

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


}
