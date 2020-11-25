package com.zwrss.privatechat.server

import java.net.ServerSocket

import com.zwrss.privatechat.server.controller.SimpleController
import com.zwrss.privatechat.server.controller.SocketWrapper

object Server {

  private val port: Int = 25852

  def main(args: Array[String]): Unit = {

    val serverSocket = new ServerSocket(port)

    println("Server started, waiting for connections...")

    while (true) {
      val socketWrapper = new SocketWrapper(serverSocket.accept(), SimpleController)
      println("Got connection!")
      socketWrapper.start()
    }

  }

}
