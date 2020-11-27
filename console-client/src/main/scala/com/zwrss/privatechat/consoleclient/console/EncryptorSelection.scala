package com.zwrss.privatechat.consoleclient.console

import java.net.Socket

import com.zwrss.privatechat.cipher.RSA
import com.zwrss.privatechat.connection.ConnectionController
import com.zwrss.privatechat.console.ConsoleController
import com.zwrss.privatechat.console.ConsoleControllerBehavior
import com.zwrss.privatechat.consoleclient.Client
import com.zwrss.privatechat.consoleclient.connection.ChatInitialization

class EncryptorSelection(username: String) extends ConsoleControllerBehavior {

  override def onEntry(console: ConsoleController): Unit = {
    // todo to move to onInput
    console println "Generating RSA key and requesting handshake"
    val rsa = RSA.random(1024)
    val remote = new ConnectionController(new Socket(Client.host, Client.port), new ChatInitialization(username, console, rsa))
    remote.start()
  }

  override def onInput(line: String, console: ConsoleController): Unit = {
    // todo encryption selection
  }

}
