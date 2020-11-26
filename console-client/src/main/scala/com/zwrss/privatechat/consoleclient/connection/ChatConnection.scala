package com.zwrss.privatechat.consoleclient.connection

import com.zwrss.privatechat.connection.ConnectionController
import com.zwrss.privatechat.connection.ConnectionControllerBehavior
import com.zwrss.privatechat.console.ConsoleController
import com.zwrss.privatechat.message.ChatMessage

class ChatConnection(console: ConsoleController) extends ConnectionControllerBehavior[ChatMessage] {
  override protected def _handle: PartialFunction[(ConnectionController, ChatMessage), Unit] = {
    case (_, message) =>
      console println s"${message.username}: ${message.message}"
  }
}
