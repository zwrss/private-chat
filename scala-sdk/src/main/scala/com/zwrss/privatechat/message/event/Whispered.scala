package com.zwrss.privatechat.message.event

import play.api.libs.json.Format
import play.api.libs.json.Json

case class Whispered(username: String, message: String) extends ChatEvent

object Whispered extends ChatEventCompanion[Whispered] {
  implicit def format: Format[Whispered] = Json.using[Json.WithDefaultValues].format
}
