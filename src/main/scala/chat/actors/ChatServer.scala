/*
 * Blindspotter (tm)
 *
 * Copyright (c) 2016 BalaBit
 * All rights reserved.
 */
package chat.actors

import akka.actor.{FSM, Props}
import akka.actor.ActorRef

class ChatServer extends FSM[ChatServer.State, ChatServer.Data] {
  import ChatServer._
  startWith(Active, ChatServer.Data(Map()))
  when(Active) {
    case Event(Register(userName), data: Data) => {
      if (data.users.contains(userName)) {
        sender ! RejectedUserAlreadySignedUp(Register(userName))
        stay
      } else {
        sender ! RegistrationSuccessful(Register(userName))
        stay using Data(data.users + (userName -> None))
      }
    }
    case Event(Login(userName), data: Data) => {
      // if not registered
      if (!data.users.contains(userName)) {
        sender ! RejectedUserNotRegistered(Login(userName))
        stay
      } else if (data.users(userName) != None) {
        sender ! RejectedUserAlreadyLoggedIn(Login(userName))
        stay
      } else {
        sender ! CurrentRooms(List())
        stay using (Data(data.users + (userName -> Some(sender))))
      }
    }
    case Event(CreateRoom(roomName), data: Data) => {
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

  private[actors] case class Data(users: Map[String, Option[ActorRef]])
}
