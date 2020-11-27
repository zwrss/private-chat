package com.zwrss.privatechat.consoleclient.console

import com.zwrss.privatechat.cipher.PublicKey
import com.zwrss.privatechat.connection.ConnectionController
import com.zwrss.privatechat.console.ConsoleController
import com.zwrss.privatechat.console.ConsoleControllerBehavior
import com.zwrss.privatechat.message.command.ChatCommand
import com.zwrss.privatechat.message.command.Say

class ChatConsole(username: String, remote: ConnectionController, remotePublicKey: PublicKey) extends ConsoleControllerBehavior {

  override def onEntry(console: ConsoleController): Unit = {
    console setCommandPrompt s"$username: "
    console.refreshScreen()
  }

  override def onInput(line: String, console: ConsoleController): Unit = {
    remote write[ChatCommand] Say(line)
  }
}
