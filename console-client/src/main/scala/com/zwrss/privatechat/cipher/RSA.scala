package com.zwrss.privatechat.cipher

import java.nio.charset.StandardCharsets
import java.security.SecureRandom
import java.util.Base64

case class PrivateKey(modulus: BigInt, privateKey: BigInt) {
  lazy val modulusBytes: Int = modulus.toByteArray.length
}

case class PublicKey(modulus: BigInt, publicKey: BigInt) {
  lazy val modulusBytes: Int = modulus.toByteArray.length
}


case class RSA(privateKey: BigInt, publicKey: BigInt, modulus: BigInt) extends Cipher {
  private implicit def _prv: PrivateKey = PrivateKey(modulus, privateKey)

  private implicit def _pub: PublicKey = PublicKey(modulus, publicKey)

  override def encrypt(msg: String): String = RSA.encrypt(msg)

  override def decrypt(msg: String): String = RSA.decrypt(msg)

}

object RSA {

  private def encrypt(x: BigInt)(implicit publicKey: PublicKey): BigInt = {
    if (x >= publicKey.modulus) sys.error("Cannot encode that large message!")
    if (x < 0) sys.error("Cannot encode negative big int!")
    x.modPow(publicKey.publicKey, publicKey.modulus)
  }

  private def decrypt(x: BigInt)(implicit privateKey: PrivateKey): BigInt = {
    if (x < 0) sys.error("Cannot decode negative big int!")
    x.modPow(privateKey.privateKey, privateKey.modulus)
  }

  private def zeros: Stream[Byte] = 0.toByte #:: zeros

  private def encrypt(bytes: Array[Byte])(implicit publicKey: PublicKey): Array[Byte] = {
    bytes.grouped(publicKey.modulusBytes - 1).flatMap { part =>
      val e = encrypt(BigInt(zeros.take(1).toArray ++ part)).toByteArray
      zeros.take(publicKey.modulusBytes - e.length).toArray ++ e

    }.toArray
  }

  private def decrypt(bytes: Array[Byte])(implicit privateKey: PrivateKey): Array[Byte] = {
    if (bytes.length % privateKey.modulusBytes != 0) sys.error(s"Wrong number of bytes ${bytes.length}")
    bytes.grouped(privateKey.modulusBytes).flatMap { part =>
      val d = decrypt(BigInt(zeros.take(1).toArray ++ part)).toByteArray
      d
    }.toArray
  }

  def encrypt(msg: String)(implicit publicKey: PublicKey): String = {
    val encryptedBytes = encrypt(msg.trim getBytes StandardCharsets.UTF_8)
    Base64.getEncoder encodeToString encryptedBytes
  }

  def decrypt(msg: String)(implicit privateKey: PrivateKey): String = {
    val decryptedBytes = decrypt(Base64.getDecoder decode msg.trim)
    new String(decryptedBytes, StandardCharsets.UTF_8)
  }

  private val random = new SecureRandom()

  def random(strength: Int): RSA = {
    if (strength % 8 != 0) sys.error("Strength should be dividable by 8")
    var phi = BigInt(0)
    var modulus = BigInt(0)
    while (modulus.toByteArray.length != strength / 8) {
      val p = BigInt.probablePrime(strength / 2, random)
      val q = BigInt.probablePrime(strength / 2, random)
      phi = (p - 1) * (q - 1)
      modulus = p * q
    }
    val publicKey = BigInt(65537) // this shocked me also
    val privateKey = publicKey.modInverse(phi)
    new RSA(privateKey, publicKey, modulus)
  }


}

case class RSAEncryptor(key: PublicKey) extends Encryptor {

  override def encrypt(msg: String): String = RSA.encrypt(msg)(key)

}