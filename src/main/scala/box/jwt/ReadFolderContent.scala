package box.jwt

import java.io.{ByteArrayOutputStream, IOException}
import java.util.logging.{Level, Logger}

import scala.collection.JavaConverters._
import com.box.sdk._

/**
  * Created by dzlabs.
  */
object ReadFolderContent {
  private val APP_USER_NAME = ""
  private val FILE_ID = ""
  private val sharedLink = ""

  @throws[IOException]
  def main(args: Array[String]): Unit = {
    // Turn off logging to prevent polluting the output.
    Logger.getLogger("com.box.sdk").setLevel(Level.ALL)

    val client = new BoxClient("/box-config-jwt.json")

    // Read File Metadata
    val info = BoxItem.getSharedItem(client.connection, sharedLink)

    println(s"Item ID: ${info.getID}, name ${info.getName}, type ${info.getType()}")

    println(s"Owner ID: ${info.getOwnedBy().getID} and name ${info.getOwnedBy().getName}")

    // Create a connection as the File owner
    val api = client.getUserConnection(info.getOwnedBy().getID)
    println(s"enterprise token ${client.connection.getAccessToken}, user token is ${api.getAccessToken}")

    // Read file as user
    val folder: BoxFolder = new BoxFolder(api, info.getID)
    for(child <- folder.getChildren().asScala) {
      println(s"Item ID: ${child.getID}, name ${child.getName}, type ${child.getType()}")
      if(child.getType() == "file") {
        val file = new BoxFile(api, child.getID)
        println(s"File: ${file.getInfo().getExtension()}, ${file.getInfo().getType()}, ${file.getInfo().getSize}, ${file.getInfo().getEtag}")
        println(s"${file.getDownloadURL}")
      }
    }
  }
}