package com.zwrss.privatechat.server

import java.io.File
import java.net.ServerSocket

import com.zwrss.privatechat.cipher.RSA
import com.zwrss.privatechat.configuration.PropertyStoreBuilder
import com.zwrss.privatechat.connection.ConnectionController
import com.zwrss.privatechat.logging.Logging
import com.zwrss.privatechat.server.connection.Greeting

// todo fancy configuration reading
object Server {

  def main(args: Array[String]): Unit = {

    val properties = PropertyStoreBuilder.withArgs(args).withFile(new File("config.yml")).withEnvVariables().build

    val port: Int = properties.get[Int]("server.port") getOrElse 25852

    val serverSocket = new ServerSocket(port)

    Logging.info(s"Server started, waiting for connections on port $port...")

    while (true) {
      val socket = serverSocket.accept()
      val remote = new ConnectionController(socket, new Greeting(RSA.random(1024)))
      println(s"Got connection [$socket]")
      remote.start()
    }

  }

}
