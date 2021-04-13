package com.zwrss.privatechat.message.event

import play.api.libs.json.Format
import play.api.libs.json.Json

case class UserList(list: Seq[String]) extends ChatEvent

object UserList extends ChatEventCompanion[UserList] {
  implicit val format: Format[UserList] = Json.using[Json.WithDefaultValues].format
}