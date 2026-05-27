package exercises.tradevalidationpipeline.model

import zio.json.{DeriveJsonDecoder, DeriveJsonEncoder, JsonDecoder, JsonEncoder}

case class SymbolMarket(symbol: String, market: String)

object SymbolMarket {
  implicit val decoder: JsonDecoder[SymbolMarket] = DeriveJsonDecoder.gen[SymbolMarket]
  implicit val encoder: JsonEncoder[SymbolMarket] = DeriveJsonEncoder.gen[SymbolMarket]
}