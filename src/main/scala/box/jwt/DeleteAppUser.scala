package box.jwt

import java.io.IOException
import java.nio.file.{Files, Paths}
import java.util.logging.{Level, Logger}

import com.box.sdk._

import scala.collection.JavaConversions._

/**
  * Created by dzlabs.
  */
object DeleteAppUser {
  private val CLIENT_ID = ""
  private val CLIENT_SECRET = ""
  private val ENTERPRISE_ID = ""
  private val PUBLIC_KEY_ID = ""
  private val PRIVATE_KEY_FILE = ""
  private val PRIVATE_KEY_PASSWORD = ""
  private val MAX_CACHE_ENTRIES = 100
  private val APP_USER_NAME = ""

  @throws[IOException]
  def main(args: Array[String]): Unit = { // Turn off logging to prevent polluting the output.
    Logger.getLogger("com.box.sdk").setLevel(Level.ALL)
    val privateKey = new String(Files.readAllBytes(Paths.get(PRIVATE_KEY_FILE)))
    val encryptionPref = new JWTEncryptionPreferences
    encryptionPref.setPublicKeyID(PUBLIC_KEY_ID)
    encryptionPref.setPrivateKey(privateKey)
    encryptionPref.setPrivateKeyPassword(PRIVATE_KEY_PASSWORD)
    encryptionPref.setEncryptionAlgorithm(EncryptionAlgorithm.RSA_SHA_256)
    //It is a best practice to use an access token cache to prevent unneeded requests to Box for access tokens.
    //For production applications it is recommended to use a distributed cache like Memcached or Redis, and to
    //implement IAccessTokenCache to store and retrieve access tokens appropriately for your environment.
    val accessTokenCache = new InMemoryLRUAccessTokenCache(MAX_CACHE_ENTRIES)
    val api = BoxDeveloperEditionAPIConnection.getAppEnterpriseConnection(ENTERPRISE_ID, CLIENT_ID, CLIENT_SECRET, encryptionPref, accessTokenCache)

    // Get Enterprise Users
    val userInfos = BoxUser.getAllEnterpriseUsers(api)

    // Get External Users
    //val externalUserInfos: Iterable[BoxUser.Info] = BoxUser.getExternalUsers(api)

    for(userInfo <- userInfos) {
      val userId = userInfo.getID()
      val externalAppUserId = userInfo.getExternalAppUserId()

      println(s"Deleting $userId ${userInfo.getName}: $externalAppUserId")

      val user = new BoxUser(api, userId)
      user.delete(false, true)

      println(s"Deleted $userId ${userInfo.getName}: $externalAppUserId")
    }
  }
}