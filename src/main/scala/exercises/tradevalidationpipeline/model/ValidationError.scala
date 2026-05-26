package exercises.tradevalidationpipeline.model

trait ValidationError

case object InvalidQuantity extends ValidationError
case object InvalidPrice extends ValidationError
case object EmptySymbol extends ValidationError
