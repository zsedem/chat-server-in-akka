/*
 * Blindspotter (tm)
 *
 * Copyright (c) 2016 BalaBit
 * All rights reserved.
 */
package chat.actors

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestActorRef, TestKit, TestProbe}
import org.scalatest.{FreeSpecLike, Matchers}

import scala.concurrent.duration._
import scala.reflect.ClassTag

class ChatRoomSpec
    extends TestKit(ActorSystem("ChatServerUnit"))
    with FreeSpecLike
    with Matchers
    with ImplicitSender {

  private val commonMsg = SendMessage("message")
  "entered" - {
    "should get messages sent by me" in {
      val room = getRoom
      room ! commonMsg
      expectMsg(RoomMessage("message", room, User(self)))
    }
    "should get message sent by others" in {
      val room = getRoom
      val other: TestProbe = getOtherGuy(room)
      other.send(room.ref, commonMsg)
      expectMsg(RoomMessage("message", room, User(other.ref)))
    }
  }

  "leave" - {
    "should not get further messages after i leave" in {
      val room = getRoom
      val other: TestProbe = getOtherGuy(room)
      room ! Leave()
      other.send(room.ref, commonMsg)
      expectNoMsg()
    }
    "should reject my messages after i leave" in {
      val room = getRoom
      room ! Leave()
      room ! commonMsg
      expectMsg(Rejected(commonMsg))
    }
  }

  "request historic messages" - {
    "should get messages historically on request" in {
      val room = getPreviouslyActiveRoom
      room ! RequestOldMessages()
      val archivedMessage = receiveNext[ArchivedMessage]()
      archivedMessage.text shouldEqual commonMsg.text
    }
  }

  "archived" - {
    "should not accept any messages after archived" in {
      val room = getArchivedRoom
      room ! commonMsg
      expectMsg(Rejected(commonMsg))
    }
    "should get messages historically on request" in {
      val room = getPreviouslyActiveRoom
      room ! ArchiveRoom
      room ! RequestOldMessages()
      val archivedMessage = receiveNext[ArchivedMessage]()
      archivedMessage.text shouldEqual commonMsg.text
    }
  }

  private def getArchivedRoom: Room = {
    val room = getRoom
    room ! ArchiveRoom
    room
  }

  def getPreviouslyActiveRoom: Room = {
    val room = getRoom
    room ! commonMsg
    receiveOne(5 milliseconds)
    room ! Leave()
    room
  }

  private def getOtherGuy(chatRoom: Room) = {
    val probe = TestProbe("other")
    probe.send(chatRoom.ref, Enter())
    probe
  }

  val text = "some idiot message"

  private def getRoom = {
    val name = "testRoom"
    val chatRoom = Room(TestActorRef(new ChatRoom(name)), name)
    chatRoom ! Enter()
    chatRoom
  }

  def receiveNext[T]()(implicit classTag: ClassTag[T]): T = {
    val x = receiveN(1).head
    x shouldBe a[T]
    x.asInstanceOf[T]
  }
}
