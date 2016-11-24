/*
 * Blindspotter (tm)
 *
 * Copyright (c) 2016 BalaBit
 * All rights reserved.
 */
package chat
import akka.actor.ActorRef

import scala.language.implicitConversions


case class CurrentRooms(rooms: List[Room])
case class Login(name: String)
case class CreateRoom(name: String)
case class SendMessage(text: String)
case class RoomMessage(text: String, room: Room, user: User) {
  def answer(msg: SendMessage)(implicit sender: ActorRef): Unit = room ! msg
}

case class Enter()
case class Leave()
case class Rejected(msg: SendMessage)
case class User(ref: ActorRef) { def !(t: Any)(implicit sender: ActorRef): Unit = ref ! t }
case class Room(ref: ActorRef) { def !(t: Any)(implicit sender: ActorRef): Unit = ref ! t }
case class AlreadyExists()

