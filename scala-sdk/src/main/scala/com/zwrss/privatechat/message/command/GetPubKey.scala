package com.zwrss.privatechat.message.command

import play.api.libs.json.Format
import play.api.libs.json.Json

case class GetPubKey(username: String) extends ChatCommand


object GetPubKey extends ChatCommandCompanion[GetPubKey] {
  implicit val format: Format[GetPubKey] = Json.using[Json.WithDefaultValues].format
}