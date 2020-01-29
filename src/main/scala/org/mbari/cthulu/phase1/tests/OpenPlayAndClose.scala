package org.mbari.cthulu.phase1.tests

import java.net.URL
import java.util.UUID
import java.util.concurrent.Executors

import org.mbari.vcr4j.commands.VideoCommands
import org.mbari.vcr4j.decorators.SchedulerVideoIO
import org.mbari.vcr4j.sharktopoda.SharktopodaVideoIO
import org.mbari.vcr4j.sharktopoda.commands.{OpenCmd, SharkCommands}

import scala.util.Try

class OpenPlayAndClose(mrl: URL, port: Int, uuid: UUID = UUID.randomUUID())
    extends AcceptanceTest[Throwable, Unit] {

  override def name: String = "Open, Play, and Close"

  override def apply(): Either[Throwable, Unit] = {
    val t = Try {
      val executor = Executors.newSingleThreadExecutor()
      val io       = new SchedulerVideoIO(new SharktopodaVideoIO(uuid, "localhost", port), executor)

      io.send(new OpenCmd(mrl))
      Thread.sleep(3000)
      io.send(SharkCommands.SHOW)
      io.send(VideoCommands.PLAY)
      Thread.sleep(3000)
      io.send(VideoCommands.STOP)
      Thread.sleep(1000)
      io.send(SharkCommands.CLOSE)
      io.close()
      executor.shutdown()
    }

    t.toEither
  }

}
