package com.zwrss.privatechat.consoleclient

import com.zwrss.privatechat.console.ConsoleController
import com.zwrss.privatechat.consoleclient.console.NameCollection
import com.zwrss.privatechat.logging.Logging


object Client {

  val port: Int = 25852
  val host: String = "localhost"

  def main(args: Array[String]): Unit = {

    Logging.filename = "client.log"

    val console = new ConsoleController(new NameCollection)
    console.start()

  }

}
