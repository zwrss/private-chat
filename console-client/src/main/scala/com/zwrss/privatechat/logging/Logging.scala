package com.zwrss.privatechat.logging

import java.io.FileWriter
import java.io.PrintWriter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object Logging {

  private val loggingEnabled: Boolean = true

  private val overwrite: Boolean = true

  private val dateFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss.SSS")

  private def time: String = dateFormat format LocalDateTime.now()

  var filename: String = "application.log"

  private lazy val writer: PrintWriter = new PrintWriter(new FileWriter(filename, !overwrite), true)

  private def println[T](level: String, msg: T): T = {
    if (loggingEnabled) synchronized {
      val fileName = {
        val stackTrace = Thread.currentThread().getStackTrace
        if (stackTrace.size < 4) "<unknown class>"
        else s"${stackTrace(3).getFileName}:${stackTrace(3).getLineNumber}"
      }
      writer.println(s"$time [$level] $fileName: " + msg.toString)
    }
    msg
  }

  def debug[T](msg: T): T = println("DEBUG", msg)

  def info[T](msg: T): T = println("INFO", msg)

  def warn[T](msg: T): T = println("WARN", msg)

  def error[T](msg: T): T = println("ERROR", msg)

}
