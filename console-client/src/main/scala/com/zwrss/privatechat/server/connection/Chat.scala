package com.zwrss.privatechat.server.connection

import com.zwrss.privatechat.connection.ConnectionController
import com.zwrss.privatechat.connection.ConnectionControllerBehavior
import com.zwrss.privatechat.message.ChatMessage

object Chat {
  var users: Seq[User] = Seq.empty
}

case class User(username: String, remote: ConnectionController)

class Chat(username: String) extends ConnectionControllerBehavior[ChatMessage] {

  override def onEntry(remote: ConnectionController): Unit = {
    if (Chat.users.exists(_.username == username)) sys.error(s"$username already connected!")
    Chat.users :+= User(username, remote)
    remote.onClose { () =>
      Chat.users = Chat.users.filterNot(_.remote == remote)
    }
  }

  override protected def _handle: PartialFunction[(ConnectionController, ChatMessage), Unit] = {
    case (_, message) =>
      Chat.users.foreach(_.remote write message)
  }
}
