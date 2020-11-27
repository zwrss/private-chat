package com.zwrss.privatechat.consoleclient.connection

import com.zwrss.privatechat.connection.ConnectionController
import com.zwrss.privatechat.connection.ConnectionControllerBehavior
import play.api.libs.json.JsValue

object Waiting extends ConnectionControllerBehavior[Nothing]()((_: JsValue) => ???) {
  override protected def _handle: PartialFunction[(ConnectionController, Nothing), Unit] = PartialFunction.empty
}