package org.mbari.cthulu.phase1

import java.io.File
import java.net.URL

import picocli.CommandLine.{Command, Parameters, Option => Opt}
import java.util.concurrent.Callable

import org.mbari.cthulu.phase1.tests.{Framecapture, OpenPlayAndClose, PlayRates, SeekAndRequestVideoIndex, SendAllCommands}
import picocli.CommandLine


/**
  *
  *
  */
@Command(
  description = Array("Run automated tests on cthulu"),
  name = "acceptance-test",
  mixinStandardHelpOptions = true,
  version = Array("0.0.1")
)
class AppRunner extends Callable[Integer] {

  @Opt(names = Array("-p", "--port"), description = Array("The udp control port for sharktopoda"))
  private var port: Int = 8800

  @Parameters(index = "0", description = Array("The movie url/file"))
  private var mrl: String = _
  private lazy val movieUrl: URL =
    if (mrl.startsWith("http:") || mrl.startsWith("file:")) new URL(mrl)
    else new File(mrl).toURI.toURL

  private[this] lazy val tests = Seq(
//    new OpenPlayAndClose(movieUrl, port),
//    new SendAllCommands(movieUrl, port),
//    new Framecapture(movieUrl, port),
//    new SeekAndRequestVideoIndex(movieUrl, port),
    new PlayRates(movieUrl, port)
  )

  def call(): Integer = {
    tests.foreach(_.run())
    0
  }
}

object App {
  def main(args: Array[String]): Unit = {
    println("Starting acceptance test")
    val exitCode = new CommandLine(new AppRunner).execute(args: _*)
    System.exit(exitCode)
  }
}
