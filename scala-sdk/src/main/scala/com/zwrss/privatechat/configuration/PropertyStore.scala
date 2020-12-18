package com.zwrss.privatechat.configuration

import java.io.File
import java.io.FileReader

import com.zwrss.privatechat.yaml.YamlLookup
import org.yaml.snakeyaml.Yaml

import scala.reflect.ClassTag
import scala.reflect.classTag

sealed trait PropertyStore {
  def get[T: ClassTag](property: String): Option[T]
}

case class CompositePropertyStore(store1: PropertyStore, store2: PropertyStore) extends PropertyStore {
  override def get[T: ClassTag](property: String): Option[T] = (store1 get property) orElse (store2 get property)
}

case class EqualsMapStringsPropertyStore(args: Array[String]) extends PropertyStore {
  private lazy val map: Map[String, String] = args.view.map { entry =>
    val split = entry split '='
    split.head -> split.last
  }.toMap

  override def get[T: ClassTag](property: String): Option[T] = {
    map get property map {
      case value if classTag[T].runtimeClass == classOf[Int] => value.toInt.asInstanceOf[T]
      case value if classTag[T].runtimeClass == classOf[Long] => value.toLong.asInstanceOf[T]
      case value if classTag[T].runtimeClass == classOf[Boolean] => value.toBoolean.asInstanceOf[T]
      case value => value.asInstanceOf[T]
    }
  }
}

case object EnvVariablesPropertyStore extends PropertyStore {
  override def get[T: ClassTag](property: String): Option[T] = {
    sys.env get property map {
      case value if classTag[T].runtimeClass == classOf[Int] => value.toInt.asInstanceOf[T]
      case value if classTag[T].runtimeClass == classOf[Long] => value.toLong.asInstanceOf[T]
      case value if classTag[T].runtimeClass == classOf[Boolean] => value.toBoolean.asInstanceOf[T]
      case value => value.asInstanceOf[T]
    }
  }
}

// todo more formats
// todo filewatcher
case class FilePropertyStore(file: File) extends PropertyStore {
  private lazy val yaml: YamlLookup = YamlLookup.parse(file)

  override def get[T: ClassTag](property: String): Option[T] = {
    val leaf = property.split('.').foldLeft(yaml)(_ / _)
    leaf.asOpt[T]
  }
}

object YamlTester {
  def main(args: Array[String]): Unit = {
    val yaml: java.util.LinkedHashMap[String, Object] = new Yaml().load[java.util.LinkedHashMap[String, Object]](new FileReader("config.yml"))
    println(yaml)
  }
}