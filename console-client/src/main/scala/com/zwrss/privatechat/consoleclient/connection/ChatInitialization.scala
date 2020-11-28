package com.zwrss.privatechat.consoleclient.connection

import com.zwrss.privatechat.cipher.RSA
import com.zwrss.privatechat.connection.ConnectionController
import com.zwrss.privatechat.connection.ConnectionControllerBehavior
import com.zwrss.privatechat.console.ConsoleController
import com.zwrss.privatechat.consoleclient.console.ChatConsole
import com.zwrss.privatechat.message.command.Greet
import com.zwrss.privatechat.message.event.Welcome

class ChatInitialization(username: String, console: ConsoleController, rsa: RSA) extends ConnectionControllerBehavior[Welcome] {

  override def onEntry(remote: ConnectionController): Unit = {
    remote write Greet(username, rsa.getPublicKey)
  }

  override protected def _handle: PartialFunction[(ConnectionController, Welcome), Unit] = {
    case (remote, message) =>
      console setBehavior new ChatConsole(username, remote, rsa, message.publicKey)
      remote setBehavior new ChatConnection(console, rsa)
  }
}
