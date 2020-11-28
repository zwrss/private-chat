package com.zwrss.privatechat.server.connection

import com.zwrss.privatechat.cipher.Encryptor
import com.zwrss.privatechat.cipher.PublicKey
import com.zwrss.privatechat.cipher.RSA
import com.zwrss.privatechat.cipher.RSAEncryptor
import com.zwrss.privatechat.connection.ConnectionController
import com.zwrss.privatechat.connection.ConnectionControllerBehavior
import com.zwrss.privatechat.message.command.ChatCommand
import com.zwrss.privatechat.message.command.GetPubKey
import com.zwrss.privatechat.message.command.Say
import com.zwrss.privatechat.message.command.Whisper
import com.zwrss.privatechat.message.event.ChatEvent
import com.zwrss.privatechat.message.event.Joined
import com.zwrss.privatechat.message.event.Left
import com.zwrss.privatechat.message.event.Said
import com.zwrss.privatechat.message.event.SharedPubKey
import com.zwrss.privatechat.message.event.Whispered

object Chat {
  var users: Seq[User] = Seq.empty

  def broadcast(createEvent: User => ChatEvent): Unit = {
    users.foreach { user =>
      val event = createEvent(user)
      user.remote write event
    }
  }

  def broadcast(event: ChatEvent): Unit = broadcast(_ => event)
}

case class User(username: String, remote: ConnectionController, publicKey: PublicKey) {
  lazy val encryptor: Encryptor = RSAEncryptor(publicKey)
}

class Chat(username: String, rsa: RSA, remotePublicKey: PublicKey) extends ConnectionControllerBehavior[ChatCommand] {

  private lazy val encryptor: Encryptor = RSAEncryptor(remotePublicKey)

  override def onEntry(remote: ConnectionController): Unit = {
    if (Chat.users.exists(_.username == username)) sys.error(s"$username already connected!")
    Chat.users :+= User(username, remote, remotePublicKey)
    remote write[ChatEvent] Said("SYSTEM", encryptor encrypt "You are now connected to the server!")
    remote write[ChatEvent] Said("SYSTEM", encryptor encrypt "Remember that messages send to a public chat are visible to the server!")
    remote write[ChatEvent] Said("SYSTEM", encryptor encrypt "Messages whispered are protected and can be only read by recipient.")
    Chat broadcast Joined(username)
    remote.onClose { () =>
      Chat.users = Chat.users.filterNot(_.remote == remote)
      Chat broadcast Left(username)
    }
  }

  override protected def _handle: PartialFunction[(ConnectionController, ChatCommand), Unit] = {
    case (_, Say(message)) =>
      Chat.broadcast { user =>
        Said(username, user.encryptor encrypt (rsa decrypt message))
      }
    case (_, Whisper(u, message)) =>
      Chat.users.find(_.username == u).foreach { user =>
        user.remote write[ChatEvent] Whispered(username, message)
      }
    case (remote, GetPubKey(username)) =>
      Chat.users.find(_.username == username).foreach { user =>
        remote write[ChatEvent] SharedPubKey(username, user.publicKey)
      }
  }
}
