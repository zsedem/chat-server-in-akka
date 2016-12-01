/*
 * Blindspotter (tm)
 *
 * Copyright (c) 2016 BalaBit
 * All rights reserved.
 */
package chat.actors

import akka.actor.{FSM, Props}

class ChatServer extends FSM[ChatServer.State, List[String]] {
  import ChatServer._
  startWith(Active, List())
  when(Active) {
    case Event(Login(_), _: List[String]) => {
      sender ! CurrentRooms(List())
      stay
    }
    case Event(CreateRoom(roomName), _: List[String]) => {
      if (context.child(roomName).isEmpty) {
        sender ! Room({
          val actorProps = Props({new ChatRoom(roomName)})
          context.actorOf(actorProps, roomName)
        }, roomName)
      } else {
        sender ! AlreadyExists()
      }
      stay
    }
  }

  initialize()
}

object ChatServer {
  private[actors] sealed trait State
  private[actors] case object Active extends State


}
