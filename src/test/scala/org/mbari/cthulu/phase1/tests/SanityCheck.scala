package org.mbari.cthulu.phase1.tests

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
/**
 * @author Brian Schlining
 * @since 2020-03-02T14:48:00
 */
class SanityCheck extends AnyFlatSpec with Matchers {

  "SanityCheck" should "pass" in {
    "1" should be ("1")
  }
}


