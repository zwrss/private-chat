package com.zwrss.privatechat.message.command

import play.api.libs.json.Format
import play.api.libs.json.Json

case class Say(message: String) extends ChatCommand

object Say extends ChatCommandCompanion[Say] {
  implicit val format: Format[Say] = Json.using[Json.WithDefaultValues].format
}