package com.zwrss.privatechat.yaml

import java.io.File
import java.io.FileReader
import java.io.Reader
import java.util.NoSuchElementException

import org.yaml.snakeyaml.Yaml

import scala.reflect.ClassTag
import scala.reflect.classTag

trait YamlLookup {
  def /(name: String): YamlLookup

  def as[T: ClassTag](c: Class[T]): T = as[T]

  def as[T: ClassTag]: T = asOpt[T] getOrElse (throw new NoSuchElementException("Undefined YAML"))

  def asOpt[T: ClassTag]: Option[T]
}

case class YamlValue[V](value: V) extends YamlLookup {
  override def /(name: String): YamlLookup = YamlEmpty

  private def assignable(ancestor: Class[_], child: Class[_]): Boolean = {
    try {
      if (ancestor isAssignableFrom child) true
      else if (ancestor.isPrimitive && ancestor == child.getField("TYPE").get(null)) true
      else false
    } catch {
      case t: Throwable => false
    }
  }

  override def asOpt[T: ClassTag]: Option[T] = value match {
    case x if assignable(classTag[T].runtimeClass, x.getClass) => Option(x.asInstanceOf[T])
    case _ => None
  }

  override def toString: String = s"$value: ${value.getClass.getSimpleName}"
}

case class YamlNode(node: java.util.Map[String, Object]) extends YamlLookup {
  override def /(name: String): YamlLookup = node.get(name) match {
    case i: Integer => YamlValue(i)
    case i: java.lang.Long => YamlValue(i.toLong)
    case i: java.lang.Boolean => YamlValue(i.booleanValue())
    case i: String => YamlValue[String](i)
    case i: java.util.Map[_, _] => YamlNode(i.asInstanceOf[java.util.Map[String, Object]])
    case _ => YamlEmpty
  }

  override def asOpt[T: ClassTag]: Option[T] = None

  override def toString: String = {
    import scala.jdk.CollectionConverters._
    val values = node.asScala.map {
      case (key, value) =>
        s"$key = " + /(key).toString
    }
    s"{ ${values.mkString(", ")} }"
  }
}

case object YamlEmpty extends YamlLookup {
  override def /(name: String): YamlLookup = YamlEmpty

  override def asOpt[T: ClassTag]: Option[T] = None
}

object YamlLookup {

  def parse(reader: Reader): YamlLookup = YamlNode(new Yaml().load(reader))

  def parse(file: File): YamlLookup = parse(new FileReader(file))

}
