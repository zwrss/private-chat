package com.zwrss.privatechat.logging

import java.io.FileWriter
import java.io.PrintWriter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object Logging {

  object Level extends Enumeration {
    val DEBUG = Value(4, "DEBUG")
    val INFO = Value(3, "INFO")
    val WARN = Value(2, "WARN")
    val ERROR = Value(1, "ERROR")
    val NONE = Value(0, "NONE")
  }

  private val loggingLevel: Level.Value = Level.DEBUG

  private val overwrite: Boolean = true

  private val dateFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss.SSS")

  private def time: String = dateFormat format LocalDateTime.now()

  var filename: Option[String] = None

  private lazy val writer: PrintWriter = filename match {
    case Some(f) => new PrintWriter(new FileWriter(f, !overwrite), true)
    case _ => new PrintWriter(System.out)
  }

  private def println[T](level: Level.Value, pattern: String, obj: T): T = {
    if (level.id <= loggingLevel.id) synchronized {
      val fileName = {
        val stackTrace = Thread.currentThread().getStackTrace
        if (stackTrace.size < 4) "<unknown class>"
        else s"${stackTrace(3).getFileName}:${stackTrace(3).getLineNumber}"
      }
      writer.println(s"$time [$level] $fileName: " + pattern.format(obj.toString))
      writer.flush()
    }
    obj
  }

  def debug[T](msg: T): T = println(Level.DEBUG, "%s", msg)

  def debug[T](pattern: String, msg: T): T = println(Level.DEBUG, pattern, msg)

  def info[T](msg: T): T = println(Level.INFO, "%s", msg)

  def info[T](pattern: String, msg: T): T = println(Level.INFO, pattern, msg)

  def warn[T](msg: T): T = println(Level.WARN, "%s", msg)

  def error[T](msg: T): T = println(Level.ERROR, "%s", msg)

}
