package com.zwrss.privatechat.consoleclient.console

import com.zwrss.privatechat.cipher.Encryptor
import com.zwrss.privatechat.cipher.PublicKey
import com.zwrss.privatechat.cipher.RSA
import com.zwrss.privatechat.cipher.RSAEncryptor
import com.zwrss.privatechat.connection.ConnectionController
import com.zwrss.privatechat.console.ConsoleController
import com.zwrss.privatechat.console.ConsoleControllerBehavior
import com.zwrss.privatechat.consoleclient.connection.Whispering
import com.zwrss.privatechat.message.command.ChatCommand
import com.zwrss.privatechat.message.command.GetPubKey
import com.zwrss.privatechat.message.command.Say

class ChatConsole(username: String, remote: ConnectionController, rsa: RSA, remotePublicKey: PublicKey) extends ConsoleControllerBehavior {

  private lazy val encryptor: Encryptor = RSAEncryptor(remotePublicKey)

  override def onEntry(console: ConsoleController): Unit = {
    console setCommandPrompt s"$username: "
    console.refreshScreen()
  }

  private val WHISPER = "/w ([^ ]*) (.*)".r

  override def onInput(line: String, console: ConsoleController): Unit = line match {
    case WHISPER(user, message) =>
      remote write[ChatCommand] GetPubKey(user)
      remote setBehavior new Whispering(console, rsa, user, message)
      console println (s"Whispered to $user: $message")
      console.refreshScreen()
    case _ =>
      remote write[ChatCommand] Say(encryptor encrypt line)
  }
}
