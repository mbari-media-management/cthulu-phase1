package org.mbari.cthulu.phase1.tests

import java.net.URL
import java.time.Duration
import java.util.UUID

import org.mbari.cthulu.phase1.SIO
import org.mbari.vcr4j.commands.VideoCommands
import org.mbari.vcr4j.sharktopoda.client.localization.Message


/**
 * @author Brian Schlining
 * @since 2020-03-02T14:54:00
 */
class AddSingleLocalizations(mrl: URL, port: Int, uuid: UUID = UUID.randomUUID())
  extends LocalizationTest(mrl, port, uuid) {
  val name: String = "Add multiple localizations one at a time"

  def test(io: SIO): Unit = {
    io.send(VideoCommands.STOP)
    Thread.sleep(3000)

    // Create a sequence of localizations
    val localizations = newLocalizations(100, Duration.ofMillis(2000))

    localizations.foreach(lio.getController.addLocalization)

    io.send(VideoCommands.PLAY)

    Thread.sleep(5000) // Let IO messages propagate

  }

}