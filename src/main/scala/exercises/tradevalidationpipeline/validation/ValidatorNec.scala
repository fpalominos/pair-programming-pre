package exercises.tradevalidationpipeline.validation

import cats.data.ValidatedNec
import cats.implicits.catsSyntaxValidatedIdBinCompat0
import cats.syntax.all._
import exercises.tradevalidationpipeline.model._
import zio.{ULayer, ZLayer}

sealed trait ValidatorNec {
  type ValidationResult[A] = ValidatedNec[ValidationError, A]
  def validate(trade: Trade): ValidationResult[Trade] = {
    (trade.id.validNec, validateSymbol(trade.symbol), validateQuantity(trade.quantity), validatePrice(trade.price)).mapN(Trade)
  }
  def validateQuantity(quantity: Int): ValidationResult[Int] =
    if (quantity > 0) quantity.validNec else InvalidQuantity.invalidNec
  def validatePrice(price: Double): ValidationResult[Double] =
    if (price > 0) price.validNec else InvalidPrice.invalidNec
  def validateSymbol(symbol: Option[String]): ValidationResult[Option[String]] =
    if (symbol.exists(_.trim.nonEmpty)) symbol.validNec else EmptySymbol.invalidNec
}

object ValidatorNec extends ValidatorNec

object ValidatorNecService {
  val layer: ULayer[ValidatorNec] = ZLayer.succeed {
    new ValidatorNec {}
  }
}
