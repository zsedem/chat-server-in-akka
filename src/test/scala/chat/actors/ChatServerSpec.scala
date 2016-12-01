/*
 * Blindspotter (tm)
 *
 * Copyright (c) 2016 BalaBit
 * All rights reserved.
 */
package chat.actors

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestActorRef, TestKit}
import org.scalatest.{FreeSpecLike, Matchers}

import scala.reflect.ClassTag

class ChatServerSpec
    extends TestKit(ActorSystem("ChatServerUnit"))
    with FreeSpecLike
    with Matchers
    with ImplicitSender {
  "sign up message" - {
    "users can register" in {
      val server = getServer
      server ! Register("zsedem")
      expectMsg(RegistrationSuccessful(Register("zsedem")))
    }
    "should fail if user already exists" in {
      val server = getServer
      server ! Register("zsedem")
      expectMsg(RegistrationSuccessful(Register("zsedem")))
      server ! Register("zsedem")
      expectMsg(RejectedUserAlreadySignedUp(Register("zsedem")))
    }
  }
  "login message" - {
    "should answered with a list of rooms" in {
      val server = getServer
      server ! Register("zsedem")
      expectMsg(RegistrationSuccessful(Register("zsedem")))
      server ! Login("zsedem")
      expectMsg(CurrentRooms(List()))
    }
    "should not allow user with same name" in {
      val server = getServer
      server ! Register("zsedem")
      expectMsg(RegistrationSuccessful(Register("zsedem")))
      server ! Login("zsedem")
      expectMsg(CurrentRooms(List()))
      server ! Login("zsedem")
      expectMsg(RejectedUserAlreadyLoggedIn(Login("zsedem")))
    }
    "non-registered users cannot log in" in {
      val server = getServer
      server ! Login("zsedem")
      expectMsg(RejectedUserNotRegistered(Login("zsedem")))
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
  }

  def receiveNext[T]()(implicit classTag: ClassTag[T]): T = {
    val x = receiveN(1).head
    x shouldBe a[T]
    x.asInstanceOf[T]
  }
  private def getServer = TestActorRef({ new ChatServer() })
}
