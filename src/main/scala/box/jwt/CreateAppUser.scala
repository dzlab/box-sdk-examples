package box.jwt

import java.io.IOException
import java.nio.file.{Files, Paths}
import java.util.logging.{Level, Logger}

import com.box.sdk._


/**
  * Created by dzlabs.
  */
object CreateAppUser {
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
    val params = new CreateUserParams
    params.setSpaceAmount(1073741824) //1 GB
    val user = BoxUser.createAppUser(api, APP_USER_NAME, params)
    System.out.format("User created with name %s and id %s\n\n", APP_USER_NAME, user.getID)
  }
}