package com.github.zwrss.privatechat.cipher

import java.nio.charset.StandardCharsets

object BigIntSuite {
  def main(args: Array[String]): Unit = {
    val str = "lM";
    println(s"String: $str")
    val bytes = str.getBytes(StandardCharsets.UTF_8)
    println(s"Bytes: ${bytes.map(b => String.format("%02X", Byte.box(b))).mkString(":")}")
    val bigInt = BigInt(bytes)
    println(s"BigInt: $bigInt")
  }
}
