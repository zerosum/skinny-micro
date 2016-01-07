package org.scalatra

import org.scalatra.test.scalatest.ScalatraFunSuite
import skinny.micro.SkinnyMicroServlet

object RouteRegistryTestServlet extends SkinnyMicroServlet {
  get("/foo") {}
  post("/foo/:bar") {}
  put("""^/foo.../bar$""".r) {}
  get("/nothing", false) {}
  get(false) {}

  def renderRouteRegistry: String = routes.toString
}

class RouteRegistryTest extends ScalatraFunSuite {

  test("route registry string representation contains the entry points") {
    RouteRegistryTestServlet.renderRouteRegistry should equal(Seq(
      "GET\t/foo",
      "GET\t/nothing [Boolean Guard]",
      "GET\t[Boolean Guard]",
      "POST\t/foo/:bar",
      "PUT\t^/foo.../bar$"
    ).mkString("\n") + "\n")
  }
}
