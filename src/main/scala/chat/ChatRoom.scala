/*
 * Blindspotter (tm)
 *
 * Copyright (c) 2016 BalaBit
 * All rights reserved.
 */
package chat

import akka.actor.Actor

import scala.concurrent.Future

class ChatRoom extends Actor {
  var users = Set[User]()
  override def receive: Receive = {
    case Enter() =>
      users = users + User(sender)

    case msg: SendMessage =>
      if (users.contains(User(sender))) {
        users foreach { case User(user) =>
          user ! RoomMessage(msg.text, Room(self), User(sender))
        }
      } else {
        sender ! Rejected(msg)
      }

    case Leave() =>
      users -= User(sender)
  }
}
