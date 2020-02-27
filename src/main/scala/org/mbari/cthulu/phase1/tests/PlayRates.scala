package org.mbari.cthulu.phase1.tests

import java.net.URL
import java.util.UUID

import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers
import org.mbari.cthulu.phase1.SIO
import org.mbari.vcr4j.VideoCommand
import org.mbari.vcr4j.commands.{ShuttleCmd, VideoCommands}
import org.mbari.vcr4j.sharktopoda.SharktopodaState
import org.mbari.vcr4j.sharktopoda.SharktopodaState.State
import org.mbari.vcr4j.sharktopoda.commands.{OpenCmd, SharkCommands}
import org.slf4j.LoggerFactory

/**
 * @author Brian Schlining
 * @since 2020-02-10T10:27:00
 */
class PlayRates(mrl: URL, port: Int, uuid: UUID = UUID.randomUUID())
  extends StandardTest[Unit](mrl, port, uuid) {

  private[this] val log = LoggerFactory.getLogger(getClass)

  @volatile
  private var passed: Boolean = true

  @volatile
  private var expectedState: State = State.NOT_FOUND

  @volatile
  private var state: State = State.NOT_FOUND

  override def test(io: SIO): Unit = {
    io.getStateObservable
        .observeOn(Schedulers.io())
        .subscribe(s => handleState(s))
    io.send(new OpenCmd(mrl))

    send(io, VideoCommands.PLAY, State.PLAYING)
    send(io, VideoCommands.PAUSE, State.PAUSED)
    send(io, VideoCommands.PLAY, State.PLAYING)
    send(io, new ShuttleCmd(0.5), State.SHUTTLE_FORWARD)
    send(io, VideoCommands.PAUSE, State.PAUSED)
    send(io, VideoCommands.PLAY, State.PLAYING)
    send(io, new ShuttleCmd(0.9), State.SHUTTLE_FORWARD)
    send(io, VideoCommands.PAUSE, State.PAUSED)
    send(io, VideoCommands.PLAY, State.PLAYING)
    send(io, new ShuttleCmd(-.05), State.SHUTTLE_REVERSE)
    send(io, VideoCommands.PAUSE, State.PAUSED)
    send(io, VideoCommands.PLAY, State.PLAYING)
    send(io, new ShuttleCmd(-0.9), State.SHUTTLE_REVERSE)
    send(io, VideoCommands.PLAY, State.PLAYING)
    send(io, VideoCommands.PAUSE, State.PAUSED)
    io.send(SharkCommands.CLOSE)

  }

  private def send[A](io: SIO, cmd: VideoCommand[A], expectedState: SharktopodaState.State): Unit = {
    this.expectedState = expectedState
    io.send(cmd)
    Thread.sleep(400)
    io.send(VideoCommands.REQUEST_STATUS)
    Thread.sleep(400)
  }

  override def name: String = "Set and test playrates"

  private def handleState(state: SharktopodaState): Unit = {
    this.state = state.getState
    if (this.state != expectedState) {
      passed = false
      log.warn(s"--- Expected state: $expectedState but found ${this.state}")
    }
  }

}
