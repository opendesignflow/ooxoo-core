package org.odfi.ooxoo.lib.yaml

import com.google.gson.annotations.{Expose, SerializedName}
import jakarta.json.{JsonObject, JsonValue}
import jakarta.json.bind.annotation.JsonbProperty
import org.odfi.ooxoo.lib.json.model.JSONHelper

import java.io.StringReader
import scala.beans.BeanProperty

class TestClassForYAML {

  @JsonbProperty("name")
  @SerializedName("name")
  @Expose
  @BeanProperty
  var name: String = _

  @JsonbProperty("id")
  @SerializedName("id")
  @Expose
  @BeanProperty
  var id: String = _

  @JsonbProperty("value")
  @SerializedName("value")
  @Expose
  @BeanProperty
  var value: JsonValue = _


  @JsonbProperty("json")
  @SerializedName("json")
  @Expose
  @BeanProperty
  var json: JsonObject = _

}

object TestYAMLJsonParser extends App {

  val testPackage =
    """|name: TestName
       |id: test
       |value: 5
      """.stripMargin

  val testPackageWithSTR =
    """|name: TestName
       |id: test
       |value: http://google.com
      """.stripMargin

  val testPackageShort =
    """|name: TestName
       |id: test
      """.stripMargin


  // Parse Definitions without embedded Json
  //-------------------------------
  val parsed = JSONHelper.fromYAML[TestClassForYAML](new StringReader(testPackageShort))
  assert(parsed.name=="TestName")

  val parsedLong = JSONHelper.fromYAML[TestClassForYAML](new StringReader(testPackage))
  assert(parsedLong.name=="TestName")
  assert(parsedLong.value.getValueType == JsonValue.ValueType.NUMBER,s"Wrong value type, found: ${parsedLong.value.getValueType}")
  assert(parsedLong.value.toString.toInt == 5,s"Wrong value type, found: ${parsedLong.value.getValueType}")

  val parsedString = JSONHelper.fromYAML[TestClassForYAML](new StringReader(testPackageWithSTR))
  println("HTTP: "+parsedString.value.toString)


  // Parse with subprojects
  //----------------------------
  val testPackageWithJson =
  """|name: TestName
     |json:
     |  name: subname
     |id: test
      """.stripMargin

  val parsedWithJson = JSONHelper.fromYAML[TestClassForYAML](new StringReader(testPackageWithJson))
  assert(parsedWithJson.id=="test")
  assert(parsedWithJson.json!=null)
}
