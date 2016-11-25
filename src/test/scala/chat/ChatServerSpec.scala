/*
 * Blindspotter (tm)
 *
 * Copyright (c) 2016 BalaBit
 * All rights reserved.
 */
package chat

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestActorRef, TestKit}
import chat.actors._
import org.scalatest.{FreeSpec, FreeSpecLike, Matchers}

import scala.reflect.ClassTag

class ChatServerSpec
    extends TestKit(ActorSystem("ChatServerUnit"))
    with FreeSpecLike
    with Matchers
    with ImplicitSender {
  "login message" - {
    "should answered with a list of rooms" in {
      val server = getServer
      server ! Login("zsedem")
      expectMsg(CurrentRooms(List()))
    }
  }
  "create rooms" - {
    "if a room created it should answer with a room" in {
      val server = getServer
      server ! CreateRoom("bsp")
      receiveNext[Room]()
    }
    "room created twice with same name is rejected" in {
      val server = getServer
      server ! CreateRoom("bsp")
      receiveNext[Room]()
      server ! CreateRoom("bsp")
      expectMsg(AlreadyExists())
    }
    "messages sent to created room are forwarded to me" in {

    }
  }

  def receiveNext[T]()(implicit classTag: ClassTag[T]): T = {
    val x = receiveN(1).head
    x shouldBe a[T]
    x.asInstanceOf[T]
  }
  private def getServer = TestActorRef({ new ChatServer() })
}
