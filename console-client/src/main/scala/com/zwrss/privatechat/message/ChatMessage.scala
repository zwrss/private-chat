package com.zwrss.privatechat.message

import play.api.libs.json.Format
import play.api.libs.json.Json

case class ChatMessage(username: String, message: String)

object ChatMessage {
  implicit val format: Format[ChatMessage] = Json.using[Json.WithDefaultValues].format
}