package com.zwrss.privatechat.consoleclient.console

import java.net.Socket

import com.zwrss.privatechat.connection.ConnectionController
import com.zwrss.privatechat.console.ConsoleController
import com.zwrss.privatechat.console.ConsoleControllerBehavior
import com.zwrss.privatechat.consoleclient.Client
import com.zwrss.privatechat.consoleclient.connection.ChatConnection

object AuthorizationConsole extends ConsoleControllerBehavior {

  override def onEntry(console: ConsoleController): Unit = {
    console println "What is your name?"
  }

  override def onInput(line: String, console: ConsoleController): Unit = {
    console.refreshScreen()
    val remote = new ConnectionController(new Socket(Client.host, Client.port), new ChatConnection(console))
    remote.start()
    console setBehavior new ChatConsole(line, remote)
  }

}
