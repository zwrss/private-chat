package com.zwrss.privatechat.consoleclient

import com.zwrss.privatechat.console.ConsoleController
import com.zwrss.privatechat.consoleclient.console.NameCollection
import com.zwrss.privatechat.logging.Logging


object Client {

  def main(args: Array[String]): Unit = {

    var port: Int = 25852
    var host: String = "localhost"

    if (args.length >= 2) {
      host = args.head
      port = args(1).toInt
    }

    Logging.filename = "client.log"


    val console = new ConsoleController(new NameCollection(host, port))
    console.start()

  }

}
