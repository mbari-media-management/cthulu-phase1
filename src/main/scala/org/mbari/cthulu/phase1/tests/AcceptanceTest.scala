package org.mbari.cthulu.phase1.tests

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

  def apply(): Either[E, V]

  def info(msg: String) = println(s"$noteChar  $msg")

  val failChar = "❌"
  val passChar = "✅"
  val noteChar = "ℹ️"

}
