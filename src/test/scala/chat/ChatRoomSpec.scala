/*
 * Blindspotter (tm)
 *
 * Copyright (c) 2016 BalaBit
 * All rights reserved.
 */
package chat

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestActorRef, TestKit, TestProbe}
import org.scalatest.{FreeSpecLike, Matchers}

class ChatRoomSpec
  extends TestKit(ActorSystem("ChatServerUnit"))
    with FreeSpecLike
    with Matchers
    with ImplicitSender {
  "entered" - {
    "should get messages sent by me" in {

    }
    "should get message sent by others" in {

    }
  }
  "leave" - {
    "should not get further messages after i leave" in {

    }
    "should reject my messages after i leave" in {

    }
  }

  private def getOtherGuy(chatRoom: Room) = {
    val probe = TestProbe("other")
    probe.send(chatRoom.ref, Enter())
    probe
  }

  val text = "some idiot message"

  private def getRoom = {
    val chatRoom = Room(TestActorRef(new ChatRoom()))
    chatRoom ! Enter()
    chatRoom
  }
}
