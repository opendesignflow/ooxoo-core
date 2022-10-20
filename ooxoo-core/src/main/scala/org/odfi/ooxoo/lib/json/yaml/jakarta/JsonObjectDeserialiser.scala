package org.odfi.ooxoo.lib.json.yaml.jakarta

import com.fasterxml.jackson.core.{JsonParser, TreeNode}
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import jakarta.json.{Json, JsonObject}

import java.io.StringReader


class JsonObjectDeserialiser extends StdDeserializer[JsonObject](classOf[JsonObject]) {

  override def deserialize(p: JsonParser, ctxt: DeserializationContext): JsonObject = {


    val token = p.currentToken()
   // println(s"In Deser: ${token.id()},${token.asString()}")
    val t =  p.readValueAsTree[TreeNode]()
    //val nxt = p.nextToken()

   // println("Read: "+t)
   // print("Next: "+p.nextToken().id())
    Json.createReader(new StringReader(t.toString)).readObject()
   /* p.skipChildren()
    val cp= p.nextToken()

    token.id() match {
      case JsonTokenId.ID_START_OBJECT =>

       val t =  p.readValueAsTree[TreeNode]()
        println("Read: "+t)
      case JsonTokenId.ID_END_OBJECT =>
        val strw = new StringWriter()
        p.releaseBuffered(strw)
        strw.flush()
        println("End: "+strw.getBuffer.toString)
    }*/

   /* p.currentTokenId() match {
      case JsonTokenId.ID_EMBEDDED_OBJECT =>
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
    }*/
   // null

  }
}
