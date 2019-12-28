package box.jwt

import java.io.{FileInputStream, InputStreamReader}

import com.box.sdk.{BoxConfig, BoxDeveloperEditionAPIConnection, IAccessTokenCache, InMemoryLRUAccessTokenCache}

/**
 * Documentation for Account setup https://developer.box.com/docs/setting-up-a-jwt-app
 * @param path the path to the Box json configuration
 */
class BoxClient(path: String = "/box-account-config.json") {
  /**
   * Maximum cache entries
   */
  val MAX_CACHE_ENTRIES = 100

  /**
   * Box Access Tokens cache
   */
  val accessTokenCache: IAccessTokenCache = new InMemoryLRUAccessTokenCache(MAX_CACHE_ENTRIES)

  /**
   * Box configuration
   */
  val boxConfig: BoxConfig = {
    // Read Box config file
    val reader =  try {
      new InputStreamReader( getClass.getResourceAsStream( path ) )
    }catch { case e =>
      new InputStreamReader( new FileInputStream( path ) )
    }
    BoxConfig.readFrom( reader )
  }

  /**
   * Box client
   */
  lazy val connection: BoxDeveloperEditionAPIConnection = {
    val conn = BoxDeveloperEditionAPIConnection.getAppEnterpriseConnection(boxConfig, accessTokenCache)
    conn.setExpires(600000)
    conn
  }

  /**
   * Get a user connection
   * @param userId the User ID
   * @return
   */
  def getUserConnection(userId: String) = {
    BoxDeveloperEditionAPIConnection.getAppUserConnection(userId, boxConfig)
  }
}