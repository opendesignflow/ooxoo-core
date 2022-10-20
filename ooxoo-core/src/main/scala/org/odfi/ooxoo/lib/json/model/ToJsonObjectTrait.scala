package org.odfi.ooxoo.lib.json.model



import java.io.StringReader
import javax.json.bind.Jsonb


trait ToJsonObjectTrait {


  override def toString = {
    JSONHelper.toJSONString(this)
  }

  def toJsonObject = {
    jakarta.json.Json.createReader(new StringReader(toString)).readObject()
  }

  def toLegacyJsonObject(jsonb : Jsonb = JSONHelper.jsonb) = {
    javax.json.Json.createReader(new StringReader(jsonb.toJson(this))).readObject()
  }
}
