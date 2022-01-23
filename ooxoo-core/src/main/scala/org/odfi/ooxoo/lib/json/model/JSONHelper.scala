package org.odfi.ooxoo.lib.json.model

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.google.gson

import java.io.{File, FileOutputStream, FileReader, InputStream, InputStreamReader, Reader, StringReader}
import java.lang.reflect.{Field, Method, Type}
import java.time.Instant
import java.util
import java.util.{Base64, UUID}
import com.google.gson.annotations.{Expose, SerializedName}
import com.google.gson.reflect.TypeToken
import com.google.gson.{ExclusionStrategy, FieldAttributes, GsonBuilder, JsonArray, JsonDeserializationContext, JsonDeserializer, JsonElement, JsonPrimitive, JsonSerializationContext, JsonSerializer}
import org.odfi.ooxoo.lib.json.yaml.JsonObjectDeserialiser
import org.odfi.ooxoo.lib.json.yaml.{JsonObjectDeserialiser, JsonValueDeserialiser}

import jakarta.json.{Json, JsonObject, JsonString, JsonValue}
import jakarta.json.bind.annotation.JsonbProperty
import jakarta.json.bind.{JsonbBuilder, JsonbConfig}
import jakarta.json.bind.config.{BinaryDataStrategy, PropertyNamingStrategy, PropertyOrderStrategy, PropertyVisibilityStrategy}
import jakarta.json.bind.serializer.{DeserializationContext, JsonbDeserializer, JsonbSerializer, SerializationContext}
import jakarta.json.stream.{JsonGenerator, JsonParser}
import scala.jdk.CollectionConverters.CollectionHasAsScala
import scala.reflect.ClassTag


object JSONHelper {

  lazy val jsonBConfig = new JsonbConfig
  jsonBConfig.withBinaryDataStrategy(BinaryDataStrategy.BASE_64_URL)
  jsonBConfig.withFormatting(true)
  jsonBConfig.withPropertyOrderStrategy(PropertyOrderStrategy.REVERSE)
  jsonBConfig.withPropertyNamingStrategy(PropertyNamingStrategy.IDENTITY)
  jsonBConfig.withPropertyVisibilityStrategy(new OnlyExposeStrategy)
  jsonBConfig.withSerializers(new UUIDJsonBSerDes)
  jsonBConfig.withDeserializers(new UUIDJsonBSerDes)
  lazy val jsonb = JsonbBuilder.create(jsonBConfig)


  def createStdJSONBConfig = {
    val jsonBConfig = new JsonbConfig
    jsonBConfig.withBinaryDataStrategy(BinaryDataStrategy.BASE_64_URL)
    jsonBConfig.withFormatting(true)
    jsonBConfig.withPropertyOrderStrategy(PropertyOrderStrategy.REVERSE)
    jsonBConfig.withPropertyNamingStrategy(PropertyNamingStrategy.IDENTITY)
    jsonBConfig.withPropertyVisibilityStrategy(new OnlyExposeStrategy)
    jsonBConfig.withSerializers(new UUIDJsonBSerDes)
    jsonBConfig.withDeserializers(new UUIDJsonBSerDes)
    jsonBConfig
  }

  def createGSONBuilder = {
    new GsonBuilder()
      .setPrettyPrinting()
      .setExclusionStrategies(new JacksonAndDocumentExclusionStrategy)
      .registerTypeHierarchyAdapter(classOf[Array[Byte]], new JSONHelper.ByteArrayToBase64TypeAdapter())
      .registerTypeHierarchyAdapter(classOf[UUID], new JSONHelper.UUIDTypeAdapter())
      .registerTypeHierarchyAdapter(classOf[JsonValue], new JSONHelper.JsonValueTypeAdapter())

  }

  def createGSON = {
    createGSONBuilder.create()
  }

  def createJSONB = {
    jsonb
  }

  /**
   * Creates a Jackson Mapper that can parse YAML into JSONB compatible object hierarchies
   *
   * @return
   */
  def createJacksonYAMLReader = {
    val mapper = new ObjectMapper(new YAMLFactory)
    mapper.findAndRegisterModules()

    val m = new SimpleModule()
    m.addDeserializer(classOf[JsonValue], new JsonValueDeserialiser)
    m.addDeserializer(classOf[JsonObject],new JsonObjectDeserialiser)
    mapper.registerModule(m)
    mapper
  }

