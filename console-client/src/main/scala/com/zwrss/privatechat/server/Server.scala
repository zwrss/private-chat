package com.zwrss.privatechat.server

import java.net.ServerSocket

import com.zwrss.privatechat.cipher.RSA
import com.zwrss.privatechat.connection.ConnectionController
import com.zwrss.privatechat.logging.Logging
import com.zwrss.privatechat.server.connection.Greeting

object Server {

  private val port: Int = 25852

  def main(args: Array[String]): Unit = {

    Logging.filename = "server.log"

    val serverSocket = new ServerSocket(port)

    println("Server started, waiting for connections...")

    while (true) {
      val remote = new ConnectionController(serverSocket.accept(), new Greeting(RSA.random(1024)))
      println("Got connection!")
      remote.start()
    }

  }

}
