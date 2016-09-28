name := "akkaIO"

version := "1.0"

scalaVersion := "2.11.8"

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

libraryDependencies ++= Seq("com.typesafe.akka" % "akka-actor_2.11" % "2.4.9",
                            "com.typesafe.akka" % "akka-stream_2.11" % "2.4.9",
                            "com.github.scala-incubator.io" % "scala-io-core_2.10.2" % "0.4.2"
)