  def fromYAML[T](r: Reader)(implicit tag: ClassTag[T]) : T = {

    val mapper = createJacksonYAMLReader
    mapper.readValue(r,tag.runtimeClass).asInstanceOf[T]
  }

  /**
   * Returns A Populared Copy of this Object!!!
   *
   * @param f
   * @return
   */
  def fromJSONFile[T](f: File)(implicit tag: ClassTag[T]) = {

    jsonb.fromJson(new FileReader(f), tag.runtimeClass).asInstanceOf[T]
  }

  /**
   *
   * @param is
   * @param tag
   * @tparam T
   * @return
   */
  def fromJSONStream[T](is: InputStream)(implicit tag: ClassTag[T]) = {


    jsonb.fromJson(new InputStreamReader(is, "UTF-8"), tag.runtimeClass).asInstanceOf[T]
  }

  /**
   *
   * @param is
   * @param tag
   * @tparam T
   * @return
   */
  def fromJSONStreamArray[T](is: InputStream)(implicit tag: ClassTag[T]): Array[T] = {

    // var istType = new java.util.ArrayList[T]() {}.getClass.getGenericSuperclass

    jsonb.fromJson[Array[T]](new InputStreamReader(is, "UTF-8"), tag.newArray(0).getClass)
  }

  def fromGJSONStreamArray[T](is: InputStream)(implicit tag: ClassTag[T]): Array[T] = {

    var istType = new java.util.ArrayList[T]() {}.getClass.getGenericSuperclass


    //val userListType = new Array[T](0).getClass
    //createGSON.fromJson(new InputStreamReader(is, "UTF-8"),tag.runtimeClass.arrayType())
    //createGSON.fromJson(new InputStreamReader(is, "UTF-8"),classOf[Array[T]]).asInstanceOf[util.ArrayList[T]].asScala.toArray
    var res = createGSON.fromJson[Array[T]](new InputStreamReader(is, "UTF-8"), tag.newArray(0).getClass)
    /*res.foreach {
      parsed =>
        println("Parsed element: "+parsed.getClass.getCanonicalName)
    }*/
    res
  }

  def toJSONFile(obj: AnyRef, f: File) = {

    f.getParentFile.mkdirs()
    val w = new FileOutputStream(f)
    JSONHelper.jsonb.toJson(obj, w)
    w.close()

  }

  def toJSONString(obj: AnyRef) = jsonb.toJson(obj)

  def fromString[T](str: String)(implicit tag: ClassTag[T]) = {
    jsonb.fromJson[T](str, tag.runtimeClass)
  }

  def fromJsonObject[T](obj: JsonObject)(implicit tag: ClassTag[T]) = {
    fromString[T](obj.toString)
  }

  class JacksonAndDocumentExclusionStrategy extends ExclusionStrategy {
    override def shouldSkipField(f: FieldAttributes): Boolean = {

      // All @Expose are translated
      f.getAnnotation(classOf[Expose]) match {
        // NO expose, authorize _id and rev
        case null if (f.getAnnotation(classOf[SerializedName]) != null) =>
          //println("Testing field: "+f.getAnnotation(classOf[SerializedName]).value())
          !f.getAnnotation(classOf[SerializedName]).value().matches("_id|_rev|_attachments|_deleted")
        case other => false
      }

    }

    override def shouldSkipClass(clazz: Class[_]): Boolean = {
      false
    }
  }

  class OnlyExposeStrategy extends PropertyVisibilityStrategy {
    override def isVisible(f: Field): Boolean = {
      // All @Expose are translated
      f.getAnnotation(classOf[Expose]) match {
        // NO expose, authorize _id and rev
        case null if (f.getAnnotation(classOf[SerializedName]) != null) =>
          //println("Testing field: "+f.getAnnotation(classOf[SerializedName]).value())
          !f.getAnnotation(classOf[SerializedName]).value().matches("_id|_rev|_attachments|_deleted")
        case null if (f.getAnnotation(classOf[JsonbProperty]) != null) =>
          true
        case null => false
        case other => true
      }
    }

    override def isVisible(method: Method): Boolean = {
      false
    }
  }

  class ByteArrayToBase64TypeAdapter extends JsonSerializer[Array[Byte]] with JsonDeserializer[Array[Byte]] {

