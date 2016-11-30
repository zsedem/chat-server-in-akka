/*
 * Blindspotter (tm)
 *
 * Copyright (c) 2016 BalaBit
 * All rights reserved.
 */
package chat

import akka.actor.{
  Actor,
  ActorPath,
  ActorRef,
  ActorSelection,
  ActorSystem,
  Props
}
import chat.actors._
import com.typesafe.config.{ConfigFactory, ConfigValueFactory}
import scala.util.Random

object ClientApp extends App {
  import MainUtils._
  val host = {
    try {
      args(0)
    } catch {
      case _: IndexOutOfBoundsException =>
        System.err.println(
          "\nError: Provide actor server in the first argument")
        System.exit(1)
    }
  }
  private val config = ConfigFactory
    .load("client")
    .withValue(
      "akka.remote.netty.tcp.port",
      ConfigValueFactory.fromAnyRef(new Random().nextInt(10000) + 2000))

  val system: ActorSystem =
    ActorSystem.create("ChatClient", config)
  val server: ActorSelection = system.actorSelection(
    ActorPath.fromString(s"akka.tcp://Chat@$host:2552/user/server"))

  val commands = Set('c', 'j', 's')
  val client: ActorRef = system actorOf (Props {
    new Actor {
      var rooms: Map[String, Room] = Map()
      override def receive: Receive = {
        case RoomMessage(text, room, user) =>
          printer.println(s"${room.name} | ${user.ref.path.name}: $text")
        case msg =>
          printer.println(msg.toString)
      }
    }
  }, System.getProperty("user.name"))
  implicit val sender: ActorRef = client
  val printer: Printer =
    activateJLine("""
      | Commands:
      |   c roomName           - Create Room
      |   j roomName           - Join Room
      |   s roomName message   - Send Messages to the room
      |   q                    - Quits the client
    """.stripMargin) {

      case "q" =>
        system.terminate()
        System.exit(0)
        End

      case "" => Continue

      case str: String
          if str.headOption.exists(commands.contains)
            && str.tail.headOption.contains(' ') =>
        str.head match {

          case 'c' =>
            val roomName: String = str.split(' ')(1)
            println(roomName)
            server ! CreateRoom(roomName)

          case 'j' =>
            val roomName: String = str.split(' ')(1)
            val room: ActorSelection = system.actorSelection(
              ActorPath.fromString(
                s"akka.tcp://Chat@localhost:2552/user/server/$roomName"))
            println(room)
            room ! Enter()

          case 's' =>
            val roomName: String = str.split(' ')(1)
            val room: ActorSelection = system.actorSelection(
              ActorPath.fromString(
                s"akka.tcp://Chat@localhost:2552/user/server/$roomName"))
            val message: String = str.split(' ').tail.tail mkString " "
            println(room)
            room ! SendMessage(message)
        }
        Continue
    }
}
