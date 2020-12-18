package com.zwrss.privatechat.message.command

import play.api.libs.json.Format
import play.api.libs.json.Json

case class Whisper(username: String, message: String) extends ChatCommand


object Whisper extends ChatCommandCompanion[Whisper] {
  implicit val format: Format[Whisper] = Json.using[Json.WithDefaultValues].format
}