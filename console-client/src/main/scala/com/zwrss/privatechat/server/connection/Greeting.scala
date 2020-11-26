package com.zwrss.privatechat.server.connection

import com.zwrss.privatechat.connection.ConnectionController
import com.zwrss.privatechat.connection.ConnectionControllerBehavior
import com.zwrss.privatechat.message.GreetingMessage

object Greeting extends ConnectionControllerBehavior[GreetingMessage] {

  override protected def _handle: PartialFunction[(ConnectionController, GreetingMessage), Unit] = {
    case (remote, message) =>
      remote setBehavior new Chat(message.username)
  }

}