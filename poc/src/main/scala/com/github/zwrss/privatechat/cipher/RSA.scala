package com.github.zwrss.privatechat.cipher

import java.nio.charset.StandardCharsets
import java.security.SecureRandom
import java.util.Base64

import com.github.zwrss.privatechat.Logging

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

object RSA extends Logging {

  private def encrypt(x: BigInt)(implicit publicKey: PublicKey): BigInt = {
    if (x >= publicKey.modulus) sys.error("Cannot encode that large message!")
    if (x < 0) sys.error("Cannot encode negative big int!")
    debug("encrypting big int: " + x)
    val res = x.modPow(publicKey.publicKey, publicKey.modulus)
    debug("encrypted big int: " + res)
    res
  }

  private def decrypt(x: BigInt)(implicit privateKey: PrivateKey): BigInt = {
    if (x < 0) sys.error("Cannot decode negative big int!")
    debug("decrypting big int: " + x)
    val res = x.modPow(privateKey.privateKey, privateKey.modulus)
    debug("decrypted big int: " + res)
    res
  }

  private def zeros: Stream[Byte] = 0.toByte #:: zeros

  private def encrypt(bytes: Array[Byte])(implicit publicKey: PublicKey): Array[Byte] = {
    debug("encrypting bytes: " + bytes.map(0xff & _).mkString(","))
    val res: Array[Byte] = bytes.grouped(publicKey.modulusBytes - 1).flatMap { part =>
      debug(s"took ${part.length} bytes")
      val e = encrypt(BigInt(zeros.take(1).toArray ++ part)).toByteArray
      debug(s"size of encrypted bytes: ${e.length} / ${publicKey.modulusBytes}")
      val r = zeros.take(publicKey.modulusBytes - e.length).toArray ++ e
      debug(s"supplemented size: ${r.length}")
      r
    }.toArray
    debug("encrypted bytes: " + res.map(0xff & _).mkString(","))
    res
  }

  private def decrypt(bytes: Array[Byte])(implicit privateKey: PrivateKey): Array[Byte] = {
    if (bytes.length % privateKey.modulusBytes != 0) sys.error(s"Wrong number of bytes ${bytes.length}")
    debug("decrypting bytes: " + bytes.map(b => String.format("%02x", Byte.box(b))).mkString)
    val res = bytes.grouped(privateKey.modulusBytes).flatMap { part =>
      val d = decrypt(BigInt(zeros.take(1).toArray ++ part)).toByteArray
      d
    }.toArray
    debug("decrypted bytes: " + res.mkString(" "))
    res
  }

  def encrypt(msg: String)(implicit publicKey: PublicKey): String = {
    debug("encrypting string: " + msg)
    val encryptedBytes = encrypt(msg.trim getBytes StandardCharsets.UTF_8)
    val res = Base64.getEncoder encodeToString encryptedBytes
    debug("encrypted string: " + res)
    res
  }

  def decrypt(msg: String)(implicit privateKey: PrivateKey): String = {
    debug("decrypting string: " + msg)
    val decryptedBytes = decrypt(Base64.getDecoder decode msg.trim)
    val res = new String(decryptedBytes, StandardCharsets.UTF_8)
    debug("decrypted string: " + res)
    res
  }

  private val random = new SecureRandom()

  def random(strength: Int): RSA = {
    debug(s"Generating key with strength = $strength")
    if (strength % 8 != 0) sys.error("Strength should be dividable by 8")
    var phi = BigInt(0)
    var modulus = BigInt(0)
    while (modulus.toByteArray.length != strength / 8) {
      val p = BigInt.probablePrime(strength / 2, random)
      val q = BigInt.probablePrime(strength / 2, random)
      phi = (p - 1) * (q - 1)
      modulus = p * q
    }
    debug(s"Modulus bytes count is ${modulus.toByteArray.length}")
    val publicKey = BigInt(65537) // this shocked me also
    val privateKey = publicKey.modInverse(phi)
    new RSA(privateKey, publicKey, modulus)
  }


}

case class RSAEncryptor(publicKey: BigInt, modulus: BigInt) extends Encryptor {

  private implicit def _pub: PublicKey = PublicKey(modulus, publicKey)

  override def encrypt(msg: String): String = RSA.encrypt(msg)

}