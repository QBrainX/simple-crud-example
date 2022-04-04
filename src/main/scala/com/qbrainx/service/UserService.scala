package com.qbrainx.service

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.qbrainx.model.JsonFormat._
import com.qbrainx.model.User
import com.qbrainx.repo.UserRepo
import org.slf4j.LoggerFactory

import java.sql.SQLIntegrityConstraintViolationException
import scala.util.{Failure, Success}
import spray.json.DefaultJsonProtocol._
import spray.json.RootJsonFormat

object UserService {

  final case class GetAllUsersResponse(size: Int, users: List[User])

  implicit lazy val getAllUsersResponseJsonFormat: RootJsonFormat[GetAllUsersResponse] = jsonFormat2(GetAllUsersResponse)

  def route(userRepo: UserRepo): Route = new UserService(userRepo).routes
}

final class UserService private(userRepo: UserRepo) {

  import UserService._

  private lazy val log = LoggerFactory.getLogger(getClass)

  private def routes: Route = pathPrefix("user") {
    addUser ~ getUser ~ updateMobile ~ deleteMobile ~ deleteUser ~ getAllUsers
  }

  private def addUser: Route = path("addUser") {
    post {
      entity(as[User]) { user =>
        onComplete(userRepo.insert(user)) {
          case Success(_) => complete(StatusCodes.OK)
          case Failure(_: SQLIntegrityConstraintViolationException) => complete(StatusCodes.BadRequest, "User with id already exists")
          case Failure(exception) =>
            log.error(s"Failed inserting user :- $user", exception)
            complete(StatusCodes.InternalServerError, exception.getMessage)
        }
      }
    }
  }

  private def getUser: Route = path("getUser") {
    get {
      parameter("id".as[String]) { id =>
        onComplete(userRepo.getUser(id)) {
          case Success(Some(user)) => complete(user)
          case Success(None) => complete(StatusCodes.NotFound)
          case Failure(exception) =>
            log.error(s"Failed fetching user for id :- $id", exception)
            complete(StatusCodes.InternalServerError, exception.getMessage)
        }
      }
    }
  }

  private def updateMobile: Route = path("updateMobile") {
    put {
      parameter("id".as[String], "mobile".as[String]) { (id, mobile) =>
        onComplete(userRepo.updateMobile(id, mobile)) {
          case Success(_) => complete(StatusCodes.OK)
          case Failure(exception) =>
            log.error(s"Failed updating user mobile number for id :- $id", exception)
            complete(StatusCodes.InternalServerError, exception.getMessage)
        }
      }
    }
  }

  private def deleteMobile: Route = path("deleteMobile") {
    delete {
      parameter("id".as[String]) { id =>
        onComplete(userRepo.removeMobileNumber(id)) {
          case Success(_) => complete(StatusCodes.OK)
          case Failure(exception) =>
            log.error(s"Failed deleting user mobile number for id :- $id", exception)
            complete(StatusCodes.InternalServerError, exception.getMessage)
        }
      }
    }
  }

  private def deleteUser: Route = path("deleteUser") {
    delete {
      parameter("id".as[String]) { id =>
        onComplete(userRepo.deleteUser(id)) {
          case Success(_) => complete(StatusCodes.OK)
          case Failure(exception) =>
            log.error(s"Failed deleting user for id :- $id", exception)
            complete(StatusCodes.InternalServerError, exception.getMessage)
        }
      }
    }
  }

  private def getAllUsers: Route = path("getAllUsers") {
    get {
      onComplete(userRepo.getAllUsers) {
        case Success(result) => complete(GetAllUsersResponse(result.length, result))
        case Failure(exception) =>
          log.error(s"Failed fetching all users", exception)
          complete(StatusCodes.InternalServerError, exception.getMessage)
      }
    }
  }
}
