/*
 * Blindspotter (tm)
 *
 * Copyright (c) 2016 BalaBit
 * All rights reserved.
 */
package chat

import akka.actor.{ActorSystem, Props}
import com.typesafe.config.ConfigFactory

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

object ServerApp extends App {
  import MainUtils._
  val system: ActorSystem =
    ActorSystem.create("Chat", ConfigFactory.load("server"))
  system.actorOf(Props { new actors.ChatServer() }, "server")

  activateJLine("Use q to exit", "") {
    case "q" | "exit" | "quit" =>
      system.terminate() andThen {
        case Success(_) =>
          System.exit(0)
        case Failure(exc) =>
          exc.printStackTrace()
          System.exit(1)
      }
      End
  }
}
