package org.mbari.cthulu.phase1.tests

import java.io.File
import java.net.URL
import java.time.Duration
import java.util.UUID
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean

import org.mbari.vcr4j.commands.{SeekElapsedTimeCmd, VideoCommands}
import org.mbari.vcr4j.decorators.SchedulerVideoIO
import org.mbari.vcr4j.sharktopoda.SharktopodaVideoIO
import org.mbari.vcr4j.sharktopoda.commands.{FramecaptureCmd, OpenCmd, SharkCommands}
import org.mbari.vcr4j.sharktopoda.decorators.FramecaptureDecorator

import scala.util.Try

/**
 * @author Brian Schlining
 * @since 2020-01-28T16:41:00
 */
class Framecapture(mrl: URL, port: Int, uuid: UUID = UUID.randomUUID())
  extends AcceptanceTest[Throwable, Unit]  {
  override def name: String = "Framecapture"

  override def apply(): Either[Throwable, Unit] = {
    val t = Try {
      val executor = Executors.newSingleThreadExecutor()
      val sio = new SharktopodaVideoIO(uuid, "localhost", port)
      val decorator = new FramecaptureDecorator(sio, 5000)
      decorator.getFramecaptureObservable
        .forEach(r => info(s"Saved image to ${r.getImageLocation.toString}"))
      val io       = new SchedulerVideoIO(sio, executor)

      val wait = new AtomicBoolean(true)
      io.getStateObservable
          .filter(_.isConnected)
          .take(1)
          .forEach(_ => {
            info(s"The video at $mrl is ready to play")
            wait.set(false)
          })


      io.send(new OpenCmd(mrl))

      while(wait.get()) {
        Thread.sleep(1000)
      }

      io.send(new FramecaptureCmd(UUID.randomUUID, new File("trashme-0.png")))

      for {
        i <- 0 to 5
      } {
        io.send(new SeekElapsedTimeCmd(Duration.ofSeconds(i * 3)))
        Thread.sleep(500)
        io.send(new FramecaptureCmd(UUID.randomUUID(), new File(s"trashme-seek-$i.png")))
      }

      io.send(VideoCommands.PLAY)
      for {
        i <- 0 to 5
      } {
        Thread.sleep(1000)
        io.send(new FramecaptureCmd(UUID.randomUUID(), new File(s"trashme-play-$i.png")))
      }

      io.send(VideoCommands.STOP)
      io.send(SharkCommands.CLOSE)
      io.close()
      executor.shutdown()
    }

    t.toEither

  }
}
