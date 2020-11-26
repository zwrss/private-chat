package com.zwrss.privatechat.connection

import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket

import play.api.libs.json.Json
import play.api.libs.json.Writes

import scala.annotation.tailrec
import scala.collection.mutable

class ConnectionController(socket: Socket, var behavior: ConnectionControllerBehavior[_]) extends Thread {

  private lazy val in = new BufferedReader(new InputStreamReader(socket.getInputStream))

  private lazy val out: PrintWriter = new PrintWriter(socket.getOutputStream, true)

  override def run(): Unit = {
    try {

      @tailrec
      def continueReading(): Unit = in.readLine() match {
        case null =>
        // terminate
        case message =>
          behavior.handle(this, message)
          continueReading()
      }

      behavior onEntry this

      continueReading()

      behavior onExit this

    } finally {
      close()
    }

  }

  def setBehavior(newBehavior: ConnectionControllerBehavior[_]): Unit = {
    this.behavior onExit this
    this.behavior = newBehavior
    newBehavior onEntry this
  }

  def write[T: Writes](obj: T): Unit = out println (Json toJson obj)

  private val onCloseCallbacks: mutable.Queue[() => Unit] = mutable.Queue.empty

  def onClose(callback: () => Unit): Unit = onCloseCallbacks enqueue callback

  def close(): Unit = {
    onCloseCallbacks foreach (_.apply())
    socket.close()
  }

}
