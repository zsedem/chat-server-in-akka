/*
 * Blindspotter (tm)
 *
 * Copyright (c) 2016 BalaBit
 * All rights reserved.
 */
package chat.actors

import akka.actor.{Actor, Props}

class ChatServer extends Actor {
  override def receive: Receive = {
    case Login(_) =>
      sender ! CurrentRooms(List())

    case CreateRoom(roomName) =>
      if (context.child(roomName).isEmpty) {
        sender ! Room({
          val actorProps = Props {
            new ChatRoom(roomName)
          }
          context.actorOf(actorProps, roomName)
        }, roomName)
      } else {
        sender ! AlreadyExists()
      }

    case _ =>

  }
}

