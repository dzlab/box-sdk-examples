package box.oauth

object GetToken {

  def main(args: Array[String]): Unit = {
    val config = OAuthConfig.fromJson()
    val client = new BoxClient(config, "https://localhost/boxRedirect")
    println(client.getBoxAuthorizeUrl())
  }
}