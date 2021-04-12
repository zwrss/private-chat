package com.zwrss.privatechat.consoleclient.console

import com.zwrss.privatechat.console.ConsoleController
import com.zwrss.privatechat.console.ConsoleControllerBehavior

class NameCollection(host: String, port: Int) extends ConsoleControllerBehavior {

  override def onEntry(console: ConsoleController): Unit = {
    console println "What is your name?"
  }

  override def onInput(line: String, console: ConsoleController): Unit = {
    console setBehavior new EncryptorSelection(host, port, line)
  }

}