    def deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Array[Byte] = Base64.getUrlDecoder.decode(json.getAsString)

    def serialize(src: Array[Byte], typeOfSrc: Type, context: JsonSerializationContext) = new JsonPrimitive(Base64.getUrlEncoder.encodeToString(src))
  }

  class UUIDTypeAdapter extends JsonSerializer[UUID] with JsonDeserializer[UUID] {

    def deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): UUID = java.util.UUID.fromString(json.getAsString)

    def serialize(src: UUID, typeOfSrc: Type, context: JsonSerializationContext) = new JsonPrimitive(src.toString)
  }

  class UUIDJsonBSerDes extends JsonbDeserializer[UUID] with JsonbSerializer[UUID] {
    override def deserialize(jsonParser: JsonParser, deserializationContext: DeserializationContext, `type`: Type): UUID = {
      UUID.fromString(jsonParser.getString)
    }

    override def serialize(t: UUID, jsonGenerator: JsonGenerator, serializationContext: SerializationContext): Unit = {
      jsonGenerator.write(t.toString)
    }
  }

  class JSONInstantDeserialiser extends JsonbDeserializer[Instant] {

    /* def deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Instant =  {
       val instant = Instant.ofEpochSecond(json.getAsJsonObject.get.getJsonNumber("seconds").longValue(), tsObj.getJsonNumber("nanos").longValue())

     }*/
    override def deserialize(jsonParser: JsonParser, deserializationContext: DeserializationContext, `type`: Type): Instant = {

      val obj = jsonParser.getObject
      val instant = Instant.ofEpochSecond(
        obj.getJsonNumber("seconds").longValue(),
        obj.getJsonNumber("nanos").longValue()
      )
      instant


    }
  }

  class JsonValueTypeAdapter extends JsonSerializer[JsonValue] with JsonDeserializer[JsonValue] {

    def deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): JsonValue = {
      json match {
        case json if (json.isJsonPrimitive) =>
          json.getAsJsonPrimitive match {
            case str if (str.isString) => Json.createValue(str.getAsString)
            case b if (b.isBoolean) => if (b.getAsBoolean) JsonValue.TRUE else JsonValue.FALSE
            case double if (double.isNumber && double.getAsString.contains(".")) => Json.createValue(double.getAsDouble)
            case long if (long.isNumber) => Json.createValue(long.getAsLong)
            //case long if (str.isString) => Json.createValue(str.getAsString)
            // case double if (str.isString) => Json.createValue(str.getAsString)
            case other => sys.error("Primitive not supported: " + other)
          }
        case json if (json.isJsonObject) => Json.createReader(new StringReader(json.toString)).readObject()
        case json if (json.isJsonArray && json.getAsJsonArray.size() > 0) => Json.createReader(new StringReader(json.toString)).readArray()
        case other => null
      }
    }

    def serialize(src: JsonValue, typeOfSrc: Type, context: JsonSerializationContext) = {
      src.getValueType match {
        case JsonValue.ValueType.STRING => new JsonPrimitive(src.asInstanceOf[JsonString].getString)
        case JsonValue.ValueType.NUMBER if (src.toString.contains(".")) => new JsonPrimitive(src.toString.toDouble)
        case JsonValue.ValueType.NUMBER => new JsonPrimitive(src.toString.toLong)
        case JsonValue.ValueType.TRUE => new JsonPrimitive(true)
        case JsonValue.ValueType.FALSE => new JsonPrimitive(false)
        case JsonValue.ValueType.ARRAY =>
          //println("InARR")
          val arr = new JsonArray()
          src.asJsonArray().asScala.foreach {
            elt =>
              arr.add(context.serialize(elt))
          }
          arr
        case JsonValue.ValueType.OBJECT =>
          // println("InObj")
          val obj = new gson.JsonObject()
          val srcJson = src.asJsonObject()
          srcJson.keySet().forEach {
            key =>
              obj.add(key, context.serialize(srcJson.get(key)))
          }
          // obj.addProperty("hello","World")
          obj
        //createGSON.toJsonTree(src.asJsonObject().toString)
        //context.serialize(src.asJsonObject().toString)
        case other => sys.error("Not supported Object or Array")
        // case JsonValue.ValueType.OBJECT =>  createGSON.newJsonReader(new StringReader(src.toString))

      }

    }
  }

}
