package util

import akka.actor._
import akka.pattern.AskableActorSelection
import akka.util.Timeout
import util.Message.{GetResult, Finish, Start, ProcessLine}

import scala.concurrent.Await

/**
 */
object Message {
  case object Start
  case object Finish
  case class ProcessLine(line: Array[String])
  case object GetResult
}

class FileHelper extends Actor {
  def readFile(/*paths: Paths*/): Unit = {
    scala.io.Source.fromFile(getClass.getClassLoader.getResource("./data.txt").getFile)
      .getLines.foreach(line => {
        val lineFields = line.split(";")
        if (lineFields.nonEmpty) {
          val actorId = "worker-"+lineFields(0)
          val sel = context.system.actorSelection("akka://ProcessingFiles/user/fileReader/"+actorId)
          val asker = new AskableActorSelection(sel).ask(new Identify(actorId))
          var ref
          try {
            val ident = Await.result(asker, new Timeout(3000).duration())
            ref = ident.getRef();
          } catch (Exception e) {
          }
          val worker = context.system.actorOf(Props(new FileHelperWorker(self)), name = "worker"+lineFields(0))
          worker ! ProcessLine(lineFields)
        }
      })
      context.system.actorSelection("akka://ProcessingFiles/user/*") ! GetResult


//    val foreach: Future[IOResult] = FileIO.fromPath(file)
//      .withAttributes(ActorAttributes.dispatcher("custom-blocking-io-dispatcher"))
//      .to(Sink.ignore)
//      .run()
//
  }


  @throws[Exception](classOf[Exception])
  override def preStart(): Unit = {
    self ! Start
  }

  def receive = {
    case Start => readFile()
    case Finish => context.system.terminate()
    case _ => println("Unknown message!")
  }



//  val file = new File("example.csv")
//
//  SynchronousFileSource(file)
//    .runForeach((chunk: ByteString) ⇒ handle(chunk))
//
//  implicit val dispatcher = context.dispatcher
//
//  val file = FileIO.open("myFile.data")
//  // read 200 bytes from the beginning of the file
//  val readResultFuture = file.read(0,200)
//
//  // do stuff with the future
//  readResultFuture.onSuccess({
//    case bytes:ByteString=>{
//      println(bytes.utf8String)
//    }
//  }).andThen{ case _ => file.close()} // Close when we're done reading
}
