/*
 * Blindspotter (tm)
 *
 * Copyright (c) 2016 BalaBit
 * All rights reserved.
 */
package chat

import akka.actor.Actor

class ChatRoom extends Actor {
  var subscribes: Set[User] = Set()
  override def receive: Receive = {
    case Enter() =>
      val user = User(sender())
      subscribes += user
    case SendMessage(text) if subscribes contains User(sender()) =>
      subscribes foreach { ref =>
        ref ! RoomMessage(text, Room(self), User(sender()))
      }
    case msg: SendMessage =>
      sender() ! Rejected(msg)
    case Leave() =>
      subscribes -= User(sender())

  }
}
