package util

import akka.actor.{ActorRef, Actor}
import util.Message.{RetrieveData, GetResult, AccumulateAmount}

/**
 */
class FileHelperWorker(supervisor: ActorRef, private val account: String) extends Actor {
//  private var account = ""
  private var accumAmount: Double = 0
//  @throws[Exception](classOf[Exception])
//  override def preStart(): Unit = {
//    account = self.path.name
//  }

  override def receive: Receive = {
    case AccumulateAmount(amount) => {
      accumAmount+=amount
      println(account + "  -  " + accumAmount)
    }
    case GetResult => {
      sender() ! RetrieveData(this)
      context.stop(self)
    }
    case _ => println("Unknown message for worker!")
  }

  def getAccount(): String = account
  def getAmount(): Double = accumAmount
}
