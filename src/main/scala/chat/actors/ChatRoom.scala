/*
 * Blindspotter (tm)
 *
 * Copyright (c) 2016 BalaBit
 * All rights reserved.
 */
package chat.actors

import akka.actor.{Actor, ActorLogging}

class ChatRoom(name: String) extends Actor with ActorLogging {
  var users = Set[User]()
  override def receive: Receive = {
    case Enter() =>
      val user = User(sender)
      users = users + user
      log.debug("{} user did connected", user)

    case msg: SendMessage =>
      if (users.exists(_.ref == sender)) {
        users foreach (_ ! RoomMessage(msg.text, Room(self, name), User(sender)))
      } else {
        sender ! Rejected(msg)
      }

    case Leave() =>
      users -= User(sender)
  }
}
