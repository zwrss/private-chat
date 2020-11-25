package com.github.zwrss.privatechat.servlets

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import org.eclipse.jetty.servlet.DefaultServlet

import scala.io.Source

class ResourcesServlet extends DefaultServlet {

  override def doGet(req: HttpServletRequest, resp: HttpServletResponse): Unit = {
    val file = req.getRequestURI.stripPrefix("/")
    val resource = Source.fromResource(file)
    resp setStatus 200
    resource.getLines foreach resp.getWriter.println
  }
}