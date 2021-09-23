package com.idyria.osi.ooxoo.lib.json.yaml

import com.fasterxml.jackson.core.{JsonParser, JsonToken, JsonTokenId}
import com.fasterxml.jackson.databind.DeserializationContext
import org.eclipse.yasson.internal.serializer.JsonValueDeserializer

import javax.json.{Json, JsonValue}

class JsonValueDeserialiser extends com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer[JsonValue](classOf[JsonValue]) {
  /*override def deserialize(p: JsonParser, ctxt: DeserializationContext): JsonValue = {
    println("IN DESER")
    ctxt.
    javax.json.Json.createValue("OK")
    // new JsonString("OK")
  }*/

  /*override def deserialize(p: JsonParser, ctxt: DeserializationContext): JsonValue = {
    this.getValueType(ctxt)
  }*/

  override def deserialize(p: JsonParser, ctxt: DeserializationContext): JsonValue = {


    val token = p.currentToken()
    //println(s"In Deser: ${token.id()},${p.getIntValue}")

    p.currentTokenId() match {
      case JsonTokenId.ID_NULL =>
        JsonValue.NULL
      case JsonTokenId.ID_TRUE =>
        JsonValue.TRUE
      case JsonTokenId.ID_FALSE =>
        JsonValue.FALSE
      case JsonTokenId.ID_NUMBER_INT =>
        Json.createValue(p.getIntValue)
        Json.createValue(p.getIntValue)
      case JsonTokenId.ID_NUMBER_FLOAT =>
        Json.createValue(p.getDoubleValue)
      case other => Json.createValue(p.getText)
    }

    /*token match {

      case s : JsonToken if (s.id() == JsonToken.VALUE_NULL.id()) =>
        JsonValue.NULL
      case s : JsonToken if (s.isBoolean) =>

        if (s.asString().toBoolean) {
          JsonValue.TRUE
        } else {
          JsonValue.FALSE
        }
        Json.createValue(1)
      case s : JsonToken if (s.id() == JsonToken.VALUE_NUMBER_INT.id()) =>
        Json.createValue(s.asString().toInt)
      case other =>
        Json.createValue("OK")
    }*/
    //this.deserialize(p,ctxt)
  }
}