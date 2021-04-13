package com.zwrss.privatechat.connection

import com.zwrss.privatechat.logging.Logging
import play.api.libs.json.Json
import play.api.libs.json.Writes

import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket
import java.net.SocketException
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
          Logging.debug(s"Got message [$socket]: " + message)
          behavior.handle(this, message)
          if (behavior.isAlive) continueReading()
      }

      behavior onEntry this

      continueReading()

      behavior onExit this

    } catch {
      case e: SocketException => // do nothing
    } finally {
      Logging.debug(s"Disconnected [$socket]")
      close()
    }

  }

  def setBehavior(newBehavior: ConnectionControllerBehavior[_]): Unit = {
    this.behavior onExit this
    this.behavior = newBehavior
    newBehavior onEntry this
  }

  def write[T: Writes](obj: T): Unit = {
    val message = Json toJson obj
    Logging.debug(s"Sending message [$socket]: " + message)
    out println message
  }

  private val onCloseCallbacks: mutable.Queue[() => Unit] = mutable.Queue.empty

  def onClose(callback: () => Unit): Unit = onCloseCallbacks enqueue callback

  def close(): Unit = {
    onCloseCallbacks foreach (_.apply())
    socket.close()
  }

}
