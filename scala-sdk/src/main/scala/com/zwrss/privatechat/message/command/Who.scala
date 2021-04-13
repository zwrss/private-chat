package com.zwrss.privatechat.message.command

import play.api.libs.json.Format
import play.api.libs.json.Json

case object Who extends ChatCommand

object WhoCompanion extends ChatCommandCompanion[Who.type] {
  implicit val format: Format[Who.type] = Json.using[Json.WithDefaultValues].format
}