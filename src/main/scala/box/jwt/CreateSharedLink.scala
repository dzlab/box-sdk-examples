package box.jwt

import java.io.{DataInputStream, File, FileInputStream, IOException}

import com.box.sdk._

/**
  * Created by dzlabs.
  */
object CreateSharedLink {
  private val CLIENT_ID = ""
  private val CLIENT_SECRET = ""
  private val ENTERPRISE_ID = ""
  private val PUBLIC_KEY_ID = ""
  private val PRIVATE_KEY_FILE = ""
  private val PRIVATE_KEY_PASSWORD = ""
  private val MAX_CACHE_ENTRIES = 100
  private var USER_ID = "" // <--- put the appUserId here

  private val FILE = ""

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

    val enterpriseConnection = BoxDeveloperEditionAPIConnection.getAppEnterpriseConnection(ENTERPRISE_ID, CLIENT_ID, CLIENT_SECRET, encryptionPref, accessTokenCache)
    USER_ID = createUser(enterpriseConnection, "custom-app-user", "unemployed")

    val api = BoxDeveloperEditionAPIConnection.getAppUserConnection(USER_ID, CLIENT_ID, CLIENT_SECRET, encryptionPref, accessTokenCache)
    val rootFolder = BoxFolder.getRootFolder(api)
    System.out.println("folder owner: " + rootFolder.getInfo.getOwnedBy.getName)
    val kentestFolder = getChildFolder(rootFolder)
    kentestFolder.collaborate("...", BoxCollaboration.Role.EDITOR)
    val fileId = uploadFile(FILE, api, kentestFolder)
    val boxFile = new BoxFile(api, fileId)
    createSharedLink(boxFile)
  }

  private def createUser(api: BoxDeveloperEditionAPIConnection, userName: String, jobTitle: String): String = {
    val spaceAmount = 1073741

    // Create param object
    val params = new CreateUserParams
    params.setJobTitle(jobTitle)
    params.setSpaceAmount(spaceAmount)

    // Create app user
    val createdUserInfo = BoxUser.createAppUser(api, userName)
    println(s"Created user: ${createdUserInfo.getID}")
    createdUserInfo.getID
  }

  def createSharedLink(boxFile: BoxFile): BoxSharedLink = {
    val permissions = new BoxSharedLink.Permissions
    permissions.setCanDownload(true)
    permissions.setCanPreview(true)
    val unshareAt = null
    val sharedLink = boxFile.createSharedLink(BoxSharedLink.Access.OPEN, unshareAt, permissions)
    System.out.println("Download link: " + sharedLink.getDownloadURL)
    sharedLink
  }

  private def getChildFolder(rootFolder: BoxFolder): BoxFolder = {
    var folder: BoxFolder = null
    val items = rootFolder.getChildren
    import scala.collection.JavaConversions._
    for (item <- items) {
      if (item.getName == "...") folder = item.getResource.asInstanceOf[BoxFolder]
    }
    if (null == folder) {
      val info = rootFolder.createFolder("...")
      folder = info.getResource
    }
    folder
  }

  private def uploadFile(pathFileName: String, api: BoxAPIConnection, folder: BoxFolder) = {
    var fileExists = false
    var fileId: String = null
    try {
      val fileName = pathFileName.substring(pathFileName.lastIndexOf("/") + 1, pathFileName.length)
      import scala.collection.JavaConversions._
      for (itemInfo <- folder) {
        if (itemInfo.isInstanceOf[BoxFile#Info]) {
          val fileInfo = itemInfo.asInstanceOf[BoxFile#Info]
          if (fileName == fileInfo.getName) {
            fileExists = true
            fileId = fileInfo.getID
          }
        }
      }
      if (!fileExists) {
        System.out.println("uploading new file: " + fileName)
        val stream = new FileInputStream(pathFileName)
        val boxInfo = folder.uploadFile(stream, pathFileName)
        fileId = boxInfo.getID
        stream.close()
      }
      else {
        System.out.println("uploading new version of file: " + fileName)
        val file = new BoxFile(api, fileId)
        val stream = new FileInputStream(pathFileName)
        file.uploadVersion(stream)
        stream.close()
      }
    } catch {
      case e: IOException =>
        System.out.println(e)
    }
    fileId
  }
}