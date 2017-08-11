package modules

import javax.inject.Inject

import actors.Clerk
import akka.actor.ActorSystem

class AppStartup @Inject()(actorSystem:ActorSystem) {

  //val lobby = actorSystem.actorOf(LobbyActor.props, "lobby")
  val election = actorSystem.actorOf(Clerk.props, "clerk")

}
