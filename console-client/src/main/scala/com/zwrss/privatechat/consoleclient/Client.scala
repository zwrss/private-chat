package com.zwrss.privatechat.consoleclient

import com.zwrss.privatechat.configuration.PropertyStoreBuilder
import com.zwrss.privatechat.console.ConsoleController
import com.zwrss.privatechat.consoleclient.console.NameCollection
import com.zwrss.privatechat.logging.Logging

import java.io.File


object Client {

  def main(args: Array[String]): Unit = {

    val properties = PropertyStoreBuilder.withArgs(args).withFile(new File("config.yml")).withEnvVariables().build
    // todo injecting properties


    val port: Int = properties.get[Int]("server.port") getOrElse 25852
    val host: String = properties.get[String]("server.host") getOrElse "localhost"

    Logging.filename = properties.get[String]("logging.default.filename") getOrElse "client.log" // todo pass configuration


    val console = new ConsoleController(new NameCollection(host, port))
    console.start()

  }

}
