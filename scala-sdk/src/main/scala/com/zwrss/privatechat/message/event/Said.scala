package com.zwrss.privatechat.message.event

import play.api.libs.json.Format
import play.api.libs.json.Json

case class Said(username: String, message: String) extends ChatEvent

object Said extends ChatEventCompanion[Said] {
  implicit val format: Format[Said] = Json.using[Json.WithDefaultValues].format
}