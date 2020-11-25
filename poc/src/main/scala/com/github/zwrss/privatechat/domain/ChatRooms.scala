package com.github.zwrss.privatechat.domain

import scala.collection.concurrent.TrieMap

class ChatRooms {

  private val rooms: TrieMap[String, ChatRoom] = TrieMap.empty

  def add(roomId: String, user: ChatUser): Unit = synchronized {
    rooms.getOrElseUpdate(roomId, new ChatRoom).add(user)
  }

  def remove(roomId: String, user: ChatUser): Unit = synchronized {
    rooms.get(roomId) foreach { room =>
      room.remove(user)
      if (room.isEmpty) rooms.remove(roomId)
    }
  }

  def get(i: String): ChatRoom = rooms(i)

  def isEmpty: Boolean = rooms.isEmpty

}
