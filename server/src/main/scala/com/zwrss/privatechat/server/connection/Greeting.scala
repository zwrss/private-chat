package com.zwrss.privatechat.server.connection

import com.zwrss.privatechat.cipher.RSA
import com.zwrss.privatechat.connection.ConnectionController
import com.zwrss.privatechat.connection.ConnectionControllerBehavior
import com.zwrss.privatechat.message.command.Greet
import com.zwrss.privatechat.message.event.Welcome

class Greeting(rsa: RSA) extends ConnectionControllerBehavior[Greet] {

  override protected def _handle: PartialFunction[(ConnectionController, Greet), Unit] = {
    case (remote, message) =>
      remote setBehavior new Chat(message.username, rsa, message.publicKey)
      remote write Welcome(rsa.getPublicKey)
  }

}