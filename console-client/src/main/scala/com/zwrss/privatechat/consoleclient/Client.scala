package com.zwrss.privatechat.consoleclient

import com.zwrss.privatechat.console.ConsoleController
import com.zwrss.privatechat.consoleclient.console.AuthorizationConsole


object Client {

  val port: Int = 25852
  val host: String = "localhost"

  def main(args: Array[String]): Unit = {

    Thread.sleep(1000)

    val console = new ConsoleController(AuthorizationConsole)
    console.start()

  }

}
