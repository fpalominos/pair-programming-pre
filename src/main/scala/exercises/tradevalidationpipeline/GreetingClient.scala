package exercises.tradevalidationpipeline

import zio._
import zio.http._

object GreetingClient extends ZIOAppDefault {

  val app =
    for {
      client   <- ZIO.serviceWith[Client](_.host("localhost").port(8080))
      request  =  Request.get("greet").addQueryParam("name", "John")
      response <- client.batched(request)
      _        <- response.body.asString.debug("Response")
    } yield ()

  def run = app.provide(Client.default)
}
