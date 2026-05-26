package exercises.tradevalidationpipeline

import exercises.tradevalidationpipeline.model.Trade
import exercises.tradevalidationpipeline.utils.TradeDataUtils.{getTrades, resourceName, withFile}
import exercises.tradevalidationpipeline.validation.ValidatorNec
import zio.stream.ZStream
import zio.{Task, ZIO, ZLayer}

trait TradeValidationService {
  def liveTradeValidationStream: ZStream[Any, Throwable, Unit]
}

case class TradeValidationServiceLive(validator: ValidatorNec) extends TradeValidationService {

  private val listOfTrades: Task[List[Trade]] = withFile(resourceName)(file => ZIO.attempt(getTrades(file)))
    override def liveTradeValidationStream: ZStream[Any, Throwable, Unit] = {
      ZStream
        .fromZIO(listOfTrades)
        .flatMap(list => ZStream.fromIterable(list))
        .tap { trade => ZIO.attempt(println(s"Processing $trade")) }
        .map { trade => validator.validate(trade) }
    }
}

object TradeValidationServiceLive {
  val layer: ZLayer[ValidatorNec, Nothing, TradeValidationServiceLive] = ZLayer.fromFunction(TradeValidationServiceLive(_))
}
