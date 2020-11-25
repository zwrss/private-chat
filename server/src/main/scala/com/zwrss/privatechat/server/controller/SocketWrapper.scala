package com.zwrss.privatechat.server.controller

import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket

import play.api.libs.json.Json
import play.api.libs.json.Writes

import scala.annotation.tailrec

class SocketWrapper(socket: Socket, var controller: Controller[_]) extends Thread {

  private lazy val in = new BufferedReader(new InputStreamReader(socket.getInputStream))

  private lazy val out: PrintWriter = new PrintWriter(socket.getOutputStream, true)

  override def run(): Unit = {
    try {

      @tailrec
      def continueReading(): Unit = in.readLine() match {
        case null =>
          // terminate
          println("Got null, terminating!")
        case message =>
          println("Received: " + message)
          controller.handle(this, message)
          continueReading()
      }

      continueReading()

    } finally println("Connection terminated")

  }

  def registerController(controller: Controller[_]): Unit = {
    this.controller = controller
    controller.onEntry(this)
  }

  def write[T: Writes](obj: T): Unit = out println (Json toJson obj)

}
