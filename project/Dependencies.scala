import sbt._

object Dependencies {
  lazy val circeConfig      = "io.circe"        %% "circe-config"            % "0.7.0"
  lazy val config           = "com.typesafe"    % "config"                   % "1.4.0"
  lazy val jansi = "org.fusesource.jansi" % "jansi" % "1.18"
  lazy val logback          = "ch.qos.logback"  % "logback-classic"          % "1.2.3"
  lazy val picocli          = "info.picocli"    % "picocli"                  % "4.2.0"
  lazy val scalactic        = "org.scalactic"   %% "scalactic"               % "3.1.1"
  lazy val scalaTest        = "org.scalatest"   %% "scalatest"               % "3.1.1"
  lazy val slf4j            = "org.slf4j"       % "slf4j-api"                % "1.7.30"
  lazy val vcr4jSharktopoda = "org.mbari.vcr4j" % "vcr4j-sharktopoda"        % "4.3.2.jre11-SNAPSHOT"
  lazy val vcr4jSharkClient = "org.mbari.vcr4j" % "vcr4j-sharktopoda-client" % "4.3.2.jre11-SNAPSHOT"
}
