package com.zwrss.privatechat.message.command

import com.zwrss.privatechat.cipher.PublicKey
import play.api.libs.json.Format
import play.api.libs.json.Json

case class Greet(username: String, publicKey: PublicKey) extends ChatCommand

object Greet extends ChatCommandCompanion[Greet] {
  implicit val format: Format[Greet] = Json.using[Json.WithDefaultValues].format
}