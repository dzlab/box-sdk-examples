package box.jwt

import java.io._

import com.box.sdk._

/**
  * Created by dzlabs.
  */
object GetManagedUsersItems {
  private val CLIENT_ID = ""
  private val CLIENT_SECRET = ""
  private val ENTERPRISE_ID = ""
  private val PUBLIC_KEY_ID = ""
  private val PRIVATE_KEY_FILE = ""
  private val PRIVATE_KEY_PASSWORD = ""
  private val MAX_CACHE_ENTRIES = 100

  @throws[Exception]
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
    val userInfo = BoxUser.getCurrentUser(api).getInfo()
    System.out.format("Welcome, %s!\n\n", userInfo.getName)
    val managedUsers = BoxUser.getAllEnterpriseUsers(api)
    import scala.collection.JavaConversions._
    for (managedUser <- managedUsers) {
      System.out.println(managedUser.getName + " " + managedUser.getStatus)
      if (managedUser.getStatus == BoxUser.Status.ACTIVE) { // Used to get AppUser or ManagedUser
        val userApi = BoxDeveloperEditionAPIConnection.getAppUserConnection(managedUser.getID, CLIENT_ID, CLIENT_SECRET, encryptionPref, accessTokenCache)
        val boxFolder = BoxFolder.getRootFolder(userApi)
        val items = boxFolder.getChildren("name")
        for (item <- items) {
          System.out.println("\t" + item.getName)
        }
        //break //todo: break is not supported
        // 400 - they haven't accepted TOC
        // 403 - INACTIVE
      }
    }
  }
}