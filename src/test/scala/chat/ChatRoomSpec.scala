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
      val chatRoom: Room = getRoom

      chatRoom ! SendMessage(text)

      expectMsg(RoomMessage(text, chatRoom, User(self)))
    }
    "should get message sent by others" in {
      val chatRoom: Room = getRoom
      val otherGuy = getOtherGuy(chatRoom)

      otherGuy.send(chatRoom.ref, SendMessage(text))

      expectMsg(RoomMessage(text, chatRoom, User(otherGuy.ref)))
    }
  }
  "leave" - {
    "should not get further messages after i leave" in {
      val chatRoom = getRoom
      chatRoom ! Leave()

      val otherGuy = getOtherGuy(chatRoom)
      otherGuy.send(chatRoom.ref, SendMessage(text))

      expectNoMsg()
    }
    "should reject my messages after i leave" in {
      val chatRoom = getRoom
      chatRoom ! Leave()

      val msg = SendMessage(text)
      chatRoom ! msg

      expectMsg(Rejected(msg))
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
