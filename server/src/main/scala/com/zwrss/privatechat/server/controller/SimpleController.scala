package com.zwrss.privatechat.server.controller

import com.zwrss.privatechat.message.SimpleMessage

object SimpleController extends Controller[SimpleMessage] {
  override protected def _handle: PartialFunction[(SocketWrapper, SimpleMessage), Unit] = {
    case (wrapper, message) => println(message.message)
  }
}