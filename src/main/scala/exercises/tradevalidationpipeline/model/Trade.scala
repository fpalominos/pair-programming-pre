package exercises.tradevalidationpipeline.model

case class Trade(
    id: String,
    symbol: Option[String],
    quantity: Int,
    price: Double
)
