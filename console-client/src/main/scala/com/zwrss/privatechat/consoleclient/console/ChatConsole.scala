package com.zwrss.privatechat.consoleclient.console

import com.zwrss.privatechat.cipher.Encryptor
import com.zwrss.privatechat.cipher.PublicKey
import com.zwrss.privatechat.cipher.RSA
import com.zwrss.privatechat.cipher.RSAEncryptor
import com.zwrss.privatechat.connection.ConnectionController
import com.zwrss.privatechat.console.ConsoleController
import com.zwrss.privatechat.console.ConsoleControllerBehavior
import com.zwrss.privatechat.consoleclient.connection.Disconnected
import com.zwrss.privatechat.consoleclient.connection.Whispering
import com.zwrss.privatechat.message.command.ChatCommand
import com.zwrss.privatechat.message.command.GetPubKey
import com.zwrss.privatechat.message.command.Say
import com.zwrss.privatechat.message.command.Who

class ChatConsole(username: String, remote: ConnectionController, rsa: RSA, remotePublicKey: PublicKey) extends ConsoleControllerBehavior {

  private lazy val encryptor: Encryptor = RSAEncryptor(remotePublicKey)
  private val WHISPER = "/w (?:\"([^\"]+)\"|([^ ]*)) (.*)".r
  private val HELP = "/help"
  private val WHO = "/who"
  private val EXIT = "/exit"

  override def onEntry(console: ConsoleController): Unit = {
    console println s"SYSTEM: Connected to server."
    console println s"SYSTEM: Remember that messages sent to the common chat are visible to the server!"
    console setCommandPrompt s"$username: "
  }

  override def onInput(line: String, console: ConsoleController): Unit = line match {
    case HELP =>
      console println s"SYSTEM: Commands /help, /exit, /who, /w <whisper target> <whisper message>"
    case WHO =>
      remote write[ChatCommand] Who
    case EXIT =>
      console println s"SYSTEM: Received kill signal, disconnecting..."
      remote setBehavior Disconnected
      console setBehavior Stopped
    case WHISPER(unquotedUser, quotedUser, message) =>
      val user = if (quotedUser != null && quotedUser.nonEmpty) quotedUser else unquotedUser
      remote write[ChatCommand] GetPubKey(user)
      remote setBehavior new Whispering(console, rsa, user, message)
      console println (s"Whispered to $user: $message")
    case _ =>
      remote write[ChatCommand] Say(encryptor encrypt line)
  }
}
