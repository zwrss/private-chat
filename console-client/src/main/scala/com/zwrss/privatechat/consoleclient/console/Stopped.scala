package com.zwrss.privatechat.consoleclient.console

import com.zwrss.privatechat.console.ConsoleController
import com.zwrss.privatechat.console.ConsoleControllerBehavior

object Stopped extends ConsoleControllerBehavior {
  override def isAlive: Boolean = false

  override def onInput(line: String, console: ConsoleController): Unit = {}
}
