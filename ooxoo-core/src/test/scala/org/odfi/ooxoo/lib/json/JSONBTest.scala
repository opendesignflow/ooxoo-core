package org.odfi.ooxoo.lib.json

import com.google.gson.annotations.{Expose, SerializedName}
import org.odfi.ooxoo.lib.json.model.JSONHelper
import org.scalatest.funsuite.AnyFunSuite

import jakarta.json.bind.annotation.JsonbProperty
import scala.beans.BeanProperty



class JEntity {

  @Expose
  @JsonbProperty("a")
  @SerializedName("a")
  var a: String = _

  @Expose
  @JsonbProperty("sub")
  @SerializedName("sub")
  @BeanProperty
  var sub  = Array[JSubEntity]()



}

class JSubEntity {
  @JsonbProperty("c")
  @SerializedName("c")
  var c: String = _

  @JsonbProperty("d")
  @SerializedName("d")
  var d: String = _
}

class JSONBTest extends AnyFunSuite {

  test("Deserialize") {

    val inputString =
      """
 {
      "a" : "test1",
      "sub" : [{
       "c":"test2",
      "d": "test3"

      }]
        }
        """

    val topClass = classOf[JEntity]
    topClass.getDeclaredFields.foreach {
      f =>
        println(s"Field: ${f.getName} -> ${f.getType} ->${f.getGenericType} -> ${f.getAnnotatedType.getType}")
    }

    val result = JSONHelper.createJSONB.fromJson[JEntity](inputString, classOf[JEntity])

    //val result = JSONHelper.createGSON.fromJson[JEntity](inputString, classOf[JEntity])

    // Assert
    //-----------
    assert(result.a == "test1")
    assert(result.sub.length == 1)
    assert(result.sub(0).c == "test2")

    result.sub = Array()

  }

}
