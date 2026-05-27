package exercises.tradevalidationpipeline

import exercises.tradevalidationpipeline.model.SymbolMarket
import exercises.tradevalidationpipeline.utils.TradeDataUtils.{getSymbolMarkets, symbolMarketResourceName, withFile}
import zio._
import zio.http._
import zio.http.codec.HttpCodecError

object ReferenceDataServer extends ZIOAppDefault {

  private val listOfSymbolMarket: Task[List[SymbolMarket]] = withFile(symbolMarketResourceName)(file => ZIO.attempt(getSymbolMarkets(file)))

  val routes =
    Routes(
      Method.GET / Root     -> handler(Response.text("Reference data service")),
      Method.GET / "symbol" -> handler { (req: Request) =>
        val symbol: Either[HttpCodecError.QueryParamError, String] = req.query[String]("symbol")

        symbol match {
          case Left(error) =>
            ZIO.succeed(
              Response
                .text(s"Invalid or missing query parameter: ${error.getMessage}")
                .status(Status.BadRequest)
            )

          case Right(requestedSymbol) =>
            listOfSymbolMarket
              .map { symbolMarkets =>
                symbolMarkets.find(_.symbol == requestedSymbol) match {
                  case Some(SymbolMarket(symbol, market)) =>
                    Response.json(s"""
                                     |{
                                     |  "symbol": "$symbol",
                                     |  "market": "$market"
                                     |}
                                     |""".stripMargin)

                  case None =>
                    Response
                      .text(s"Symbol not found: $requestedSymbol")
                      .status(Status.NotFound)
                }
              }
              .catchAll { error =>
                ZIO.succeed(
                  Response
                    .text(s"Failed to load reference data: ${error.getMessage}")
                    .status(Status.InternalServerError)
                )
              }
        }
      }
    )

  def run: ZIO[Any with ZIOAppArgs with Scope, Throwable, Unit] =
    for {
      _ <- listOfSymbolMarket.tap(symbolMarkets =>
        ZIO.logInfo(s"Loaded ${symbolMarkets.size} symbol market records")
      )
      _ <- Server.serve(routes).provide(Server.default)
    } yield ()
}