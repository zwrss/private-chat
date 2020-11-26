package com.zwrss.privatechat.cipher

trait Decryptor {
  def decrypt(msg: String): String
}


trait Encryptor {
  def encrypt(msg: String): String
}

trait Cipher extends Encryptor with Decryptor

object Cipher {

  def rsa: Cipher = RSA.random(1024)

  def ceasar: Cipher = CeasarsCipher.random()

}