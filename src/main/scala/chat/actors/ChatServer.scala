/*
 * Blindspotter (tm)
 *
 * Copyright (c) 2016 BalaBit
 * All rights reserved.
 */
package chat.actors

import akka.actor.{FSM, Props}
import akka.actor.ActorRef

class ChatServer extends FSM[ChatServer.State, Map[String, ActorRef]] {
  import ChatServer._
  startWith(Active, Map())
  when(Active) {
    case Event(Login(userName), users: Map[String, ActorRef]) => {
      if (users.contains(userName)) {
        sender ! RejectedUserAlreadyLoggedIn(Login(userName))
        stay using users
      }
      else {
        sender ! CurrentRooms(List())
        stay using (users + (userName -> sender))
      }
    }
    case Event(CreateRoom(roomName), users: Map[String, ActorRef]) => {
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
