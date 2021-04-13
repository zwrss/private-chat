package com.zwrss.privatechat.server

import com.zwrss.privatechat.cipher.RSA
import com.zwrss.privatechat.configuration.PropertyStoreBuilder
import com.zwrss.privatechat.connection.ConnectionController
import com.zwrss.privatechat.logging.Logging
import com.zwrss.privatechat.server.connection.Greeting

import java.io.File
import java.net.ServerSocket

// todo fancy configuration reading
object Server {

  def main(args: Array[String]): Unit = {

    val properties = {
      val configFile: Option[File] = {
        val f = new File("config.yml")
        if (f.exists() && f.isFile && f.canRead) Option(f) else None
      }
      var builder = PropertyStoreBuilder.withArgs(args)
      configFile.foreach(file => builder = builder.withFile(file))
      builder = builder.withEnvVariables()
      builder.build
    }

    val port: Int = properties.get[Int]("server.port") getOrElse 25852

    val serverSocket = new ServerSocket(port)

    Logging.info(s"Server started, waiting for connections on port $port...")
    println(s"Server started, waiting for connections on port $port...")

    while (true) {
      val socket = serverSocket.accept()
      val remote = new ConnectionController(socket, new Greeting(RSA.random(1024)))
      Logging.debug(s"Got connection [$socket]")
      remote.start()
    }

  }

}
