package box.jwt

import java.io.IOException
import java.util.logging.{Level, Logger}

import com.box.sdk._
import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream

/**
  * Created by dzlabs.
  */
object ReadFileAsUser {
  private val APP_USER_NAME = ""
  private val FILE_ID = ""

  @throws[IOException]
  def main(args: Array[String]): Unit = {
    // Turn off logging to prevent polluting the output.
    Logger.getLogger("com.box.sdk").setLevel(Level.ALL)

    val client = new BoxClient("/box-config-jwt.json")

    // Read File Metadata
    val fileByServiceAccount : BoxFile = new BoxFile(client.connection, FILE_ID)
    fileByServiceAccount.getInfo()
    fileByServiceAccount.getInfo().getOwnedBy()
    val ownerid = fileByServiceAccount.getInfo().getOwnedBy().getID
    println(s"Owener ID: ${ownerid}")

    // Create a connection as the File owner
    val api = client.getUserConnection(ownerid)
    println(s"enterprise token ${client.connection.getAccessToken}, user token is ${api.getAccessToken}")

    // Read file as user
    val file: BoxFile = new BoxFile(api, FILE_ID)
    val output = new ByteOutputStream()
    file.download(output)
    println(new String(output.getBytes))
  }
}