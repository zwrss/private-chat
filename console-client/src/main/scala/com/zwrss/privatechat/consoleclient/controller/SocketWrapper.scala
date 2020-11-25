package com.zwrss.privatechat.consoleclient.controller

import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket

import play.api.libs.json.Json
import play.api.libs.json.Writes

class SocketWrapper(socket: Socket) {

  private lazy val in = new BufferedReader(new InputStreamReader(socket.getInputStream))

  private lazy val out: PrintWriter = new PrintWriter(socket.getOutputStream, true)

  def write[T: Writes](obj: T): Unit = out println (Json toJson obj)

  def close(): Unit = socket.close()

}
