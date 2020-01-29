package org.mbari.cthulu.phase1.tests

import java.net.URL
import java.util.UUID

import org.mbari.cthulu.phase1.SIO
import org.mbari.vcr4j.commands.VideoCommands
import org.mbari.vcr4j.sharktopoda.commands.{OpenCmd, SharkCommands}

import scala.util.Try

class OpenPlayAndClose(mrl: URL, port: Int, uuid: UUID = UUID.randomUUID())
    extends StandardTest[Unit](mrl, port, uuid) {

  override def name: String = "Open, Play, and Close"

  override def test(io: SIO): Unit = {
    io.send(new OpenCmd(mrl))
    Thread.sleep(3000)
    io.send(SharkCommands.SHOW)
    io.send(VideoCommands.PLAY)
    Thread.sleep(3000)
    io.send(VideoCommands.STOP)
    Thread.sleep(1000)
    io.send(SharkCommands.CLOSE)
  }

}
