package box.jwt

import java.io.{DataInputStream, File, FileInputStream, IOException}

import com.box.sdk._

import scala.collection.JavaConversions._

/**
  * Created by dzlab.
  */
object GetAllAppUsers {
  private val CLIENT_ID = ""
  private val CLIENT_SECRET = ""
  private val ENTERPRISE_ID = ""
  private val PUBLIC_KEY_ID = ""
  private val PRIVATE_KEY_FILE = ""
  private val PRIVATE_KEY_PASSWORD = ""
  private val MAX_CACHE_ENTRIES = 100

  @throws[IOException]
  def main(args: Array[String]): Unit = {
    val file = new File(PRIVATE_KEY_FILE)
    val fileData = new Array[Byte](file.length.toInt)
    val dis = new DataInputStream(new FileInputStream(file))
    dis.readFully(fileData)
    dis.close()
    val privateKey = new String(fileData)
    val encryptionPref = new JWTEncryptionPreferences
    encryptionPref.setPublicKeyID(PUBLIC_KEY_ID)
    encryptionPref.setPrivateKey(privateKey)
    encryptionPref.setPrivateKeyPassword(PRIVATE_KEY_PASSWORD)
    encryptionPref.setEncryptionAlgorithm(EncryptionAlgorithm.RSA_SHA_256)
    val accessTokenCache = new InMemoryLRUAccessTokenCache(MAX_CACHE_ENTRIES)

    val api = BoxDeveloperEditionAPIConnection.getAppEnterpriseConnection(ENTERPRISE_ID, CLIENT_ID, CLIENT_SECRET, encryptionPref, accessTokenCache)

    val currentUser: BoxUser = BoxUser.getCurrentUser(api)
    println(s"Service account user ${currentUser.getID}, name: ${currentUser.getInfo().getName}")

    val users = BoxUser.getAllEnterpriseUsers(api, "App")
    for (user <- users) {
      System.out.println(user.getName)
      System.out.println("\t" + user.getIsPlatformAccessOnly)
    }

  }
}