package exercises.tradevalidationpipeline

import exercises.tradevalidationpipeline.model.{Trade, ValidationError}
import exercises.tradevalidationpipeline.query.RefDataService
import exercises.tradevalidationpipeline.utils.TradeDataUtils.{getTrades, tradeResourceName, withFile}
import exercises.tradevalidationpipeline.validation.ValidatorNec
import zio.stream.ZStream
import zio.{Task, ZIO, ZLayer}

trait TradeValidationService {
  def liveTradeValidationStream: ZStream[Any, Throwable, Unit]
  def liveTradeValidationStreamParallel: ZStream[Any, Throwable, Either[List[ValidationError], Trade]]
  def liveTradeValidationStreamValidationFromService: ZStream[Any, Throwable, Either[ValidationError, Trade]]
}

case class TradeValidationServiceLive(validator: ValidatorNec, refDataService: RefDataService) extends TradeValidationService {

  private val listOfTrades: Task[List[Trade]] = withFile(tradeResourceName)(file => ZIO.attempt(getTrades(file)))

  override def liveTradeValidationStream: ZStream[Any, Throwable, Unit] = {
    ZStream
      .fromZIO(listOfTrades)
      .flatMap(list => ZStream.fromIterable(list))
      .tap { trade => ZIO.attempt(println(s"Processing $trade")) }
      .map { trade => validator.validate(trade) }
  }

  def liveTradeValidationStreamParallel: ZStream[Any, Throwable, Either[List[ValidationError], Trade]] = {
    ZStream
      .fromZIO(listOfTrades)
      .flatMap(list => ZStream.fromIterable(list))
      .tap { trade => ZIO.attempt(println(s"Processing $trade")) }
      .mapZIOParUnordered(64) { trade => validator.validateUIO(trade) }
      .tap {
        case Left(error) =>
          ZIO.logWarning(s"Trade validation failed: $error")

        case Right(trade) =>
          ZIO.logInfo(s"Trade validation succeeded: ${trade.id}")
      }
  }

  def liveTradeValidationStreamValidationFromService: ZStream[Any, Throwable, Either[ValidationError, Trade]] = {
    ZStream
      .fromZIO(listOfTrades)
      .flatMap(list => ZStream.fromIterable(list))
      .tap { trade =>
        ZIO.logInfo(s"Processing trade: ${trade.id}")
      }
      .mapZIOParUnordered(64) { trade =>
        validator
          .validateFromService(trade, refDataService)
          .map(result => trade -> result)
      }
      .tap {
        case (trade, Left(error)) =>
          ZIO.logWarning(
            s"Trade validation failed: tradeId=${trade.id}, symbol=${trade.symbol}, error=$error"
          )
        case (trade, Right(_)) =>
          ZIO.logInfo(
            s"Trade validation succeeded: tradeId=${trade.id}, symbol=${trade.symbol}"
          )
      }
      .map {
        case (_, result) => result
      }
  }
}

object TradeValidationServiceLive {
  val layer: ZLayer[ValidatorNec with RefDataService, Nothing, TradeValidationServiceLive] = ZLayer.fromFunction(TradeValidationServiceLive(_,_))
}
