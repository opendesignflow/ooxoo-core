package org.odfi.ooxoo.lib.json.model



import java.io.StringReader


trait ToJsonObjectTrait {


  override def toString = {
    JSONHelper.toJSONString(this)
  }

  def toJsonObject = {
    jakarta.json.Json.createReader(new StringReader(toString)).readObject()
  }

  def toLegacyJsonObject = {
    javax.json.Json.createReader(new StringReader(toString)).readObject()
  }
}
