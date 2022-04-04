package com.qbrainx.repo

import com.qbrainx.model.User

import scala.concurrent.Future

trait UserRepo {

  def insert(user: User): Future[Unit]

  def getUser(id: String): Future[Option[User]]

  def updateMobile(id: String, mobile: String): Future[Unit]

  def removeMobileNumber(id: String): Future[Unit]

  def deleteUser(id: String): Future[Unit]

  def getAllUsers: Future[List[User]]
}
