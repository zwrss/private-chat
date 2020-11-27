package com.zwrss.privatechat.message.event

import play.api.libs.json.Format
import play.api.libs.json.JsError
import play.api.libs.json.JsNull
import play.api.libs.json.JsObject
import play.api.libs.json.JsResult
import play.api.libs.json.JsString
import play.api.libs.json.JsValue

import scala.reflect.ClassTag
import scala.reflect.classTag

trait ChatEvent

abstract class ChatEventCompanion[T <: ChatEvent : ClassTag] {

  def format: Format[T]

  def clazz: Class[_] = classTag[T].runtimeClass

  def name: String = getClass.getSimpleName.stripSuffix("$")

}

object ChatEvent {

  // cuz of deserialization
  private val companions: Seq[ChatEventCompanion[_ <: ChatEvent]] = Seq(
    Joined, Left, Said
  )

  implicit def format: Format[ChatEvent] = new Format[ChatEvent] {
    override def reads(json: JsValue): JsResult[ChatEvent] = (json \ "type").asOpt[String] match {
      case Some(tpe) if companions.exists(_.name == tpe) =>
        val companion = companions.find(_.name == tpe).get
        companion.format.reads(json)
      case _ => JsError("`type` field with supported type expected")
    }

    override def writes(o: ChatEvent): JsValue = companions.find(_.clazz == o.getClass) match {
      case Some(companion) =>
        val original = companion.format.asInstanceOf[Format[ChatEvent]].writes(o).as[JsObject]
        original + ("type" -> JsString(companion.name))
      case _ => JsNull
    }
  }

}
