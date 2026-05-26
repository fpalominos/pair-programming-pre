package exercises.tradevalidationpipeline

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

  def run: ZIO[Any, Throwable, Unit] = program.provide(ValidatorNecService.layer, TradeValidationServiceLive.layer)

}




