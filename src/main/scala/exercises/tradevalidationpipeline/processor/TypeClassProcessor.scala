package exercises.tradevalidationpipeline.processor

import exercises.tradevalidationpipeline.model.{Trade, ValidationError}
import exercises.tradevalidationpipeline.validation.implicitexample.ValidatorI

//implicit validator, which can then be mocked during tests if needed
class TypeClassProcessor(implicit validator: ValidatorI[ValidationError, Trade]) {
  def process(trades: Seq[Trade]): Seq[Either[ValidationError, Trade]] = {
    trades.map(t => validator.validate(t))
  }
}
