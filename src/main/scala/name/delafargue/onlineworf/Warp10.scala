package name.delafargue.onlineworf

import java.util.UUID
import io.warp10.crypto._
import io.warp10.quasar.encoder.QuasarTokenEncoder
import collection.JavaConverters._

import scala.concurrent.duration._


object Warp10 {


  val qte = new QuasarTokenEncoder()

  def deliverToken(req: TokenRequest): String = req.token_type match {
    case ReadToken => deliverReadToken(req)
    case WriteToken => deliverWriteToken(req)
  }

  private def buildEncoder(data: Warp10Data): (QuasarTokenEncoder, KeyStore) = {
    val keyStore: KeyStore = new DummyKeyStore()
    keyStore.setKey(KeyStore.SIPHASH_APPID, keyStore.decodeKey(data.hash_app))
    keyStore.setKey(KeyStore.SIPHASH_TOKEN, keyStore.decodeKey(data.hash_token))
    keyStore.setKey(KeyStore.AES_TOKEN,     keyStore.decodeKey(data.aes_token))

    (new QuasarTokenEncoder(), keyStore)
  }

  def deliverWriteToken(req: TokenRequest) = {
    val (qte, keyStore) = buildEncoder(req.warp10_data)
    val writeToken = qte.deliverWriteToken(
      req.app_name,
      req.owner_id,
      req.owner_id,
      req.labels.asJava,
      req.ttl.toMillis,
      keyStore
    )

    writeToken
  }

  def deliverReadToken(req: TokenRequest) = {
    val (qte, keyStore) = buildEncoder(req.warp10_data)
    val readToken = qte.deliverReadToken(
      req.app_name,
      req.owner_id,
      List(req.owner_id).asJava,
      List(req.app_name).asJava,
      req.labels.asJava,
      Map.empty.asJava,
      req.ttl.toMillis,
      keyStore
    )

    readToken
  }
}
