package org.mbari.cthulu.phase1.tests

import java.net.URL
import java.util.UUID

import org.mbari.cthulu.phase1.SIO
import org.mbari.vcr4j.commands.VideoCommands
import scala.jdk.CollectionConverters._


/**
 * @author Brian Schlining
 * @since 2020-03-02T14:55:00
 */
class SetLocalizations(mrl: URL, port: Int, uuid: UUID = UUID.randomUUID())
  extends LocalizationTest(mrl, port, uuid) {
  val name: String = "Set Localizations"

  def test(io: SIO): Unit = {
    io.send(VideoCommands.STOP)

    // Create a sequence of localizations
    val localizations = newLocalizations(100)

    lio.getController
      .addLocalizations(localizations.asJava)
    
    io.send(VideoCommands.PLAY)

  }

}
