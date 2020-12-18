package com.zwrss.privatechat.message.event

import play.api.libs.json.Format
import play.api.libs.json.Json

case class Left(username: String) extends ChatEvent

object Left extends ChatEventCompanion[Left] {
  implicit val format: Format[Left] = Json.using[Json.WithDefaultValues].format
}