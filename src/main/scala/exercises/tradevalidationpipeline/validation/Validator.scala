package exercises.tradevalidationpipeline.validation

import exercises.tradevalidationpipeline.model.{EmptySymbol, InvalidPrice, InvalidQuantity, Trade, ValidationError}
import zio.{ULayer, ZLayer}
// sealed trait convenient for mock testing if Validator was dependent on external services
trait Validator {
  def validate(trade: Trade): Either[ValidationError, Trade]
  def validateQuantity(trade: Trade): Either[ValidationError, Trade]
  def validatePrice(trade: Trade): Either[ValidationError, Trade]
  def validateSymbol(trade: Trade): Either[ValidationError, Trade]
}

class ValidatorImpl extends Validator {
  override def validate(trade: Trade): Either[ValidationError, Trade] = {
    for {
      validatedQuantity <- validateQuantity(trade)
      validatedPrice    <- validatePrice(validatedQuantity)
      validatedSymbol   <- validateSymbol(validatedPrice)
    } yield validatedSymbol
  }

  def validateQuantity(trade: Trade): Either[ValidationError, Trade] =
    if (trade.quantity > 0) Right(trade) else Left(InvalidQuantity)
  def validatePrice(trade: Trade): Either[ValidationError, Trade] =
    if (trade.price > 0) Right(trade) else Left(InvalidPrice)
  def validateSymbol(trade: Trade): Either[ValidationError, Trade] =
    if (trade.symbol.exists(_.trim.nonEmpty)) Right(trade) else Left(EmptySymbol)
}

object Validator {
  val layer: ULayer[Validator] = ZLayer.succeed(new ValidatorImpl)
}
