/*
 * Blindspotter (tm)
 *
 * Copyright (c) 2016 BalaBit
 * All rights reserved.
 */
package chat

import akka.actor.Actor

class ChatRoom extends Actor {

  override def receive: Receive = {
    case Enter() =>

    case msg: SendMessage =>

    case Leave() =>

  }
}
