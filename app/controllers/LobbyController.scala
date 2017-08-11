package controllers

import javax.inject.Inject

import actors.Clerk.{BeginVoting, ResetElection}
import actors.ClientAgentActor
import akka.actor.ActorSystem
import akka.stream.Materializer
import akka.util.Timeout
import play.api.libs.json.JsValue
import play.api.libs.streams.ActorFlow
import play.api.mvc.{AbstractController, ControllerComponents, WebSocket}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

class LobbyController @Inject()(cc: ControllerComponents)(implicit actorSystem:ActorSystem, mat:Materializer, ec:ExecutionContext) extends AbstractController(cc) {

  implicit val timeout = Timeout(5 seconds)

  def index = Action {
    //actorSystem.actorSelection("/user/lol") ! Nominate("Olive Garden")
    Ok(views.html.index.render("z"))
  }

  def stream = WebSocket.acceptOrResult[JsValue, JsValue] { request =>
    for (clerk <- actorSystem.actorSelection("/user/clerk").resolveOne()) yield {
      Right(ActorFlow.actorRef { out =>
        ClientAgentActor.props(clerk, out)
      })
    }
  }

  def beginVoting = Action {
    actorSystem.actorSelection("/user/clerk") ! BeginVoting
    Ok("")
  }

  def reset = Action {
    actorSystem.actorSelection("/user/clerk") ! ResetElection
    Ok("")
  }

}
