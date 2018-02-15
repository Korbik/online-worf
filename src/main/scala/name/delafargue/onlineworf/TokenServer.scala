package name.delafargue.onlineworf

import cats.effect.IO
import cats.syntax.either._
import fs2.StreamApp
import io.circe._
import io.circe.parser._
import io.circe.syntax._
import io.circe.generic.JsonCodec
import io.circe.generic.auto._
import org.http4s._
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl
import org.http4s.server.blaze.BlazeBuilder
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

sealed trait TokenType
case object ReadToken extends TokenType
case object WriteToken extends TokenType

case class Warp10Data(
  aes_token: String,
  hash_app: String,
  hash_token: String,
)

case class TokenRequest(
  token_type: TokenType,
  app_name: String,
  owner_id: String,
  labels: Map[String,String],
  warp10_data: Warp10Data,
  ttl: Duration = 10.days,
)

object TokenRequest {
  implicit val encodeTokenType: Encoder[TokenType] = Encoder.encodeString.contramap(_.toString)
  implicit val decodeTokenType: Decoder[TokenType] = Decoder.decodeString.emap {
    case "ReadToken" => Right(ReadToken)
    case "WriteToken" => Right(WriteToken)
    case _ => Left("ReadToken or WriteToken")
  }
  implicit val encodeInstant: Encoder[Duration] = Encoder.encodeString.contramap(_.toString)
  implicit val decodeInstant: Decoder[Duration] = Decoder.decodeString.emap { str =>
    Either.catchNonFatal(Duration(str)).leftMap(t => "Duration")
  }
}



object TokenServer extends StreamApp[IO] with Http4sDsl[IO] {
  import TokenRequest._
  implicit val decoder = jsonOf[IO, TokenRequest]
  val service = HttpService[IO] {
    case req @ POST -> Root / "token" =>
      for {
        request <- req.as[TokenRequest]
        token = Warp10.deliverToken(request)
        resp <- Ok(Json.obj("token" -> Json.fromString(token)))
      } yield resp
  }

  def stream(args: List[String], requestShutdown: IO[Unit]) =
    BlazeBuilder[IO]
      .bindHttp(8080, "0.0.0.0")
      .mountService(service, "/")
      .serve
}
