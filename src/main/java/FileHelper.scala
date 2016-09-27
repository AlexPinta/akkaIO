import java.io.File
import java.nio.file.Paths
import akka.actor.Actor
import akka.actor.Actor.Receive
import akka.io.IO
import akka.stream.{ActorAttributes, IOResult}
import akka.stream.scaladsl.FileIO
import akka.stream.scaladsl._
import akka.util.ByteString
import scala.concurrent.Future

/**
 */
class FileHelper extends Actor {

  def readFile(/*paths: Paths*/): Unit = {
//    val file = Paths.get(getClass.getClassLoader.getResource("./data.txt").toString)
    scala.io.Source.fromFile(getClass.getClassLoader.getResource("./data.txt").getFile)
      .getLines.foreach(line => println(line))
//      .via(Framing.delimiter(ByteString(System.lineSeparator), maximumFrameLength=8192, allowTruncation=true))
//      .map(line => {
//        println(line.utf8String)
        //        line.utf8String
//      })



//    val foreach: Future[IOResult] = FileIO.fromPath(file)
//      .withAttributes(ActorAttributes.dispatcher("custom-blocking-io-dispatcher"))
//      .to(Sink.ignore)
//      .run()
//
  }

  def receive = {
    case "hello" => println("hello ")
    case _ => readFile()
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
