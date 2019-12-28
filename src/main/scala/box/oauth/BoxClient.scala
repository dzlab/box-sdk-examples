package box.oauth

import play.api.libs.json._

import scala.util.Random
import com.box.sdk.BoxAPIConnection


case class OAuthConfig(client_id: String, client_secret: String)

object OAuthConfig {
  def fromJson(name: String = "/box-config.json"): OAuthConfig = {
    val configJson: JsValue = Json.parse(getClass.getResourceAsStream(name))
    val ak : String = configJson.\("access_key").as[String]
    val sk : String = configJson.\("secret_key").as[String]
    OAuthConfig(ak, sk)
  }
}

class BoxClient(config: OAuthConfig, redirect_uri: String) {
  val box_authorize_url = "https://account.box.com/api/oauth2/authorize"
  val box_token_url = "https://api.box.com/oauth2/token"

  def getRandomString(length: Int =10): String = {
    val chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
    val rnd = Random
    val charSequence = for(_ <- 0 to length) yield "" + chars(rnd.nextInt(chars.length))
    charSequence.reduce(_ + _)
  }

  /**
   * Get a url for directing user to Box login page in order to authorize out App
   * @param stateLength
   * @return
   */
  def getBoxAuthorizeUrl(stateLength:Int = 20) =
    s"${box_authorize_url}?response_type=code&client_id=${config.client_id}&redirect_uri=${redirect_uri}&state=${getRandomString(stateLength)}"

  /**
   * Get Box connection
   * @param code the Authorization code returned by Box
   * @return
   */
  def getUserConnection(code: String): BoxAPIConnection = {
    // Instantiate new Box API connection object
    val connection = new BoxAPIConnection(config.client_id, config.client_secret, code)
    connection
  }
}
