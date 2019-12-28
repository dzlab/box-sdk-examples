package box.jwt

import java.io.IOException
import java.util.ArrayList
import java.util.logging.{Level, Logger}

import com.box.sdk.{BoxAPIConnection, BoxFile, ScopedToken}
import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream

/**
  * Created by dzlabs.
  */
object GetDownscopedTokens {

  private val APP_USER_NAME = ""
  private val FILE_ID = ""

  @throws[IOException]
  def main(args: Array[String]): Unit = {
    // Turn off logging to prevent polluting the output.
    Logger.getLogger("com.box.sdk").setLevel(Level.ALL)

    val client = new BoxClient(".../config.json")

    // Define resource and scopes that downscoped token should have access to// Define resource and scopes that downscoped token should have access to
    val fileResource = s"https://api.box.com/2.0/files/$FILE_ID"
    //val folderResource = "https://api.box.com/2.0/folders/67890"
    val scopes = new ArrayList[String]
    scopes.add("base_preview")
    scopes.add("item_download")

    // Perform token exchange to get downscoped token
    val downscopedToken: ScopedToken = client.connection.getLowerScopedToken(scopes, fileResource)

    println(s"Token ${downscopedToken.getAccessToken}")

    val api = new BoxAPIConnection(downscopedToken.getAccessToken)
    val file: BoxFile = new BoxFile(api, FILE_ID)
    val output = new ByteOutputStream()
    file.download(output)

    println(new String(output.getBytes))
  }
}
