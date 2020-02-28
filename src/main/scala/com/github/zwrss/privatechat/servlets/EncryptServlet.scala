package com.github.zwrss.privatechat.servlets

import java.nio.charset.StandardCharsets
import java.util.Base64

import com.github.zwrss.privatechat.Logging
import com.github.zwrss.privatechat.cipher.CeasarsCipher
import com.github.zwrss.privatechat.cipher.PublicKey
import com.github.zwrss.privatechat.cipher.RSA
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import scala.util.Try

class EncryptServlet extends HttpServlet with Logging {

  override def doGet(req: HttpServletRequest, resp: HttpServletResponse): Unit = {
    resp setStatus 200
    resp.setHeader("Content-Type", "text/plain; charset=utf-8")
    Option(req getParameter "cipher") match {
      case Some("ceasar") =>
        for {
          message <- Option(req getParameter "message")
          base64 <- Try((req getParameter "base64").toBoolean).toOption
          shift <- Try((req getParameter "shift").toInt).toOption
        } {
          val cipher = CeasarsCipher(shift, if (base64) CeasarsCipher.Base64Mode else CeasarsCipher.AlphanumericMode)
          val encrypted = cipher encrypt message
          resp.getWriter println encrypted
        }
      case Some("rsa") =>
        for {
          message <- Option(req getParameter "message")
          modulus <- Try(BigInt(req getParameter "modulus")).toOption
          publicKey <- Try(BigInt(req getParameter "publicKey")).toOption
        } {
          val encrypted = RSA.encrypt(message)(PublicKey(modulus, publicKey))
          resp.getWriter println encrypted
        }
      case Some("base64") =>
        Option(req getParameter "message").foreach { message =>
          val encrypted = Base64.getEncoder.encodeToString(message.getBytes(StandardCharsets.UTF_8))
          resp.getWriter println encrypted
        }
      case _ => // do nothing
    }
  }

  override def doPost(req: HttpServletRequest, resp: HttpServletResponse): Unit = {
    doGet(req, resp)
  }
}