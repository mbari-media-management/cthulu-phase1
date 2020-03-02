package org.mbari.cthulu.phase1.tests

import java.util.UUID
import org.mbari.cthulu.phase1.SIO
import org.mbari.vcr4j.commands.VideoCommands
import java.net.URL

/**
  * @author Brian Schlining
  * @since 2020-02-27T10:26:00
  */
class LocalizationTest(mrl: URL, port: Int, uuid: UUID = UUID.randomUUID())
    extends StandardTest[Unit](mrl, port, uuid) {
  val name: String = "Localization"

  def test(io: SIO): Unit = {
    io.send(VideoCommands.STOP)
    val localizations = LocalizationGenerator.newLocalizations(100, Option(uuid))
    

  }

}
