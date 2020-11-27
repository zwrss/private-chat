package com.zwrss.privatechat.server.connection

import com.zwrss.privatechat.cipher.PublicKey
import com.zwrss.privatechat.cipher.RSA
import com.zwrss.privatechat.connection.ConnectionController
import com.zwrss.privatechat.connection.ConnectionControllerBehavior
import com.zwrss.privatechat.message.command.ChatCommand
import com.zwrss.privatechat.message.command.Say
import com.zwrss.privatechat.message.command.Whisper
import com.zwrss.privatechat.message.event.ChatEvent
import com.zwrss.privatechat.message.event.Joined
import com.zwrss.privatechat.message.event.Left
import com.zwrss.privatechat.message.event.Said

object Chat {
  var users: Seq[User] = Seq.empty

  def broadcast(event: ChatEvent): Unit = {
    users.foreach(_.remote write event)
  }
}

case class User(username: String, remote: ConnectionController)

class Chat(username: String, rsa: RSA, remotePublicKey: PublicKey) extends ConnectionControllerBehavior[ChatCommand] {

  override def onEntry(remote: ConnectionController): Unit = {
    if (Chat.users.exists(_.username == username)) sys.error(s"$username already connected!")
    Chat.users :+= User(username, remote)
    remote write[ChatEvent] Said("SYSTEM", "You are now connected to the server!")
    remote write[ChatEvent] Said("SYSTEM", "Remember that messages send to a public chat are visible to the server!")
    remote write[ChatEvent] Said("SYSTEM", "Messages whispered are protected and can be only read by recipient.")
    Chat broadcast Joined(username)
    remote.onClose { () =>
      Chat.users = Chat.users.filterNot(_.remote == remote)
      Chat broadcast Left(username)
    }
  }

  override protected def _handle: PartialFunction[(ConnectionController, ChatCommand), Unit] = {
    case (_, Say(message)) =>
      Chat broadcast Said(username, message)
    case (_, Whisper(username, message)) =>

  }
}
