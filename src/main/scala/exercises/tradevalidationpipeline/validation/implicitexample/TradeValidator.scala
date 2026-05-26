package exercises.tradevalidationpipeline.validation.implicitexample

import exercises.tradevalidationpipeline.model.{EmptySymbol, InvalidPrice, InvalidQuantity, Trade, ValidationError}

object TradeValidator {
    implicit val tradeValidator: ValidatorI[ValidationError, Trade] = new ValidatorI[ValidationError, Trade] {
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
}
