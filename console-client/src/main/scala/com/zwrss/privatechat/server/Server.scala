package com.zwrss.privatechat.server

import java.net.ServerSocket

import com.zwrss.privatechat.connection.ConnectionController
import com.zwrss.privatechat.server.connection.Greeting

object Server {

  private val port: Int = 25852

  def main(args: Array[String]): Unit = {

    val serverSocket = new ServerSocket(port)

    println("Server started, waiting for connections...")

    while (true) {
      val remote = new ConnectionController(serverSocket.accept(), Greeting)
      println("Got connection!")
      remote.start()
    }

  }

}
