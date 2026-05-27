package exercises.tradevalidationpipeline.utils

import exercises.tradevalidationpipeline.model.{SymbolMarket, Trade}
import zio.{ZIO, _}

import java.io.IOException
import scala.io.{BufferedSource, Source}

object TradeDataUtils {

  val tradeResourceName        = "trade_data.txt"
  val symbolMarketResourceName = "symbol_market_data.txt"

  def openFile(name: String): IO[IOException, BufferedSource] =
    ZIO.attemptBlockingIO(Source.fromResource(name))

  def closeFile(bufferedSourceFile: BufferedSource): ZIO[Any, Nothing, Unit] =
    ZIO.succeedBlocking(bufferedSourceFile.close())

  def withFile[A](name: String)(useFile: BufferedSource => Task[A]): Task[A] =
    ZIO.acquireReleaseWith(openFile(name))(closeFile)(useFile)

  def getTrades(
      bufferedSourceFile: BufferedSource
  ): List[Trade] = {

    def getTrade: Iterator[Trade] = for {
      line <- bufferedSourceFile
        .getLines()
        .filter(incomingString => !incomingString.contains("id"))
      list     = line.split(",")
      id       = list.head
      symbol   = list(1)
      quantity = list(2)
      price    = list(3)
      trade    = Trade(id, Option(symbol), quantity.toInt, price.toDouble)
    } yield trade

    getTrade.toList
  }

  def getSymbolMarkets(
      bufferedSourceFile: BufferedSource
  ): List[SymbolMarket] = {

    def getSymbolMarket: Iterator[SymbolMarket] = for {
      line <- bufferedSourceFile
        .getLines()
        .filter(incomingString => !incomingString.contains("symbol"))
      list         = line.split(",")
      symbol       = list.head
      market       = list(1)
      symbolMarket = SymbolMarket(symbol, market)
    } yield symbolMarket

    getSymbolMarket.toList
  }
}
