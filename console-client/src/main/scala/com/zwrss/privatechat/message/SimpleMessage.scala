package com.zwrss.privatechat.message

import play.api.libs.json.Format
import play.api.libs.json.Json

case class SimpleMessage(message: String)

object SimpleMessage {
  implicit val format: Format[SimpleMessage] = Json.using[Json.WithDefaultValues].format
}