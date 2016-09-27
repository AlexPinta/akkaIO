import akka.actor.Actor
import akka.actor.Actor.Receive

/**
 */
class FileHelperWorker extends Actor {
  override def receive: Receive = {
    case _ => println("start")
  }
}
