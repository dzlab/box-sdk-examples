package box.oauth

import akka.actor.ActorSystem
import akka.http.scaladsl.server.{Directive, Directive1, Route}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import box.jwt.ReadFile.FILE_ID
import com.box.sdk.BoxFile
import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream

import scala.io.StdIn

/**
  * Created by dzlabs.
  */
object WebServer {
  def main(args: Array[String]) {

    implicit val system = ActorSystem("my-system")
    implicit val materializer = ActorMaterializer()
    // needed for the future flatMap/onComplete in the end
    implicit val executionContext = system.dispatcher

    val config = OAuthConfig.fromJson()
    val client = new BoxClient(config, "https://localhost/boxRedirect")

    def getRoute (): Route = {
      get {
        path("hello") {
          complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "<h1>Say hello to akka-http</h1>"))
        } ~ path("boxRedirect") {
          parameter("code", "state") { (code, state) =>
            // Use the authorization token to create a Box connection
            val connection = client.getUserConnection(code)
            val token = connection.getAccessToken()
            println(token)

            // Read file as an example of using Box connection
            val file: BoxFile = new BoxFile(connection, "533103493942")
            val output = new ByteOutputStream()
            file.download(output)

            // return response
            complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, new String(output.getBytes)))
          }
        }
      }
    }

    val route = getRoute()

    val bindingFuture = Http().bindAndHandle(route, "localhost", 8080)

    println(s"Visit: ${client.getBoxAuthorizeUrl()}")
    println(s"Server online at http://localhost:8000/\nPress RETURN to stop...")
    StdIn.readLine() // let it run until user presses return
    bindingFuture
      .flatMap(_.unbind()) // trigger unbinding from the port
      .onComplete(_ => system.terminate()) // and shutdown when done
  }
}