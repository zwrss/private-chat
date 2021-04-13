package com.zwrss.privatechat.consoleclient.console

import com.zwrss.privatechat.cipher.RSA
import com.zwrss.privatechat.connection.ConnectionController
import com.zwrss.privatechat.console.ConsoleController
import com.zwrss.privatechat.console.ConsoleControllerBehavior
import com.zwrss.privatechat.consoleclient.connection.ChatInitialization

import java.net.Socket

class EncryptorSelection(host: String, port: Int, username: String) extends ConsoleControllerBehavior {

  override def onEntry(console: ConsoleController): Unit = {
    // todo to move to onInput
    console println s"SYSTEM: Generating RSA key and requesting handshake to $host:$port"
    val rsa = RSA.random(1024)
    val remote = new ConnectionController(new Socket(host, port), new ChatInitialization(username, console, rsa))
    remote.start()
  }

  override def onInput(line: String, console: ConsoleController): Unit = {
    // todo encryption selection
  }

}
