package exercises.tradevalidationpipeline

import zio.http.Client
import exercises.tradevalidationpipeline.query.RefDataServiceLive
import exercises.tradevalidationpipeline.validation.ValidatorNecService
import zio.{ZIO, ZIOAppDefault}

object StreamWithTradeValidationApp extends ZIOAppDefault {
/*  override def run: ZIO[Any, Throwable, Unit] =
    streamWithTradeValidation.runDrain*/

  val program: ZIO[TradeValidationService, Throwable, Unit] =
  {
    for {
      service <- ZIO.service[TradeValidationService]
      _ <- service.liveTradeValidationStream.runDrain
    } yield ()
  }

  val programParallel: ZIO[TradeValidationService, Throwable, Unit] =
  {
    for {
      service <- ZIO.service[TradeValidationService]
      _ <- service.liveTradeValidationStreamParallel.runDrain
    } yield ()
  }

  val programParallelFromService: ZIO[TradeValidationService, Throwable, Unit] =
  {
    for {
      service <- ZIO.service[TradeValidationService]
      _ <- service.liveTradeValidationStreamValidationFromService.runDrain
    } yield ()
  }

  //def run: ZIO[Any, Throwable, Unit] = programParallelFromService.provide(ValidatorNecService.layer, RefDataServiceLive.layer, TradeValidationServiceLive.layer)

  def run: ZIO[Any, Throwable, Unit] =
    programParallelFromService.provide(
      Client.default,
      ValidatorNecService.layer,
      RefDataServiceLive.layer,
      TradeValidationServiceLive.layer
    )
}




