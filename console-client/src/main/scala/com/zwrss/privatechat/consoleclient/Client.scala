package com.zwrss.privatechat.consoleclient

import java.net.Socket

import com.zwrss.privatechat.consoleclient.controller.SocketWrapper
import com.zwrss.privatechat.message.SimpleMessage

object Client {

  private val port: Int = 25852
  private val host: String = "localhost"

  def main(args: Array[String]): Unit = {

    val socket = new SocketWrapper(new Socket(host, port))
    socket write SimpleMessage("Hello!")
    socket write SimpleMessage("This is a wut?")
    socket write SimpleMessage("Bye!")
    Thread sleep 10000
    socket.close()

    System exit 0

  }

}
