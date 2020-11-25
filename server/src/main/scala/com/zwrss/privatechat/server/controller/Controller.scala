package com.zwrss.privatechat.server.controller

import play.api.libs.json.Json
import play.api.libs.json.Reads

abstract class Controller[Message: Reads] {

  def isAlive: Boolean = true

  type MessageWithWrapper = (SocketWrapper, Message)

  protected def _handle: PartialFunction[MessageWithWrapper, Unit]

  private[controller] def handle(wrapper: SocketWrapper, message: String): Unit = {
    val deserializedMessage: Message = (Json parse message).as[Message]
    if (_handle.isDefinedAt(wrapper -> deserializedMessage)) {
      _handle.apply(wrapper -> deserializedMessage)
    }
  }

  def onEntry(wrapper: SocketWrapper): Unit = {}

}
