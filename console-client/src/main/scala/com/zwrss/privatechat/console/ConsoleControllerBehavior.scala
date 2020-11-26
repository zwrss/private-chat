package com.zwrss.privatechat.console

abstract class ConsoleControllerBehavior {

  def isAlive: Boolean = true

  def onInput(line: String, console: ConsoleController): Unit

  def onEntry(console: ConsoleController): Unit = {}

}
