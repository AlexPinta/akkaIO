import akka.actor.{Props, ActorSystem}

/**
 */
object Application extends App {
  val system = ActorSystem("ProcessingFiles")
  val fileReader = system.actorOf(Props(new FileHelper()), name = "fileReader")
  fileReader ! ""
}
