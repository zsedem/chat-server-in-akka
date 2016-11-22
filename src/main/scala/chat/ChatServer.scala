/*
 * Blindspotter (tm)
 *
 * Copyright (c) 2016 BalaBit
 * All rights reserved.
 */
package chat

import akka.actor.{Actor, Props}

class ChatServer extends Actor {
  override def receive: Receive = {
    case _: CreateRoom =>
      val newChatRoom = Room(context.actorOf(Props {
        new ChatRoom()
      }))
      sender() ! newChatRoom
      newChatRoom.!(Enter())(sender())

    case _ =>
      sender() ! User(sender())
  }
}

