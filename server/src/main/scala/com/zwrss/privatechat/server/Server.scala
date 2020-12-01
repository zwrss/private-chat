package com.zwrss.privatechat.server

import java.net.ServerSocket

import com.zwrss.privatechat.cipher.RSA
import com.zwrss.privatechat.connection.ConnectionController
import com.zwrss.privatechat.logging.Logging
import com.zwrss.privatechat.server.connection.Greeting

object Server {

  def main(args: Array[String]): Unit = {

    var port: Int = 25852

    sys.env.get("PRIVATECHAT_SERVER_PORT").foreach { portString =>
      port = portString.toInt
    }

    if (args.length >= 1) {
      port = args.head.toInt
    }

    Logging.filename = "server.log"

    val serverSocket = new ServerSocket(port)

    Logging.info(s"Server started, waiting for connections on port $port...")

    while (true) {
      val socket = serverSocket.accept()
      val remote = new ConnectionController(socket, new Greeting(RSA.random(1024)))
      println(s"Got connection [$socket]")
      remote.start()
    }

  }

}
