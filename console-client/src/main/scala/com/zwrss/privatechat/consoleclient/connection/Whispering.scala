package com.zwrss.privatechat.consoleclient.connection

import com.zwrss.privatechat.cipher.RSA
import com.zwrss.privatechat.cipher.RSAEncryptor
import com.zwrss.privatechat.connection.ConnectionController
import com.zwrss.privatechat.connection.ConnectionControllerBehavior
import com.zwrss.privatechat.console.ConsoleController
import com.zwrss.privatechat.message.command.ChatCommand
import com.zwrss.privatechat.message.command.Whisper
import com.zwrss.privatechat.message.event.ChatEvent
import com.zwrss.privatechat.message.event.SharedPubKey

class Whispering(console: ConsoleController, rsa: RSA, username: String, message: String) extends ConnectionControllerBehavior[ChatEvent] {
  override protected def _handle: PartialFunction[(ConnectionController, ChatEvent), Unit] = {
    case (remote, SharedPubKey(u, pubKey)) if u == username =>
      remote write[ChatCommand] Whisper(username, RSAEncryptor(pubKey) encrypt message)
      remote setBehavior new ChatConnection(console, rsa)
  }
}
