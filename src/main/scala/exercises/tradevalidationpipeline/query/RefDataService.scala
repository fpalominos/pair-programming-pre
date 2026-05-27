package exercises.tradevalidationpipeline.query

import exercises.tradevalidationpipeline.model.SymbolMarket
import zio._
import zio.http._
import zio.json._

trait RefDataService {
  def fetchSymbol(symbol: String): Task[SymbolMarket]
}

case class RefDataServiceLive(client: Client) extends RefDataService {

  private def refDataClient: Client =
    client.host("localhost").port(8080)

  private def symbolRequest(symbol: String): Request =
    Request
      .get("symbol")
      .addQueryParam("symbol", symbol)

  override def fetchSymbol(symbol: String): Task[SymbolMarket] =
    refDataClient
      .batched(symbolRequest(symbol))
      .flatMap(decodeSymbolMarketResponse(symbol, _))

  private def decodeSymbolMarketResponse(requestedSymbol: String, response: Response): Task[SymbolMarket] =
    response.body.asString.flatMap { body =>
      if (response.status.isSuccess) {
        ZIO.fromEither(body.fromJson[SymbolMarket])
          .mapError(error =>
            new RuntimeException(
              s"Failed to decode reference data response for symbol '$requestedSymbol': $error. Body: $body"
            )
          )
      } else {
        ZIO.fail(
          new RuntimeException(
            s"Reference data lookup failed for symbol '$requestedSymbol'. Status: ${response.status}. Body: $body"
          )
        )
      }
    }
}

object RefDataServiceLive {
  val layer: ZLayer[Client, Nothing, RefDataService] =
    ZLayer.fromFunction(RefDataServiceLive(_))
}