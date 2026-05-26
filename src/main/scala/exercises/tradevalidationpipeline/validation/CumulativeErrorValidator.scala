package exercises.tradevalidationpipeline.validation

import exercises.tradevalidationpipeline.model._
// sealed trait convenient for mock testing if Validator was dependent on external services
sealed trait CumulativeErrorValidator {
  def validate(trade: Trade): Either[List[ValidationError], Trade]
}

class CumulativeErrorValidatorImpl extends CumulativeErrorValidator {
  override def validate(trade: Trade): Either[List[ValidationError], Trade] = {
    val errors: List[ValidationError] = List(
      if (trade.quantity <= 0) Some(InvalidQuantity) else None,
      if (trade.price <= 0) Some(InvalidPrice) else None,
      if (!trade.symbol.exists(_.trim.nonEmpty)) Some(EmptySymbol) else None
    ).flatten

    if (errors.isEmpty) Right(trade) else Left(errors)
  }
}
