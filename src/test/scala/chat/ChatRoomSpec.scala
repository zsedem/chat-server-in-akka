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

  private val msg = SendMessage("message")
  "entered" - {
    "should get messages sent by me" in {
      val room = getRoom
      room ! msg
      expectMsg(RoomMessage("message", room, User(self)))
    }
    "should get message sent by others" in {
      val room = getRoom
      val other: TestProbe = getOtherGuy(room)
      other.send(room.ref, msg)
      expectMsg(RoomMessage("message", room, User(other.ref)))
    }
  }
  "leave" - {
    "should not get further messages after i leave" in {
      val room = getRoom
      val other: TestProbe = getOtherGuy(room)
      room ! Leave()
      other.send(room.ref, msg)
      expectNoMsg()
    }
    "should reject my messages after i leave" in {
      val room = getRoom
      room ! Leave()
      room ! msg
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
