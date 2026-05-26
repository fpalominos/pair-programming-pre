package exercises.tradevalidationpipeline.processor

import exercises.tradevalidationpipeline.model.{InvalidQuantity, Trade, ValidationError}
import exercises.tradevalidationpipeline.validation.implicitexample.ValidatorI
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{mock, verify, when}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class TypeClassProcessorTest extends AnyFlatSpec with Matchers {
  "processor" should "return InvalidQuantity when quantity doesn't pass validation" in {
    implicit val mockTradeValidator: ValidatorI[ValidationError, Trade] =
      mock(classOf[ValidatorI[ValidationError, Trade]])
    val typeClassProcessor: TypeClassProcessor = new TypeClassProcessor()
    val trade1                                 = Trade(id = "1", symbol = Some("s1"), quantity = -3, price = 4)
    // simulating that validation interacts with an external service, hence mocking
    when(mockTradeValidator.validate(any[Trade])).thenReturn(Left(InvalidQuantity))
    val result: Seq[Either[ValidationError, Trade]] = typeClassProcessor.process(Seq(trade1))
    result shouldBe Seq(Left(InvalidQuantity))

    verify(mockTradeValidator).validate(trade1)
  }
}
