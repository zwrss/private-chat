package com.github.zwrss.privatechat

trait Logging {

  // todo serious logging

  private def log(prefix: String, msg: Any): Unit = {
    println(prefix + ": " + msg.toString)
  }

  final protected def debug(msg: Any): Unit = log("DEBUG", msg)

  final protected def info(msg: Any): Unit = log("INFO", msg)

  final protected def warn(msg: Any): Unit = log("WARN", msg)

  final protected def error(msg: Any): Unit = log("ERROR", msg)
  
}
