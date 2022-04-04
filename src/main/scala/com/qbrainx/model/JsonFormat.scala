package com.qbrainx.model

import spray.json.DefaultJsonProtocol._
import spray.json.RootJsonFormat


object JsonFormat {

  implicit lazy val userJsonFormat: RootJsonFormat[User] = jsonFormat4(User)
}
