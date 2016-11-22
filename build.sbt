name := "akka-chat"

version := "1.0"

scalaVersion := "2.12.0"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.4.14",
  "com.typesafe.akka" %% "akka-agent" % "2.4.14",
  "com.typesafe.akka" %% "akka-contrib" % "2.4.14",
  "com.typesafe.akka" %% "akka-osgi" % "2.4.14",
  "com.typesafe.akka" %% "akka-persistence" % "2.4.14",
  "com.typesafe.akka" %% "akka-persistence-tck" % "2.4.14",
  "com.typesafe.akka" %% "akka-remote" % "2.4.14",
  "com.typesafe.akka" %% "akka-slf4j" % "2.4.14",
  "com.typesafe.akka" %% "akka-testkit" % "2.4.14",
  "org.scalatest" %% "scalatest" % "3.0.0"
)