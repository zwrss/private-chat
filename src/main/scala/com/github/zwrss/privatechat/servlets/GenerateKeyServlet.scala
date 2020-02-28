package com.github.zwrss.privatechat.servlets

import com.github.zwrss.privatechat.cipher.RSA
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import org.eclipse.jetty.servlet.DefaultServlet
import play.api.libs.json.Json

import scala.util.Try

class GenerateKeyServlet extends DefaultServlet {

  override def doGet(req: HttpServletRequest, resp: HttpServletResponse): Unit = {
    resp setStatus 200
    val strength = Option(req getParameter "strength") flatMap (x => Try(x.toInt).toOption) getOrElse 1024
    val rsa = RSA.random(strength)
    val key = Json.obj(
      "modulus" -> rsa.modulus.toString(),
      "privateKey" -> rsa.privateKey.toString(),
      "publicKey" -> rsa.publicKey.toString()
    )
    resp.getWriter.println(key.toString)

  }

}