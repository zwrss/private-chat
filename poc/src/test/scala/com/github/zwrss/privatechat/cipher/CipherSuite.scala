package com.github.zwrss.privatechat.cipher

object CipherSuite {

  def main(args: Array[String]): Unit = {

    val cipher = Cipher.rsa

    def en(msg: String): String = {
      val encrypted = cipher encrypt msg
      println(s"-- ENCRYPT -- \n$msg\n-- = --\n$encrypted\n-- - --")
      encrypted
    }

    def de(msg: String): String = {
      val decrypted = cipher decrypt msg
      println(s"-- DECRYPT -- \n$msg\n-- = --\n$decrypted\n-- - --")
      decrypted
    }

    def t(msg: String): Unit = {
      val decrypted = de(en(msg))
      if (msg == decrypted) println("OK")
      else println("ERROR")
    }


    t("12345")
    t("Wat is dat?")
    t("This is some pretty long message as for chat service.")
    t("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Cras molestie porta ipsum, at gravida eros venenatis ac. Donec semper neque scelerisque, euismod mauris ut, porttitor felis. Maecenas luctus ultricies massa, id laoreet diam convallis vitae. Donec dapibus tristique aliquet. Suspendisse sodales luctus lobortis. In vitae lobortis orci, in lobortis erat. Vivamus facilisis felis nec diam ultrices scelerisque. Proin ultrices lectus pulvinar nisl consequat iaculis. Morbi eget dignissim libero, eget faucibus nisl. Nullam egestas enim ac ultrices interdum. ")

  }

}
