package util

import java.io._
import java.nio.channels.WritableByteChannel

import akka.actor._
import akka.stream.scaladsl.FileIO
import service.ActorService
import util.Message._
import scala.io.Source
import scalax.io.Resource
import scalax.io.managed.{WriterResource, WritableByteChannelResource, OutputStreamResource}

/**
 */
object Message {
  case object Start
  case object Finish
  case class AccumulateAmount(line: Double)
  case object GetResult
  case class RetrieveData(data: FileHelperWorker)
}

class FileHelper extends Actor {
  def readFile(): Unit = {
    Source.fromFile(getClass.getClassLoader.getResource("./data.txt").getFile)
      .getLines.foreach(line => {
        val lineFields = if(line.isEmpty) new Array[String](0) else line.split(";")
        if (lineFields.nonEmpty) {
          implicit val supervisor = this
          val worker = ActorService.getActor("akka://ProcessingFiles/user/", "worker-", lineFields(0))
          worker ! AccumulateAmount(lineFields(1).toDouble)

        }
      })
      context.system.actorSelection("akka://ProcessingFiles/user/*") ! GetResult
  }

  def writeDataToFile(data: FileHelperWorker): Unit = {


    val outputStream: FileOutputStream = new FileOutputStream(getClass.getClassLoader.getResource("./result.txt").getFile)

    val out: OutputStreamResource[OutputStream] = Resource.fromOutputStream(outputStream)

    // Convert the output resource to a Resource based on a WritableByteChannel.  The Resource extends Output Trait
    val writableChannel: WritableByteChannelResource[WritableByteChannel] = out.writableByteChannel

    // Convert the output resource to a WriterResource which extends the WriteChars Trait and is based on a Writer
    val writer: WriterResource[Writer] = out.writer

//    val out = Source.fromFile(getClass.getClassLoader.getResource("./result.txt").getFile)

    //    data.
//    FileIO.toPath(getClass.getClassLoader.getResource("./result.txt").getFile, )

//    val file = new File(getClass.getClassLoader.getResource("./result.txt").getFile)
//    val bw = new BufferedWriter(new FileWriter(file))
//    bw.write("").
//    bw.close()
  }


  @throws[Exception](classOf[Exception])
  override def preStart(): Unit = {
    self ! Start
  }

  def receive = {
    case Start => readFile()
    case RetrieveData(data) => { println("AMOUNT " + data.getAccount() + " - " + data.getAmount()) }
//    case Finish => context.system.terminate()
//    case _ => {
//      println("Unknown message!")
//    }
  }
}
