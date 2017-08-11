package actors

import actors.Clerk._
import akka.actor.{FSM, Props}
import com.instantdelay.vote.{Pref, RankBallot, Schulze}

object Clerk {
  def props():Props = Props[Clerk]

  // Commands
  final case object ReportState
  final case class Nominate(name:String)
  final case object BeginVoting
  final case class SubmitBallot(votes:Set[Pref[String]])
  final case object ResetElection

  // Events
  sealed trait ElectionEvent
  case class Nominated(name:String) extends ElectionEvent
  case class ResultsUpdate(results:Seq[String]) extends ElectionEvent

  // States
  sealed trait State
  case object Nominating extends State
  case object Voting extends State

  // Data
  sealed trait Data
  case class NominationData(noms:Set[String]) extends Data
  case class VotingData(noms:Set[String], votes:Seq[RankBallot[String]]) extends Data
}

class Clerk() extends FSM[State, Data] {

  private val pub = context.system.eventStream

  startWith(Nominating, NominationData(Set("Thai Boat", "Taco Bell", "Olive Garden")))

  when(Nominating) {
    case Event(Nominate(name:String), NominationData(noms)) =>
      if (!noms.contains(name))
        pub.publish(Nominated(name))
      stay using NominationData(noms + name)

    case Event(BeginVoting, NominationData(noms)) =>
      val data = VotingData(noms, Seq())
      pub.publish(data)
      goto(Voting) using data

    case Event(ReportState, _) =>
      sender() ! stateData
      stay
  }

  when(Voting) {
    case Event(SubmitBallot(votes), VotingData(noms, ballots)) =>
      val newBallots = ballots :+ RankBallot(votes)
//      val result = new Schulze().apply(JavaConverters.setAsJavaSet(noms), JavaConverters.seqAsJavaList(newBallots))
      val result = new Schulze[String].apply(noms, newBallots)
      println(result)
      pub.publish(ResultsUpdate(result))
      stay using VotingData(noms, newBallots)
    case Event(ReportState, _) =>
      sender() ! stateData
      stay
  }

  whenUnhandled {
    case Event(ResetElection, _) =>
      val newState = NominationData(Set());
      pub.publish(newState)
      goto(Nominating) using newState
  }

}
