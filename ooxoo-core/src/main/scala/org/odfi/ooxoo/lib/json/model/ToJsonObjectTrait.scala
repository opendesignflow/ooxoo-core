package org.odfi.ooxoo.lib.json.model

import jakarta.json.Json

import java.io.StringReader

trait ToJsonObjectTrait {


  override def toString = {
    JSONHelper.toJSONString(this)
  }

  def toJsonObject = {
    Json.createReader(new StringReader(toString)).readObject()
  }
}
