package util

import java.io._
import java.nio
import java.nio.ByteBuffer
import java.nio.channels.{CompletionHandler, AsynchronousFileChannel, WritableByteChannel}
import java.nio.charset.{Charset, StandardCharsets}
import java.nio.file.{StandardOpenOption, Paths}
import akka.actor._
import akka.io.IO
import service.ActorService
import util.Message._
import scala.concurrent.{Promise, ExecutionContext, Future}
import scala.io.Source
import scala.util.Try

//import scalax.io.{StandardOpenOption, Resource}
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



  def read(file: String)(implicit ec: ExecutionContext): Future[Array[Byte]] = {
    val p = Promise[Array[Byte]]()
    try {
      val channel = AsynchronousFileChannel.open(Paths.get(file), StandardOpenOption.READ)
      val buffer = ByteBuffer.allocate(channel.size().toInt)
      channel.read(buffer, 0L, buffer, onComplete(channel, p))
    }
    catch {
      case t: Throwable => p.failure(t)
    }
    p.future
  }

  def readText(file: String, charsetName: Charset = StandardCharsets.UTF_8)
              (implicit ec: ExecutionContext): Future[String] = {
    read(file)
      .map(data => {
        println(data)
        new String(_, charsetName).

      })
//      .map(f = new String(_, charsetName))
  }

  private def closeSafely(channel: AsynchronousFileChannel) =
    try {
      channel.close()
    } catch {
      case e: IOException =>
    }

  private def onComplete(channel: AsynchronousFileChannel, p: Promise[Array[Byte]]) = {
    new CompletionHandler[Integer, ByteBuffer]() {
      def completed(res: Integer, buffer: ByteBuffer): Unit = {
        p.complete(Try {
          buffer.array()
        })
        closeSafely(channel)
      }

      def failed(t: Throwable, buffer: ByteBuffer): Unit = {
        p.failure(t)
        closeSafely(channel)
      }
    }
  }


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

//    val file: Future[Int] = Future {
//      val source = scala.io.Source.fromFile("myText.txt")
//      // do domething
//    }


//    val outputStream: FileOutputStream = new FileOutputStream(getClass.getClassLoader.getResource("./result.txt").getFile)
//    val out: OutputStreamResource[OutputStream] = Resource.fromOutputStream(outputStream)
//    // Convert the output resource to a Resource based on a WritableByteChannel.  The Resource extends Output Trait
//    val writableChannel: WritableByteChannelResource[WritableByteChannel] = out.writableByteChannel
//
//    // Convert the output resource to a WriterResource which extends the WriteChars Trait and is based on a Writer
//    val writer: WriterResource[Writer] = out.writer
//
//    writer.writeStrings()

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
