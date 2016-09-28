package service

import akka.actor._
import akka.pattern.AskableActorSelection
import akka.util.Timeout
import util.FileHelperWorker
import scala.concurrent.Await
import scala.concurrent.duration.DurationInt

/**
 */
object ActorService {
  def getActor(actorPath: String, actorName: String, actorId: String)(implicit supervisor: Actor): ActorRef = {
    val selection = supervisor.context.system.actorSelection(actorPath + actorName + actorId)
    implicit val timeout = Timeout.durationToTimeout(5 seconds)
    implicit val sender = supervisor
    val futureActor = new AskableActorSelection(selection).ask(new Identify(actorName))
    val ident = Await.result(futureActor, timeout.duration).asInstanceOf[ActorIdentity]
    if (ident.getRef == null) {
      return supervisor.context.system.actorOf(Props(new FileHelperWorker(supervisor.self, actorId)), name = actorName + actorId)
    } else {
      return ident.getRef
    }

  }
}
