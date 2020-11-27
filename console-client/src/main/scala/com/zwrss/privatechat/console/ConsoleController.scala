package com.zwrss.privatechat.console

import com.zwrss.privatechat.logging.Logging
import org.fusesource.jansi.Ansi
import org.fusesource.jansi.AnsiConsole

import scala.collection.mutable
import scala.io.StdIn

class ConsoleController(var behavior: ConsoleControllerBehavior) extends Thread {

  private lazy val ansi = Ansi.ansi()

  private var commandPrompt: String = ">"

  def setBehavior(newBehavior: ConsoleControllerBehavior): Unit = {
    this.behavior = newBehavior
    newBehavior onEntry this
  }

  def setCommandPrompt(prompt: String) = commandPrompt = prompt

  private val screen: mutable.Stack[String] = new mutable.Stack[String]()

  def println(line: String): Unit = synchronized {
    Logging.debug(line)
    screen push line
    refreshScreen()
  }

  def refreshScreen(): Unit = synchronized {
    var message = Ansi.ansi.saveCursorPosition().cursorUpLine().cursorToColumn(5000).eraseScreen(Ansi.Erase.BACKWARD).cursorToColumn(0)
    if (screen.nonEmpty) {
      message = message.a(screen.head)
      screen.tail.foreach(line => message = message.cursorUpLine().a(line))
    }
    message = message.cursorDown(screen.size + 1).restoreCursorPosition()
    print(message)
  }

  override def run(): Unit = {
    AnsiConsole.systemInstall()
    print(ansi.eraseScreen())

    behavior onEntry this

    while(behavior.isAlive) {
      print(commandPrompt)
      val input = StdIn.readLine()
      behavior.onInput(input, this)
    }

    print(ansi.eraseLine())
    AnsiConsole.systemUninstall()
  }
}
