package com.zwrss.privatechat.message

import play.api.libs.json.Format
import play.api.libs.json.Json

case class EncryptedMessage(payload: String)

object EncryptedMessage {
  implicit val format: Format[EncryptedMessage] = Json.using[Json.WithDefaultValues].format
}
