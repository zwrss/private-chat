package com.zwrss.privatechat.connection

import play.api.libs.json.Json
import play.api.libs.json.Reads

abstract class ConnectionControllerBehavior[Message: Reads] {

  def isAlive: Boolean = true

  type RemoteWithMessage = (ConnectionController, Message)

  protected def _handle: PartialFunction[RemoteWithMessage, Unit]

  def onEntry(remote: ConnectionController): Unit = {}

  def onExit(remote: ConnectionController): Unit = {}

  private[connection] def handle(remote: ConnectionController, message: String): Unit = {
    val deserializedMessage: Message = (Json parse message).as[Message]
    if (_handle.isDefinedAt(remote -> deserializedMessage)) {
      _handle.apply(remote -> deserializedMessage)
    }
  }

}
