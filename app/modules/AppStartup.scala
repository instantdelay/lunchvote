package modules

import javax.inject.Inject

import actors.ElectionActor
import akka.actor.ActorSystem

class AppStartup @Inject()(actorSystem:ActorSystem) {

  //val lobby = actorSystem.actorOf(LobbyActor.props, "lobby")
  val election = actorSystem.actorOf(ElectionActor.props, "clerk")

}
