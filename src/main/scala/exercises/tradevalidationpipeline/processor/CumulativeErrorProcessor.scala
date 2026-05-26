package exercises.tradevalidationpipeline.processor

import exercises.tradevalidationpipeline.model.{Trade, ValidationError}
import exercises.tradevalidationpipeline.validation.CumulativeErrorValidator

class CumulativeErrorProcessor(validator: CumulativeErrorValidator) {
  def process(trades: Seq[Trade]): Either[String, Seq[Trade]] = {
    if (trades.isEmpty) {
      Left("Error. Empty sequence of trades provided")
    }
    else {
      val results: Seq[(Trade, Either[List[ValidationError], Trade])] = trades.map(trade => trade -> validator.validate(trade))
      val invalid                                                =
        results.collect { case (trade, Left(errors)) =>
          trade -> errors
        }
      if (invalid.nonEmpty)
        Left(s"Invalid trades: $invalid")
      else {
        Right(results.collect { case (trade, Right(_)) =>
          trade
        })
      }
    }
  }
}
