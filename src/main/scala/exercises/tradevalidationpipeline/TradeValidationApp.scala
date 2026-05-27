package exercises.tradevalidationpipeline

import exercises.tradevalidationpipeline.model.Trade
import exercises.tradevalidationpipeline.processor.{CumulativeErrorProcessor, Processor, ProcessorNec, TypeClassProcessor}
import exercises.tradevalidationpipeline.validation.{CumulativeErrorValidatorImpl, ValidatorImpl, ValidatorNec}
import exercises.tradevalidationpipeline.validation.implicitexample.TradeValidator.tradeValidator

object TradeValidationApp extends App {

  val trades1 = Seq(
    Trade(id = "1", symbol = "s1", quantity = 2, price = 10),
    Trade(id = "1", symbol = "s1", quantity = 2, price = 10)
  )
  val trades2 = Seq(
    Trade(id = "1", symbol = "s1", quantity = -2, price = -10),
    Trade(id = "2", symbol = "s2", quantity = 2, price = 10),
    Trade(id = "3", symbol = "", quantity = -4, price = 10)
  )

  val trades3 = Seq(
    Trade(id = "1", symbol = "s1", quantity = 2, price = 10),
    Trade(id = "2", symbol = "s2", quantity = 3, price = 11)
  )

  val validatorImpl        = new ValidatorImpl
  val tradeProcessor       = new Processor(validatorImpl)
  val tradeProcessorResult = tradeProcessor.process(trades2)
  println(s"TradeProcessor result: $tradeProcessorResult")

  val cumulativeErrorValidator            = new CumulativeErrorValidatorImpl
  val tradeCumulativeErrorProcessor       = new CumulativeErrorProcessor(cumulativeErrorValidator)
  val tradeCumulativeErrorProcessorResult = tradeCumulativeErrorProcessor.process(trades2)
  println(s"tradeCumulativeErrorProcessorResult result: $tradeCumulativeErrorProcessorResult")

  val necProcessor: ProcessorNec = new ProcessorNec(ValidatorNec)
  val processorNecResult         = necProcessor.process(trades3)
  println(s"TradeProcessorNec result: $processorNecResult")

  val typeClassProcessor: TypeClassProcessor = new TypeClassProcessor()
  val typeClassProcessorResult               = typeClassProcessor.process(trades2)
  println(s"typeClassProcessorResult result: $typeClassProcessorResult")
}
