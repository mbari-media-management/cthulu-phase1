package org.mbari.cthulu.phase1

import java.io.File
import java.net.URL
import java.time.Duration
import java.util.UUID
import java.util.concurrent.Callable

import javafx.collections.ListChangeListener
import org.mbari.vcr4j.VideoIO
import org.mbari.vcr4j.sharktopoda.{SharktopodaError, SharktopodaState, SharktopodaVideoIO}
import org.mbari.vcr4j.sharktopoda.client.localization.{Localization, Message}
import org.mbari.vcr4j.sharktopoda.commands.OpenCmd
import picocli.CommandLine
import picocli.CommandLine.{Command, Parameters, Option => Opt}

import scala.jdk.CollectionConverters._

@Command(
  description = Array("Open a video, listen for bounding boxes and automatically update them"),
  name = "localization-echo",
  mixinStandardHelpOptions = true,
  version = Array("0.0.2")
)
class LocalizationEcho extends Callable[Integer] {

  type LIO = org.mbari.vcr4j.sharktopoda.client.localization.IO

  type SIO = VideoIO[SharktopodaState, SharktopodaError]

  @Opt(names = Array("-p", "--port"), description = Array("The udp control port for sharktopoda"))
  private var port: Int = 8800

  @Opt(names = Array("-i", "--incoming"), description = Array("The zeromq control port to subscribe to"))
  private var incomingPort: Int = 5562

  @Opt(names = Array("-s", "--intopic"), description = Array("The zeromq topic to subscribe to"))
  private var incomingTopic: String = "localization"

  @Opt(names = Array("-o", "--outgoing"), description = Array("The zeromq control port to publish to"))
  private var outgoingPort: Int = 5561

  @Opt(names = Array("-t", "--outtopic"), description = Array("The zeromq topic to publish to"))
  private var outgoingTopic: String = "localization"

  @Parameters(index = "0", description = Array("The movie url/file"))
  private var mrl: String = _
  private lazy val movieUrl: URL =
    if (mrl.startsWith("http:") || mrl.startsWith("file:")) new URL(mrl)
    else new File(mrl).toURI.toURL

  override def call(): Integer = {
    val lio = new LIO(incomingPort, outgoingPort, incomingTopic, outgoingTopic)
    val uuid = UUID.randomUUID()
    val sio = new SharktopodaVideoIO(uuid, "localhost", port)
    sio.send(new OpenCmd(movieUrl))
    val thread = new Thread(() => {
      val ctrl = lio.getController
      ctrl.getLocalizations
        .addListener(new ListChangeListener[Localization] {
          override def onChanged(c: ListChangeListener.Change[_ <: Localization]): Unit = {
            while (c.next()) {
              if (c.wasAdded()) {
                val added = c.getAddedSubList
                  .asScala
                  .filter(!_.getConcept.equals("Modified!"))
                  .map(x => {
                    x.setDuration(Duration.ofSeconds(1))
                    x.setConcept("Modified!")
                    x.setVideoReferenceUuid(uuid)
                    x.asInstanceOf[Localization]
                  })
                ctrl.addLocalizations(added.asJava)
              }
            }
          }
        })
    })

    thread.start()
    Thread.sleep(1000 * 60 * 60)
    0
  }
}



/**
 * @author Brian Schlining
 * @since 2020-03-02T17:05:00
 */
object LocalizationEcho {



  def main(args: Array[String]): Unit = {

    val exitCode = new CommandLine(new LocalizationEcho).execute(args:_*)
    System.exit(exitCode)

  }

}
