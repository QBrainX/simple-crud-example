package com.qbrainx

import com.typesafe.config.{Config, ConfigFactory}
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

object AppConfig {

  lazy val config: Config = ConfigFactory.load()

  lazy val slickConfig: DatabaseConfig[JdbcProfile] = DatabaseConfig.forConfig("slick", config)

  lazy val httpHost: String = config.getString("http.host")
  lazy val httpPort: Int = config.getInt("http.port")
}
