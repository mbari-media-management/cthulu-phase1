package org.mbari.cthulu.phase1.tests

/**
 *
 * @tparam E The error type if the test fails
 * @tparam V The value type if the test succeds
 */
trait AcceptanceTest[E, V] extends Runnable {

  def name: String

  override def run(): Unit = {
    info(s"Starting '$name'")
    apply() match {
      case Left(e) =>
        println(s"$failChar  $name failed - $e")
      case Right(_) =>
        println(s"$passChar  '$name' passed")
    }
  }

  /**
   * THis method should contain the test code
   * @return
   */
  def apply(): Either[E, V]

  def info(msg: String) = println(s"$noteChar  $msg")

  val failChar = "❌"
  val passChar = "✅"
  val noteChar = "ℹ️"

}
