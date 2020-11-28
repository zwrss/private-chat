package com.zwrss.privatechat.message.event

import play.api.libs.json.Format
import play.api.libs.json.Json

case class Joined(username: String) extends ChatEvent

object Joined extends ChatEventCompanion[Joined] {
  implicit val format: Format[Joined] = Json.using[Json.WithDefaultValues].format
}