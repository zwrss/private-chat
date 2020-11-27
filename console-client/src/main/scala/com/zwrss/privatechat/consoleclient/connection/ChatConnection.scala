package com.zwrss.privatechat.consoleclient.connection

import com.zwrss.privatechat.cipher.RSA
import com.zwrss.privatechat.connection.ConnectionController
import com.zwrss.privatechat.connection.ConnectionControllerBehavior
import com.zwrss.privatechat.console.ConsoleController
import com.zwrss.privatechat.message.event.ChatEvent
import com.zwrss.privatechat.message.event.Joined
import com.zwrss.privatechat.message.event.Left
import com.zwrss.privatechat.message.event.Said

class ChatConnection(console: ConsoleController, rsa: RSA) extends ConnectionControllerBehavior[ChatEvent] {
  override protected def _handle: PartialFunction[(ConnectionController, ChatEvent), Unit] = {
    case (_, Said(username, message)) =>
      console println s"$username: $message"
    case (_, Joined(username)) =>
      console println s"$username has joined the server!"
    case (_, Left(username)) =>
      console println s"$username has left the server!"
  }
}
