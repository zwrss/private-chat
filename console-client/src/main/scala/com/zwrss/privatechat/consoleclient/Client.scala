package com.zwrss.privatechat.consoleclient

import org.fusesource.jansi.Ansi

import scala.io.StdIn


object Client {

  private val port: Int = 25852
  private val host: String = "localhost"

  def main(args: Array[String]): Unit = {

    //    val socket = new SocketWrapper(new Socket(host, port))
    //    socket write SimpleMessage("Hello!")
    //    socket write SimpleMessage("This is a wut?")
    //    socket write SimpleMessage("Bye!")
    //    Thread sleep 10000
    //    socket.close()
    //
    //    System exit 0

    var buffer: String = ""

    println("Last message: ")

    val writingThread: Thread = new Thread {
      override def run(): Unit = {
        var i = 0
        while (true) {
          i += 1
          print(Ansi.ansi.saveCursorPosition().cursorUpLine().eraseScreen(Ansi.Erase.BACKWARD).a(s"Last message $i $buffer").cursorDownLine().restoreCursorPosition())
          Thread.sleep(500)
        }
      }
    }
    writingThread.start()

    while (true) {
      print("Me: ")
      buffer = StdIn.readLine()
    }

  }

}
