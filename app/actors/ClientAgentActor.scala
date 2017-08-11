package actors

import actors.Clerk._
import akka.actor.{Actor, ActorRef, Props}
import com.instantdelay.vote.Pref
import play.api.libs.json._
import play.api.libs.functional.syntax._

object ClientAgentActor {
  def props(lobby:ActorRef, out:ActorRef) = Props(new ClientAgentActor(lobby, out))
}

/**
  * Receives messages from a client and acts on behalf of the client. Sends messages back to the client through `out`
  * @param out
  */
class ClientAgentActor(clerk:ActorRef, out:ActorRef) extends Actor {

  context.system.eventStream.subscribe(self, classOf[ElectionEvent])
  context.system.eventStream.subscribe(self, classOf[Clerk.Data])
  clerk ! ReportState

  private implicit val prefReads: Reads[Pref[String]] = (
    (JsPath \ "id").read[String] and
    (JsPath \ "rank").read[Int]
  )(Pref.apply(_, _))
  private implicit val ballotReads = Json.reads[SubmitBallot]

  override def receive: Receive = {
    case clientMsg:JsObject =>
      (clientMsg \ "action").as[String] match {
        case "nominate" =>
          clerk ! Nominate((clientMsg \ "name").as[String])
        case "submitBallot" =>
          clerk ! ballotReads.reads(clientMsg).get
      }

    // States
    case d:NominationData =>
      out ! Json.obj(
        "state" -> "nominate",
        "noms" -> d.noms
      )
    case d:VotingData =>
      out ! Json.obj(
        "state" -> "vote",
        "noms" -> d.noms
      )

    // Election Events
    case e:Nominated =>
      out ! Json.obj(
        "event" -> "nominated",
        "name" -> e.name
      )
    case ResultsUpdate(results) =>
      out ! Json.obj(
        "event" -> "results",
        "results" -> results
      )

  }

}
