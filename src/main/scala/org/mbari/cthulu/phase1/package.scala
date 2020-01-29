package org.mbari.cthulu

import org.mbari.vcr4j.VideoIO
import org.mbari.vcr4j.sharktopoda.{SharktopodaError, SharktopodaState}

/**
  * @author Brian Schlining
  * @since 2020-01-28T14:49:00
  */
package object phase1 {

  type SIO = VideoIO[SharktopodaState, SharktopodaError]

}
