import sbt._

object Dependencies {
  lazy val circeConfig      = "io.circe"        %% "circe-config"     % "0.7.0"
  lazy val config           = "com.typesafe"    % "config"            % "1.4.0"
  lazy val logback = "ch.qos.logback" % "logback-classic" % "1.2.3"
  lazy val picocli          = "info.picocli"    % "picocli"           % "4.1.4"
  lazy val scalaTest        = "org.scalatest"   %% "scalatest"        % "3.1.0"
  lazy val slf4j = "org.slf4j" % "slf4j-api" % "1.7.30"
  lazy val vcr4jSharktopoda = "org.mbari.vcr4j" % "vcr4j-sharktopoda" % "4.2.3.jre11-SNAPSHOT"
}
