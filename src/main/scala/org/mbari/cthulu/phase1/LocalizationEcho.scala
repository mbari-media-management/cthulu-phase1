package org.mbari.cthulu.phase1

import java.time.Duration

import org.mbari.vcr4j.sharktopoda.client.localization.Message

import scala.jdk.CollectionConverters._

/**
 * @author Brian Schlining
 * @since 2020-03-02T17:05:00
 */
object LocalizationEcho {

  type LIO = org.mbari.vcr4j.sharktopoda.client.localization.IO

  val lio = new LIO(5562, 5561, "localization", "localization")

  def main(args: Array[String]): Unit = {


    val thread = new Thread(() => lio.getController
      .getIncoming
      .ofType(classOf[Message])
      .filter(msg => Message.ACTION_ADD.equalsIgnoreCase(msg.getAction))
      .map(_.getLocalizations.asScala)
      .subscribe(xs => {
        val ys = xs.map(x => {
          x.setDuration(Duration.ofSeconds(1))
          x.setConcept("Modified!")
          x
        })
        lio.getController
          .getOutgoing
          .onNext(new Message(Message.ACTION_ADD, ys.asJava))
      }))
    thread.start()
    Thread.sleep(1000 * 60 * 60)

  }

}
