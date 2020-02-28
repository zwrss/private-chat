package com.github.zwrss.privatechat.domain

import scala.collection.concurrent.TrieMap

class ChatRoom {

  private val users: TrieMap[String, ChatUser] = TrieMap.empty

  def add(user: ChatUser): Unit = users.getOrElseUpdate(user.username, user)

  def remove(user: ChatUser): Unit = users remove user.username

  def isEmpty: Boolean = users.isEmpty

  def broadcast(user: ChatUser, message: String): Unit = users.values.foreach { u =>
    u.sendMessage(s"${user.username}: $message")
  }

  def getUsers: Seq[ChatUser] = users.values.toSeq.sortBy(_.username)

}
