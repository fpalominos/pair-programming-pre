package exercises.tradevalidationpipeline.model

case class Trade(
    id: String,
    symbol: String,
    quantity: Int,
    price: Double
)
