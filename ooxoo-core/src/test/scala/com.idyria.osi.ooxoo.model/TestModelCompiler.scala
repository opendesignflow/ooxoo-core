package com.idyria.osi.ooxoo.model

import java.io._
 
import com.idyria.osi.ooxoo.core.buffers.datatypes._
import com.idyria.osi.ooxoo.core.buffers.structural.io.sax._
import org.scalatest._

import com.idyria.osi.ooxoo.model.writers._
import com.idyria.osi.ooxoo.model.out.scala._


import scala.language.postfixOps
import scala.collection.JavaConversions._


class TestModelCompiler extends FunSuite with GivenWhenThen {

    test("Compiler test") {

        var modelInfos = ModelCompiler.compile(new File("src/test/scala/com.idyria.osi.ooxoo.model/SOAP.xmodel"))

        assertResult("SOAP")(modelInfos.name)

        assert(modelInfos.producers!=null,"ModelInfos should contain a producers annotation")
        assertResult(1)(modelInfos.producers.value.length)
        assertResult(classOf[ScalaProducer])(modelInfos.producers.value()(0).value)



    }

    test("Producer test") {

        // Prepare
        //------------------
        var scalaProducer = new ScalaProducer
        scalaProducer.targetPackage = "com.idyria.osi.ooxoo.model.test"
        var writer = new StdoutWriter

        ModelCompiler.produce(new File("src/test/scala/com.idyria.osi.ooxoo.model/SOAP.xmodel"),scalaProducer,writer)

    }


}
