package org.mbari.cthulu.phase1.tests

import java.net.URL
import java.time.Duration
import java.util.UUID
import java.util.concurrent.Executors
import java.util.concurrent.atomic.{AtomicInteger, AtomicReference}

import org.mbari.cthulu.phase1.SIO
import org.mbari.vcr4j.commands.{SeekElapsedTimeCmd, VideoCommands}
import org.mbari.vcr4j.{VideoIO, VideoIndex}
import org.mbari.vcr4j.decorators.SchedulerVideoIO
import org.mbari.vcr4j.sharktopoda.SharktopodaVideoIO
import org.mbari.vcr4j.sharktopoda.commands.{OpenCmd, SharkCommands}

import scala.jdk.OptionConverters._
import scala.util.{Random, Try}

/**
 * @author Brian Schlining
 * @since 2020-01-29T14:28:00
 */
class SeekAndRequestVideoIndex(mrl: URL, port: Int, uuid: UUID = UUID.randomUUID())
  extends AcceptanceTest[Throwable, Unit] {

  private[this] val tolerance: Int = Math.round(1 / 30D * 1000).toInt
  private[this] val lastIndexRequest = new AtomicReference[Duration](Duration.ZERO)
  private[this] val counter = new AtomicInteger(0)

  @transient
  private var passed: Boolean = true

  override def name: String = "Seek and request video index"

  override def apply(): Either[Throwable, Unit] = {

    val t = Try {
      val executor = Executors.newSingleThreadExecutor()
      val io = new SchedulerVideoIO(new SharktopodaVideoIO(uuid, "localhost", port), executor)
      io.getIndexObservable
        .subscribe(i => handleIndex(i))

      io.send(new OpenCmd(mrl))
      Thread.sleep(3000)
      io.send(VideoCommands.PAUSE)
      io.send(SharkCommands.SHOW)
      var n = 0
      for {
        i <- 0 to 10
      } {
        val dt = Random.between(0, 4000)
        val last = lastIndexRequest.get()
        val next = last.plusMillis(dt)
        request(next, io)
        n = n + 1
        Thread.sleep(500)
      }

      io.close()
      executor.shutdown()
      if (!passed)
        throw new RuntimeException("Seek test failed. A videoindex was not correct")

      if (counter.get() != n)
        throw new RuntimeException(s"Sent ${n} videoindex requests. Received ${counter.get()} reponses")
    }
    t.toEither

  }

  private def handleIndex(i: VideoIndex): Unit = {
    counter.incrementAndGet()
    i.getElapsedTime.toScala match {
      case None =>
        info("A VideoIndex is missing an elapsedTime value")
        passed = false
      case Some(actual) =>
        val expected = lastIndexRequest.get()
        val ok = checkVideoIndex(expected, actual, tolerance)
        if (!ok) {
          passed = false
          info(s"VideoIndex is incorrect. Expected ${expected.toMillis}, found ${actual.toMillis}")
        }
    }
  }

  private def request(t: Duration, io: SIO): Unit = {
    lastIndexRequest.set(t)
    io.send(new SeekElapsedTimeCmd(t))
    Thread.sleep(100)
    io.send(VideoCommands.REQUEST_ELAPSED_TIME)
  }

  private def checkVideoIndex(expected: Duration, actual: Duration, toleranceMillis: Int): Boolean = {
    val delta = Math.abs(expected.toMillis - actual.toMillis)
    delta <= toleranceMillis
  }
}
