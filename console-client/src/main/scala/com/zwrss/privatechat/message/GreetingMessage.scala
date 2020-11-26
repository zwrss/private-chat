package com.zwrss.privatechat.message

import play.api.libs.json.Format
import play.api.libs.json.Json

case class GreetingMessage(username: String)

object GreetingMessage {
  implicit val format: Format[GreetingMessage] = Json.using[Json.WithDefaultValues].format
}