package exercises.tradevalidationpipeline.validation

import cats.data.ValidatedNec
import cats.implicits.catsSyntaxValidatedIdBinCompat0
import cats.syntax.all._
import exercises.tradevalidationpipeline.model._
import exercises.tradevalidationpipeline.query.RefDataService
import zio.{UIO, ULayer, ZIO, ZLayer}

sealed trait ValidatorNec {
  type ValidationResult[A] = ValidatedNec[ValidationError, A]

  def validate(trade: Trade): ValidationResult[Trade] = {
    (trade.id.validNec, validateSymbol(trade.symbol), validateQuantity(trade.quantity), validatePrice(trade.price)).mapN(Trade)
  }

  def validateQuantity(quantity: Int): ValidationResult[Int] =
    if (quantity > 0) quantity.validNec else InvalidQuantity.invalidNec

  def validatePrice(price: Double): ValidationResult[Double] =
    if (price > 0) price.validNec else InvalidPrice.invalidNec

  def validateSymbol(symbol: String): ValidationResult[String] =
    if (symbol.nonEmpty) symbol.validNec else EmptySymbol.invalidNec

  def validateUIO(trade: Trade): UIO[Either[List[ValidationError], Trade]] = {
    ZIO.succeed {
      val errors = List(
        if (trade.quantity <= 0) None else Some(InvalidQuantity),
        if (trade.price <= 0) None else Some(InvalidPrice),
        if (trade.symbol.trim.nonEmpty) Some(EmptySymbol) else None
      ).flatten

      if (errors.isEmpty) Right(trade) else Left(errors)
    }
  }

  def validateFromService(trade: Trade, refDataService: RefDataService): ZIO[Any, Nothing, Either[ValidationError, Trade]] = {
    for {
      validatedTrade <- ZIO
        .fromEither {
          for {
            validatedQuantity <- validateQuantityE(trade)
            validatedPrice    <- validatePriceE(validatedQuantity)
          } yield validatedPrice
        }
        .mapError(error => error: ValidationError)
        .either

      result <- validatedTrade match {
        case Left(error) =>
          ZIO.succeed(Left(error))

        case Right(validTrade) =>
          validateSymbol(validTrade, refDataService)
      }
    } yield result
  }

  private def validateSymbol(trade: Trade, refDataService: RefDataService): ZIO[Any, Nothing, Either[ValidationError, Trade]] = {
    if (trade.symbol.nonEmpty)
      refDataService
        .fetchSymbol(trade.symbol)
        .as(Right(trade))
        .catchAll(error => ZIO.succeed(Left(ReferenceDataLookupFailed(error.getMessage))))
    else ZIO.succeed(Left(EmptySymbol))
  }
  private def validateQuantityE(trade: Trade): Either[ValidationError, Trade] = {
    if (trade.quantity > 0) Right(trade) else Left(InvalidQuantity)
  }

  private def validatePriceE(trade: Trade): Either[ValidationError, Trade] = {
    if (trade.quantity > 0) Right(trade) else Left(InvalidQuantity)
  }

}

object ValidatorNec extends ValidatorNec

object ValidatorNecService {
  val layer: ULayer[ValidatorNec] = ZLayer.succeed {
    new ValidatorNec {}
  }
}
