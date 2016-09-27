package util

import akka.actor.{ActorRef, Actor}
import util.Message.ProcessLine

/**
 */
class FileHelperWorker(fileHelperSupervisor: ActorRef) extends Actor {
  override def receive: Receive = {
    case ProcessLine(line) => {
      println("line " + line.length)
    }
    case _ => println("start")
  }
}
