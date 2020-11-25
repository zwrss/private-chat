package com.github.zwrss.privatechat.server


import com.github.zwrss.privatechat.Logging
import com.github.zwrss.privatechat.servlets.ChatServlet
import com.github.zwrss.privatechat.servlets.DecryptServlet
import com.github.zwrss.privatechat.servlets.EncryptServlet
import com.github.zwrss.privatechat.servlets.GenerateKeyServlet
import com.github.zwrss.privatechat.servlets.ManagementServlet
import com.github.zwrss.privatechat.servlets.ResourcesServlet
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.server.session.SessionHandler
import org.eclipse.jetty.servlet.ServletContextHandler
import org.eclipse.jetty.servlet.ServletHolder

import scala.util.Try

object Server extends Logging {

  val resourcesRoute = "/public/*"
  val managementRoute = ""
  val chatRoute = "/chat"
  val encryptRoute = "/encrypt"
  val decryptRoute = "/decrypt"
  val genKeyRoute = "/key"

  def main(args: Array[String]) = {

    val host = args.headOption orElse sys.env.get("HOST") getOrElse "localhost"

    val port = {
      args.headOption.flatMap(_ => args.tail.headOption) orElse sys.env.get("PORT") flatMap (a => Try(a.toInt).toOption) getOrElse 8082
    }

    val server = new Server(port)

    val handler = new ServletContextHandler(ServletContextHandler.SESSIONS)


    handler.setContextPath("/")
    server.setHandler(handler)

    handler.addServlet(new ServletHolder(new ResourcesServlet), resourcesRoute)
    handler.addServlet(new ServletHolder(new ManagementServlet(host, port)), managementRoute)
    handler.addServlet(new ServletHolder(new ChatServlet(host, port, true)), chatRoute)
    handler.addServlet(new ServletHolder(new EncryptServlet), encryptRoute)
    handler.addServlet(new ServletHolder(new DecryptServlet), decryptRoute)
    handler.addServlet(new ServletHolder(new GenerateKeyServlet), genKeyRoute)

    val sessionHandler = new SessionHandler
    sessionHandler.setUsingCookies(true)
    handler.setSessionHandler(sessionHandler)

    server.start()
    info(s"Server started on $host:$port with endpoints: '$managementRoute' and '$chatRoute'")
    server.join()
  }


}