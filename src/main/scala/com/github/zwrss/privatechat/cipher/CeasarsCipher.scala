package com.github.zwrss.privatechat.cipher

import java.nio.charset.StandardCharsets
import java.security.SecureRandom
import java.util.Base64

import com.github.zwrss.privatechat.Logging

case class CeasarsCipher(shift: Int, mode: CeasarsCipher.Mode) extends Cipher {

  override def encrypt(msg: String): String = CeasarsCipher.encrypt(msg)(shift, mode)

  override def decrypt(msg: String): String = CeasarsCipher.decrypt(msg)(shift, mode)

}

object CeasarsCipher extends Logging {

  val base64Chars = Seq('A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R',
    'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o',
    'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/')

  val alphanumericChars = Seq('A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R',
    'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o',
    'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9')

  trait Mode

  case object Base64Mode extends Mode

  case object AlphanumericMode extends Mode

  case class CustomAlphabetMode(chars: Seq[Char]) extends Mode

  def encrypt(msg: String)(shift: Int, mode: Mode = AlphanumericMode): String = {
    debug(s"encrypting $msg")
    val (encoded, alphabet) = mode match {
      case Base64Mode => (Base64.getEncoder encodeToString msg.trim.getBytes(StandardCharsets.UTF_8)) -> base64Chars
      case CustomAlphabetMode(abet) => msg.trim -> abet
      case _ => msg.trim -> alphanumericChars
    }
    debug(s"encrypting encoded $encoded")
    val encrypted = encoded.map {
      case c if alphabet contains c =>
        val originalIndex = alphabet indexOf c
        val newIndex = (originalIndex + shift) % alphabet.size
        alphabet(newIndex)
      case c =>
        c
    }
    debug(s"encrypted $encrypted")
    encrypted
  }

  def decrypt(msg: String)(shift: Int, mode: Mode): String = {
    debug(s"decrypting $msg")
    val alphabet = mode match {
      case Base64Mode => base64Chars
      case CustomAlphabetMode(abet) => abet
      case _ => alphanumericChars
    }
    val encoded: String = msg.trim.map {
      case c if alphabet contains c =>
        val originalIndex = alphabet indexOf c
        val newIndex = (alphabet.size + originalIndex - shift) % alphabet.size
        alphabet(newIndex)
      case c =>
        c
    }
    debug(s"decrypting encoded $encoded")
    val decrypted = mode match {
      case Base64Mode => new String(Base64.getDecoder decode encoded, StandardCharsets.UTF_8)
      case _ => encoded
    }
    debug(s"decrypted $decrypted")
    decrypted
  }

  def random(): CeasarsCipher = CeasarsCipher(new SecureRandom().nextInt(alphanumericChars.size), AlphanumericMode)

}