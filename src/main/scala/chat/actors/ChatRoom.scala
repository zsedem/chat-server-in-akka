/*
 * Blindspotter (tm)
 *
 * Copyright (c) 2016 BalaBit
 * All rights reserved.
 */
package chat.actors
import java.time.OffsetDateTime

import akka.actor.FSM

class ChatRoom(name: String) extends FSM[ChatRoom.State, ChatRoom.Data] {
  import ChatRoom._

  startWith(Active, ActiveRoomState())

  when(Active) {
    case Event(Enter(), st: ActiveRoomState) =>
      val user = User(sender)
      log.debug("{} user connected", user)
      stay using st.copy(users = st.users + user)

    case Event(msg: SendMessage, st: ActiveRoomState) =>
      val user = User(sender)
      if (st.users.exists(_.ref == sender)) {
        st.users foreach (_ ! RoomMessage(msg.text, Room(self, name), user))

        stay using st.copy(messages = ArchivedMessage(OffsetDateTime.now,
                                                      msg.text,
                                                      user) +: st.messages)
      } else {
        sender ! Rejected(msg)
        stay
      }

    case Event(RequestOldMessages(), ActiveRoomState(_, messages)) =>
      messages foreach (sender ! _)
      stay

    case Event(Leave(), st: ActiveRoomState) =>
      val user = User(sender)
      log.debug("{} user disconnected", user)
      stay using st.copy(users = st.users - user)
    case Event(ArchiveRoom, st: ActiveRoomState) =>
      goto(Archived) using (ArchivedMessages(st.messages))
  }

  when(Archived) {
    case Event(msg: SendMessage, _) =>
      sender() ! Rejected(msg)
      stay
    case Event(RequestOldMessages(), st: ArchivedMessages) =>
      st.messages foreach (sender ! _)
      stay
  }

  initialize()
}

object ChatRoom {
  private[actors] sealed trait State
  private[actors] case object Active extends State
  private[actors] case object Archived extends State

  private[actors] sealed trait Data
  private[actors] case class ArchivedMessages(messages: List[ArchivedMessage])
      extends Data

  private[actors] case class ActiveRoomState(users: Set[User] = Set(),
                                             messages: List[ArchivedMessage] =
                                               List())
      extends Data
}
