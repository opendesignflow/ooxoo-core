package com.idyria.osi.ooxoo.lib.json

import com.idyria.osi.ooxoo.lib.json.model.JSONHelper
import javax.json.bind.annotation.JsonbProperty

object TryJsonHelper extends App {

  println("Try JSON Helper...")

  class TestModel {

    @JsonbProperty
    var id: String = _

  }

  val res = JSONHelper.createJSONB.fromJson(
    """{
      |"id": "test"
      |}""".stripMargin, classOf[TestModel])

  println("Res: " + res)

}
