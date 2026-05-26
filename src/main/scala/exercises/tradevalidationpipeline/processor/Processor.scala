package exercises.tradevalidationpipeline.processor

import exercises.tradevalidationpipeline.model.Trade
import exercises.tradevalidationpipeline.validation.Validator

class Processor(validator: Validator) {
  def process(trades: Seq[Trade]): Either[String, Seq[Trade]] = {
    //val validationResults: Seq[Either[TradeError, Trade]] = trades.map(validator.validate)
    //Rather than using validate twice I would create a case class TradesWithValidationResults to capture trades and their errors for logging purposes and extraction
    //Also I am aware that validated doesn't accumulate errors so it fails fast
    val (validated, invalidated)                          = trades.partition(trade => validator.validate(trade).isRight)
    trades match {
      case trades if trades.isEmpty       => Left("Error. Empty sequence of trades provided")
      case trades if invalidated.nonEmpty =>
        // val nonValidTrades = trades.collect { case trade => validator.validate(trade).isLeft; trade  }
        val invalidatedTrades = trades
          .filter(trade => invalidated.contains(trade))
          .map(trade => (trade, validator.validate(trade)))
        Left(s"Error. Trade sequence didn't pass validation: ${invalidatedTrades.mkString(",")}")
      case _ => Right(validated)
    }
  }
}
