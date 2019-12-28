package box.jwt

import java.io.{DataInputStream, File, FileInputStream, IOException}

import com.box.sdk._

import scala.collection.JavaConversions._

/**
  * Created by dzlabs.
  */
object UploadFileAsEnterpriseAdmin {
  private val CLIENT_ID = ""
  private val CLIENT_SECRET = ""
  private val ENTERPRISE_ID = ""
  private val PUBLIC_KEY_ID = ""
  private val PRIVATE_KEY_FILE = ""
  private val PRIVATE_KEY_PASSWORD = ""
  private val MAX_CACHE_ENTRIES = 100
  private val FILE = ""
  private val USER_ID = ""

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
    val rootFolder = BoxFolder.getRootFolder(api)
    System.out.println("folder owner: " + rootFolder.getInfo.getOwnedBy.getName)
    val fileId = uploadFile(FILE, api, rootFolder)
    val boxFile = new BoxFile(api, fileId)
    val ownerLogin = boxFile.getInfo.getOwnedBy.getName
    System.out.println("file owner name: " + ownerLogin)
  }

  private def uploadFile(pathFileName: String, api: BoxAPIConnection, folder: BoxFolder) = {
    var fileExists = false
    var fileId: String = ""
    try {
      val fileName = pathFileName.substring(pathFileName.lastIndexOf("/") + 1, pathFileName.length)
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
        val boxInfo = folder.uploadFile(stream, fileName)
        fileId = boxInfo.getID
        stream.close()
      }
      else {
        System.out.println("uploading new version of file: " + fileName)
        val file = new BoxFile(api, fileId)
        val stream = new FileInputStream(fileName)
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