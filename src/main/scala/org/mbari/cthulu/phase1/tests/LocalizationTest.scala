package org.mbari.cthulu.phase1.tests

import java.util.UUID

import org.mbari.cthulu.phase1.SIO
import org.mbari.vcr4j.commands.VideoCommands
import java.net.URL
import java.time.Duration

import org.mbari.vcr4j.sharktopoda.client.localization.Localization



/**
  * @author Brian Schlining
  * @since 2020-02-27T10:26:00
  */
abstract class LocalizationTest(mrl: URL, port: Int, uuid: UUID = UUID.randomUUID())
    extends StandardTest[Unit](mrl, port, uuid) {

  type LIO = org.mbari.vcr4j.sharktopoda.client.localization.IO

  val lio = new LIO(5562, 5561, "localization", "localization")

  /**
   * Create n loclizations equally space in time starting at interval and
   *  ending at n * internval
   * @param n The number of localizations to generate
   * @param interval The time interval (elapsedTime) between them
   * @return The localizaitons with mostly random data in them
   */
  def newLocalizations(n: Int, interval: Duration = Duration.ofMillis(1000)): Seq[Localization] = {
    val localizations = LocalizationGenerator.newLocalizations(n, Option(uuid))
    localizations.zipWithIndex
      .foreach({
        case (v, i) => v.setElapsedTime(interval.multipliedBy(i))
      })
    localizations
  }


}
