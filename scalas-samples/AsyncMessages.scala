#!/usr/bin/env scalas
// or ./scalas AsyncMessages.scala
/***
scalaVersion := "2.11.7"
resolvers += "sonatype releases" at "https://oss.sonatype.org/content/repositories/releases"
libraryDependencies ++= Seq(
  "org.skinny-framework" %% "skinny-micro-server" % "0.9.9",
  "org.skinny-framework" %% "skinny-micro-json"   % "0.9.9"
) 
*/
import skinny.micro._
import skinny.micro.contrib._
import scala.concurrent._

case class Message(id: Long, text: String)

object Messages {
  private[this] val messages = Seq(
    Message(1, "Effective Java"),
    Message(2, "Scala Programming"),
    Message(3, "Reactive Streams"),
    Message(4, "Ruby on Rails"),
    Message(5, "Deep Learning")
  )

  def search(keyword: Option[String])(implicit ctx: ExecutionContext): Future[Seq[Message]] = Future {
    keyword match {
      case Some(k) => messages.filter { m => m.text.matches(s".*$k.*") }
      case _ => Seq.empty
    }
  }
}
object AsyncMessagesApp extends AsyncWebApp with JSONSupport {
  post("/messages/search") { implicit ctx =>
    Messages.search(params.get("keyword"))
      .map(messages => toJSONString(messages))
  }
}

WebServer.mount(AsyncMessagesApp).port(4567).start()

println
println("Try: curl -v -XPOST 'localhost:4567/messages/search' -d'keyword=e'")
println
