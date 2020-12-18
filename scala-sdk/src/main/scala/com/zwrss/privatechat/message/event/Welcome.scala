package com.zwrss.privatechat.message.event

import com.zwrss.privatechat.cipher.PublicKey
import play.api.libs.json.Format
import play.api.libs.json.Json

case class Welcome(publicKey: PublicKey)

object Welcome {
  implicit val format: Format[Welcome] = Json.using[Json.WithDefaultValues].format
}