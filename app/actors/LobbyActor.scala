package actors

import actors.LobbyActor._
import akka.actor.{Actor, ActorRef, Props}

object LobbyActor {
  def props = Props[LobbyActor]

  final case class Join(agent:ActorRef)
  final case class Leave(agent:ActorRef)
  final case class InitElection(election:ActorRef)
  final case class NotifyOf(message:Any)

  final case class OccupantsChanged(occupants:Set[ActorRef])
}

class LobbyActor extends Actor {

  var election:ActorRef = _
  var occupants:Set[ActorRef] = Set()

  override def receive = {
    case InitElection(election) =>
      this.election = election
    case Join(agent) =>
      occupants += agent
      agent ! election
      for (a <- occupants) a ! OccupantsChanged(occupants)
    case Leave(agent) =>
      occupants -= agent
      for (a <- occupants) a ! OccupantsChanged(occupants)
    case NotifyOf(msg) =>
      for (a <- occupants) a ! msg
  }

}
