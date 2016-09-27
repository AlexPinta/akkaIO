import akka.actor.{Props, ActorSystem}
import util.FileHelper

/**
 */
object Application extends App {
  val system = ActorSystem("ProcessingFiles")
  val fileReader = system.actorOf(Props(classOf[FileHelper]), name = "fileReader")
  fileReader ! "start"
}
