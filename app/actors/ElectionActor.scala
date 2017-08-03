package actors

import actors.ElectionActor._
import actors.LobbyActor.NotifyOf
import akka.actor.{Actor, ActorRef, FSM, Props}

object ElectionActor {
  def props() = Props[ElectionActor]

  final case object AskStatus
  final case class Nominate(name:String)

  sealed trait State
  case object Nominating extends State
  case object Voting extends State

  sealed trait Data
  case class NominationData(noms:Seq[String]) extends Data

  sealed trait ElectionEvent
  case class Nominated(name:String) extends ElectionEvent
}

class ElectionActor() extends FSM[State, Data] {

  startWith(Nominating, NominationData(List()))

  when(Nominating) {
    case Event(Nominate(name:String), NominationData(noms)) =>
      context.system.eventStream.publish(Nominated(name))
      stay using NominationData(noms :+ name)
    case Event(AskStatus, _) =>
      sender() ! stateData
      stay
  }

  when(Voting) {
    case Event(AskStatus, _) =>
      stay
  }

}
