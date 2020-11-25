package com.github.zwrss.privatechat.domain

import com.github.zwrss.privatechat.cipher.Encryptor
import org.eclipse.jetty.websocket.api.RemoteEndpoint

class ChatUser(val username: String, remoteEndpoint: RemoteEndpoint, encryptor: Encryptor) {

  def sendMessage(message: String): Unit = {
    remoteEndpoint.sendString(encryptor encrypt message)
  }

}