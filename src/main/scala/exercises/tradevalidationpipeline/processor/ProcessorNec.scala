package exercises.tradevalidationpipeline.processor

import cats.data.NonEmptyChainImpl.Type
import cats.data.Validated.{Invalid, Valid}
import exercises.tradevalidationpipeline.model.{Trade, ValidationError}
import exercises.tradevalidationpipeline.validation.ValidatorNec
import exercises.tradevalidationpipeline.validation.ValidatorNec.ValidationResult

class ProcessorNec(validator: ValidatorNec) {
  def process(trades: Seq[Trade]): Either[String, Seq[Trade]] = {
    val validationResults: Seq[(Trade, ValidationResult[Trade])] = trades.map(trade => trade -> validator.validate(trade))
    println(validationResults)
    val errors: Seq[(Trade, Type[ValidationError])] = validationResults.collect { case (trade, Invalid(e)) => (trade, e) }
    if (errors.nonEmpty) {
      Left(s"The following trades didn't pass validation: $errors")
    } else {
      val validated: Seq[Trade] = validationResults.collect{
        case (_, Valid(trade)) => trade
      }
      Right(validated)
    }
  }
}
