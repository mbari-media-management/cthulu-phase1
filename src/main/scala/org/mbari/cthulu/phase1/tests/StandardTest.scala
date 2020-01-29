package org.mbari.cthulu.phase1.tests

import java.net.URL
import java.util.UUID
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean

import org.mbari.cthulu.phase1.SIO
import org.mbari.vcr4j.decorators.SchedulerVideoIO
import org.mbari.vcr4j.sharktopoda.SharktopodaVideoIO
import org.mbari.vcr4j.sharktopoda.commands.OpenCmd

import scala.util.Try

/**
 * @author Brian Schlining
 * @since 2020-01-29T15:40:00
 */
abstract class StandardTest[A](mrl: URL, port: Int, uuid: UUID = UUID.randomUUID())
  extends AcceptanceTest[Throwable, A] {

  def test(io: SIO): A

  override def apply(): Either[Throwable, A] = {
    val t = Try {
      val executor = Executors.newSingleThreadExecutor()
      val io       = new SchedulerVideoIO(new SharktopodaVideoIO(uuid, "localhost", port), executor)

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
        Thread.sleep(100)
      }
      val a = test(io)
      io.close()
      executor.shutdown()
      a
    }
    t.toEither
  }
}
