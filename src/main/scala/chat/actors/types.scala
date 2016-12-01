/*
 * Blindspotter (tm)
 *
 * Copyright (c) 2016 BalaBit
 * All rights reserved.
 */
package chat.actors

import java.time.OffsetDateTime

import akka.actor.ActorRef

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
case class RejectedUserAlreadyLoggedIn(msg: Login)
case class User(ref: ActorRef) {
  def !(t: Any)(implicit sender: ActorRef): Unit = ref ! t
}
case class Room(ref: ActorRef, name: String) {
  def !(t: Any)(implicit sender: ActorRef): Unit = ref ! t
}
case class AlreadyExists()
case class RequestOldMessages()
case class ArchivedMessage(time: OffsetDateTime, text: String, user: User)
case object ArchiveRoom
