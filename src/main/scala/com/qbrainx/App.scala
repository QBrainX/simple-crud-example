package com.qbrainx

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import com.qbrainx.repo.UserRepo
import com.qbrainx.repo.impl.UserRepoImpl
import com.qbrainx.service.UserService
import org.slf4j.LoggerFactory

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

object App extends scala.App {

  import AppConfig._

  private val log = LoggerFactory.getLogger(getClass)

  implicit val system: ActorSystem[_] = ActorSystem[Nothing](Behaviors.empty, "simple-crud-system", config)
  implicit val ec: ExecutionContext = system.executionContext

  val repo: UserRepo = UserRepoImpl()

  val route = UserService.route(repo)

  Http().newServerAt(httpHost, httpPort).bind(route).onComplete {
    case Success(value) => log.info(s"Http server started successfully $value")
    case Failure(exception) =>
      log.error("Failed to start http server", exception)
      system.terminate()
  }
}
