package com.zwrss.privatechat.consoleclient.console

import com.zwrss.privatechat.connection.ConnectionController
import com.zwrss.privatechat.console.ConsoleController
import com.zwrss.privatechat.console.ConsoleControllerBehavior
import com.zwrss.privatechat.message.ChatMessage

class ChatConsole(username: String, remote: ConnectionController) extends ConsoleControllerBehavior {

  override def onEntry(console: ConsoleController): Unit = {
    console setCommandPrompt s"$username: "
  }

  override def onInput(line: String, console: ConsoleController): Unit = {
    remote write ChatMessage(username, line)
  }
}
