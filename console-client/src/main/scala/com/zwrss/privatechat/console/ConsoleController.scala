package com.zwrss.privatechat.console

import org.jline.reader.impl.LineReaderImpl
import org.jline.terminal.TerminalBuilder

class ConsoleController(var behavior: ConsoleControllerBehavior) extends Thread {

  private lazy val reader = {
    val value = new LineReaderImpl(TerminalBuilder.terminal())
    value.setOpt(org.jline.reader.LineReader.Option.ERASE_LINE_ON_FINISH)
    value
  }

  private var commandPrompt: String = ">"

  def setBehavior(newBehavior: ConsoleControllerBehavior): Unit = {
    this.behavior = newBehavior
    newBehavior onEntry this
  }

  def setCommandPrompt(prompt: String) = {
    commandPrompt = prompt
    reader.setPrompt(prompt)
  }

  def println(line: String): Unit = {
    reader.printAbove(line)
  }

  override def run(): Unit = {

    behavior onEntry this

    while (behavior.isAlive) {
      val input = reader.readLine(commandPrompt)
      behavior.onInput(input, this)
    }

    System.exit(0)

  }
}
