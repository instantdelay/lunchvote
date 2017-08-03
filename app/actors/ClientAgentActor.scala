package actors

import actors.ElectionActor._
import actors.LobbyActor.{Join, OccupantsChanged}
import akka.actor.{Actor, ActorRef, Props}
import play.api.libs.json.{JsObject, JsValue, Json, Writes}

import scala.reflect.ClassTag

object ClientAgentActor {
  def props(lobby:ActorRef, out:ActorRef) = Props(new ClientAgentActor(lobby, out))
}

case class JsNominate(name:String)

/**
  * Receives messages from a client and acts on behalf of the client. Sends messages back to the client through `out`
  * @param out
  */
class ClientAgentActor(clerk:ActorRef, out:ActorRef) extends Actor {

//  implicit val actorRefWrites = new Writes[ActorRef] {
//    def writes(bar: ActorRef) = {
//      Json.obj(
//        "event" -> "nominated",
//        "name" -> bar.toString()
//      )
//    }
//  }
//  implicit val nominationWrites = new Writes[NominationData] {
//    def writes(d:NominationData) = Json.obj(
//      "state" -> "nominate",
//      "noms" -> d.noms
//    )
//  }
//  implicit val w1 = Json.writes[Nominated]
//  def conditionalWriterOf[T: Writes](implicit t:ClassTag[T]): Any => Option[JsValue] = (obj:Any) => {
//    if (t.runtimeClass.isInstance(obj)) {
//      Some(implicitly[Writes[T]].writes(obj.asInstanceOf[T]))
//    }
//    else None
//  }
//  val writers = Seq(
//    conditionalWriterOf[NominationData],
//    conditionalWriterOf[Nominated]).toIterator

  context.system.eventStream.subscribe(self, classOf[ElectionEvent])
  clerk ! AskStatus

  override def receive = {
    case clientMsg:JsObject => {
      (clientMsg \ "action").as[String] match {
        case "nominate" =>
          clerk ! Nominate((clientMsg \ "name").as[String])
      }
    }
    case d:NominationData =>
      out ! Json.obj(
        "state" -> "nominate",
        "noms" -> d.noms
      )
    case e:Nominated =>
      out ! Json.obj(
        "event" -> "nominated",
        "name" -> e.name
      )
//    case other =>
//      out ! writers.flatMap(_(other)).find(_=>true)
//        .getOrElse(Json.toJson("no_converter" -> other.toString))
  }

}
