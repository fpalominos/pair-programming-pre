package exercises.tradevalidationpipeline.validation

import cats.syntax.all._
import exercises.tradevalidationpipeline.model._

object ValidatorNecApp extends App {
  val trade = Trade(id = "1", symbol = Some("s1"), quantity = -2, price = -10)
  val result = ValidatorNec.validate(trade)
  println(result)
}