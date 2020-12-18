package com.zwrss.privatechat.message.event

import com.zwrss.privatechat.cipher.PublicKey
import play.api.libs.json.Format
import play.api.libs.json.Json

case class SharedPubKey(username: String, pubKey: PublicKey) extends ChatEvent

object SharedPubKey extends ChatEventCompanion[SharedPubKey] {
  implicit def format: Format[SharedPubKey] = Json.using[Json.WithDefaultValues].format
}
