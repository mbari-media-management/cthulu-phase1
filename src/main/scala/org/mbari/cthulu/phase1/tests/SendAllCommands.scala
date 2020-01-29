package org.mbari.cthulu.phase1.tests

import java.net.URL
import java.time.Duration
import java.util.UUID
import java.util.concurrent.Executors

import org.mbari.vcr4j.commands.{SeekElapsedTimeCmd, ShuttleCmd, VideoCommands}
import org.mbari.vcr4j.decorators.SchedulerVideoIO
import org.mbari.vcr4j.sharktopoda.SharktopodaVideoIO
import org.mbari.vcr4j.sharktopoda.commands.{OpenCmd, SharkCommands}

import scala.util.Try

/**
  * @author Brian Schlining
  * @since 2020-01-28T15:28:00
  */
class SendAllCommands(mrl: URL, port: Int, uuid: UUID = UUID.randomUUID())
    extends AcceptanceTest[Throwable, Unit] {

  override def name: String = "Send all commands"

  override def apply(): Either[Throwable, Unit] = {

    val t = Try {
      val executor = Executors.newSingleThreadExecutor()
      val io       = new SchedulerVideoIO(new SharktopodaVideoIO(uuid, "localhost", port), executor)
      io.getStateObservable
        .subscribe(state => info(s"STATE UPDATE -> ${state.getState}")) //stateCounter(state))

      def cmds =
        Seq(
          new OpenCmd(mrl),
          new SeekElapsedTimeCmd(Duration.ofMillis(6000)),
          VideoCommands.PLAY,
          SharkCommands.SHOW,
          SharkCommands.REQUEST_VIDEO_INFO,
          SharkCommands.REQUEST_ALL_VIDEO_INFOS,
          VideoCommands.REQUEST_ELAPSED_TIME,
          VideoCommands.REQUEST_INDEX,
          VideoCommands.REQUEST_STATUS,
          new SeekElapsedTimeCmd(Duration.ofMillis(12000)),
          new ShuttleCmd(0.5),
          new ShuttleCmd(-0.5),
          new ShuttleCmd(0.1),
          new ShuttleCmd(-0.1),
          VideoCommands.PAUSE,
          SharkCommands.CLOSE,
          VideoCommands.REQUEST_INDEX
        )

      cmds.foreach(c => {
        io.send(c)
        Thread.sleep(2000)
      })
      io.close()
      executor.shutdown()

    }

    t.toEither
  }
}
