package com.qbrainx.repo.impl

import com.qbrainx.AppConfig.slickConfig
import com.qbrainx.model.User
import com.qbrainx.repo.UserRepo
import slick.lifted.ProvenShape

import scala.concurrent.duration.{Duration, FiniteDuration}
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.util.Try

object UserRepoImpl {

  import slickConfig.db
  import slickConfig.profile.api._

  lazy val TableName: String = "Users"
  lazy val TimeoutDuration: Duration = FiniteDuration(30, "seconds")

  def apply()(implicit ec: ExecutionContext): UserRepoImpl =
    Try(Await.result(db.run(query.schema.createIfNotExists).map(_ => new UserRepoImpl()), TimeoutDuration)).get

  private class Schema(tag: Tag) extends Table[User](tag, TableName) {

    def id: Rep[String] = column[String]("ID", O.PrimaryKey)

    def fName: Rep[String] = column[String]("F_NAME")

    def lName: Rep[String] = column[String]("L_NAME")

    def mobile: Rep[Option[String]] = column[Option[String]]("MOBILE")

    override def * : ProvenShape[User] = (id, fName, lName, mobile) <> (User.tupled, User.unapply)
  }

  private lazy val query: TableQuery[Schema] = TableQuery(new Schema(_))
}

final class UserRepoImpl private(implicit val ec: ExecutionContext) extends UserRepo {

  import UserRepoImpl._
  import slickConfig.db
  import slickConfig.profile.api._

  override def insert(user: User): Future[Unit] = db.run(query += user).map(_ => ())

  override def getUser(id: String): Future[Option[User]] = db.run(query.filter(_.id === id).result.headOption)

  override def updateMobile(id: String, mobile: String): Future[Unit] = {
    if (mobile.isEmpty || Option(mobile).isEmpty) {
      throw new Exception(s"Mobile number can not be null or empty, failed to update mobile number for id $id")
    }
    db.run(query.filter(_.id === id).map(_.mobile).update(Some(mobile))).map(_ => ())
  }

  override def removeMobileNumber(id: String): Future[Unit] =
    db.run(query.filter(_.id === id).map(_.mobile).update(None)).map(_ => ())

  override def deleteUser(id: String): Future[Unit] =
    db.run(query.filter(_.id === id).delete).map(_ => ())

  override def getAllUsers: Future[List[User]] = db.run(query.result).map(_.toList)
}
