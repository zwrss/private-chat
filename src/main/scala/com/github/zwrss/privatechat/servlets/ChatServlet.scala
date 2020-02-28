package com.github.zwrss.privatechat.servlets

import java.net.URLEncoder
import java.util.Base64

import com.github.zwrss.privatechat.Logging
import com.github.zwrss.privatechat.cipher.Encryptor
import com.github.zwrss.privatechat.domain.ChatRooms
import com.github.zwrss.privatechat.domain.ChatUser
import com.github.zwrss.privatechat.servlets.ChatServlet.ChatSocket
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import org.eclipse.jetty.websocket.api.RemoteEndpoint
import org.eclipse.jetty.websocket.api.Session
import org.eclipse.jetty.websocket.api.WebSocketAdapter
import org.eclipse.jetty.websocket.servlet.WebSocketServlet
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory
import play.api.libs.json.Json
import play.api.libs.json.Writes
import sun.security.rsa.RSAPublicKeyImpl

import scala.collection.convert.ImplicitConversions.`iterable AsScalaIterable`
import scala.io.Source


class ChatServlet(host: String, port: Int, enableEncryption: Boolean = true) extends WebSocketServlet {

  override def doGet(req: HttpServletRequest, resp: HttpServletResponse): Unit = {
    val token = URLEncoder.encode(req.getParameter("token"), "utf-8")
    val resource = Source fromURL getClass.getClassLoader.getResource("chat.html")
    val html = resource.getLines().mkString("\n").replaceAllLiterally("__TOKEN__", token).
      replaceAllLiterally("__HOST__", host).replaceAllLiterally("__PORT__", port.toString)
    resp setContentType "text/html"
    resp setStatus 200
    resp.getWriter.println(html)
  }

  def configure(factory: WebSocketServletFactory): Unit = {
    factory.getPolicy.setIdleTimeout(10000000)
    factory.register(classOf[ChatSocket])
    factory.setCreator { (request, _) =>
      val token = Option(request.getParameterMap.get("token")).flatMap(_.headOption)
      new ChatSocket(token.get, enableEncryption)
    }
  }

}

object ChatServlet {

  private val rooms = new ChatRooms

  private case class Msg(user: Option[String], message: String)

  private implicit def writer: Writes[Msg] = Json.using[Json.WithDefaultValues].writes[Msg]

  class ChatSocket(token: String, enableEncryption: Boolean) extends WebSocketAdapter with Logging {

    def sendMessage(recipient: Either[ChatUser, (Encryptor, RemoteEndpoint)], user: Option[String], message: String): Unit = {
      val messageJson = Json.toJson(Msg(user, message)).toString
      recipient match {
        case Left(chatUser) => chatUser sendMessage messageJson
        case Right((encryptor, remote)) => remote sendString (encryptor encrypt messageJson)
      }
    }

    private var behaviour: Behaviour = new Initial

    private def changeBehaviour(b: Behaviour): Unit = {
      behaviour.onExit()
      behaviour = b
      behaviour.onEnter()
    }

    override def onWebSocketConnect(session: Session): Unit = {
      super.onWebSocketConnect(session)
      behaviour.onEnter()
    }

    override def onWebSocketClose(statusCode: Int, reason: String): Unit = {
      super.onWebSocketClose(statusCode, reason)
      behaviour.onExit()
    }

    override def onWebSocketText(message: String): Unit = {
      super.onWebSocketText(message)
      behaviour.onMessage(message)
    }

    private trait Behaviour {
      def onEnter(): Unit = {}

      def onExit(): Unit = {}

      def onMessage(message: String): Unit = {}
    }

    private class Initial extends Behaviour {
//
//      private val (privateKey, publicKey) = RSA.generateKeyPair
//
//      override def onEnter(): Unit = if (enableEncryption) {
//        val keyBytes = publicKey.getEncoded
//        val base64key = Base64.getEncoder.encodeToString(keyBytes)
//        getRemote.sendString(base64key)
//      } else {
//        changeBehaviour(new Unauthenticated(x => Option(x), x => x))
//      }
//
//      override def onMessage(message: String): Unit = {
//        val keyBytes = Base64.getDecoder.decode(message.replaceAllLiterally(" ", ""))
//        val clientKey = new RSAPublicKeyImpl(keyBytes)
//        val encryptor = RSA.encryptor(clientKey)
//        val decryptor = RSA.decryptor(privateKey)
//        changeBehaviour(new Unauthenticated(decryptor, encryptor))
//      }
    }

//    private class Unauthenticated(decryptor: Decryptor, encryptor: Encryptor) extends Behaviour {
//
//      override def onEnter(): Unit = {
//        sendMessage(Right(encryptor -> getRemote), user = None, "Enter the secret code, please...")
//      }
//
//      case class AuthenticationMessage(username: String, roomId: String)
//
//      override def onMessage(message: String): Unit = {
//        val crypto = new AES(decryptor forceDecrypt message)
//        crypto.decrypt(token) match {
//          case Some(decrypted) =>
//            val m = (Json parse decrypted).as[AuthenticationMessage](Json.reads)
//            val user = new ChatUser(m.username, getRemote, encryptor)
//            rooms.add(m.roomId, user)
//            changeBehaviour(new Authenticated(decryptor, encryptor, m.roomId, user))
//          case _ =>
//            onEnter()
//        }
//      }
//
//    }
//
//    private class Authenticated(decryptor: Decryptor, encryptor: Encryptor, roomId: String, user: ChatUser) extends Behaviour {
//
//      private def broadcast(sender: Option[String], message: String): Unit = {
//        rooms.get(roomId).getUsers.foreach { recipient =>
//          sendMessage(Left(recipient), sender, message)
//        }
//      }
//
//      override def onEnter(): Unit = {
//        broadcast(sender = None, s"User ${user.username} connected to the chat!")
//        sendMessage(Left(user), user = None, s"Welcome to the private chat! There are ${rooms.get(roomId).getUsers.map(_.username).mkString(" and ")} already connected!")
//      }
//
//      override def onExit(): Unit = {
//        rooms.remove(roomId, user)
//      }
//
//      override def onMessage(message: String): Unit = {
//        broadcast(Option(user.username), decryptor forceDecrypt message)
//      }
//
//    }
//
  }

}