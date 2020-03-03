package org.mbari.cthulu.phase1.tests

import java.{util => ju}
import java.util.Random
import java.time.Duration
import org.mbari.vcr4j.sharktopoda.client.localization.Localization

object LocalizationGenerator {

  val random = new Random

  val concepts = List(
    "bat ray",
    "bat star",
    "batfish",
    "Bathochordaeinae",
    "Bathochordaeus",
    "Bathochordaeus charon",
    "Bathochordaeus mcnutti",
    "Bathochordaeus sinker",
    "Bathochordaeus stygius",
    "Bathocyroe",
    "Bathocyroe fosteri",
    "Bathocyroidae",
    "Bathophilus",
    "Bathophilus brevis",
    "Bathophilus filifer",
    "Bathophilus flemingi",
    "Bathyagoninae",
    "Bathyagonus",
    "Bathyagonus nigripinnis",
    "Bathyagonus pentacanthus",
    "bathyal",
    "Bathyalcyon",
    "Bathyalcyon robustum",
    "Bathybembix",
    "Bathybembix bairdii",
    "Bathyceradocus",
    "Bathyceradocus sp. A",
    "Bathyceramaster",
    "Bathyceramaster careyi",
    "Bathyceramaster elegans",
    "Bathycongrus",
    "Bathycongrus macrurus",
    "Bathycrinidae",
    "Bathycrinus",
    "Bathycrinus complanatus",
    "Bathycrinus equatorialis",
    "Bathyctena",
    "Bathyctena chuni",
    "Bathyctenidae",
    "Bathydorididae"
  )

  def newLocalization(videoReferenceUuid: Option[ju.UUID] = None): Localization = {
    val concept          = concepts(random.nextInt(concepts.size))
    val elapseTime       = Duration.ofMillis(random.nextInt(100000))
    val localizationUuid = ju.UUID.randomUUID()
    val x                = random.nextInt(1920) + 1
    val y                = random.nextInt(1080) + 1
    val width            = random.nextInt(1920 - x + 1) + 1
    val height           = random.nextInt(1080 - y + 1) + 1
    val duration         = Duration.ofMillis(random.nextInt(20000))
    val annotationUuid   = ju.UUID.randomUUID()
    val localization = new Localization(
      concept,
      elapseTime,
      localizationUuid,
      x,
      y,
      width,
      height,
      duration,
      annotationUuid
    )
    videoReferenceUuid.foreach(localization.setVideoReferenceUuid)
    localization
  }

  def newLocalizations(n: Int, videoReferenceUuid: Option[ju.UUID]): Seq[Localization] =
    (0 until n).map(_ => newLocalization(videoReferenceUuid))

}
