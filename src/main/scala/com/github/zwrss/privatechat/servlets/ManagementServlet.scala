package com.github.zwrss.privatechat.servlets

import java.net.URLEncoder
import java.util.UUID

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import org.eclipse.jetty.websocket.api.WebSocketAdapter
import org.eclipse.jetty.websocket.servlet.WebSocketServlet
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory
import play.api.libs.json.Json

import scala.io.Source

class ManagementServlet(host: String, port: Int) extends WebSocketServlet {

  override def doGet(req: HttpServletRequest, resp: HttpServletResponse): Unit = {
    val resource = Source fromURL getClass.getClassLoader.getResource("index.html")
    val html = resource.getLines().mkString("\n").replaceAllLiterally("__HOST__", host).
      replaceAllLiterally("__PORT__", port.toString)
    resp setContentType "text/html"
    resp setStatus 200
    resp.getWriter println html
  }

  override def configure(factory: WebSocketServletFactory): Unit = {
    factory.register(classOf[ManagementSocket])
    factory setCreator { (_, _) => new ManagementSocket(UUID.randomUUID()) }
  }

  class ManagementSocket(id: UUID) extends WebSocketAdapter {
    override def onWebSocketText(message: String): Unit = {
//      val json = Json parse message
//      val roomId: String = id.toString
//      val code: String = (json \ "code").as[String]
//      val username: String = (json \ "username").as[String]
//      val crypto = new AES(code)
//      val token = crypto.encrypt(
//        Json.obj(
//          "roomId" -> roomId,
//          "code" -> code,
//          "username" -> username
//        ).toString
//      )
//      getRemote.sendString(s"http://$host:$port/chat?token=${URLEncoder.encode(token, "utf-8")}")
    }
  }

}