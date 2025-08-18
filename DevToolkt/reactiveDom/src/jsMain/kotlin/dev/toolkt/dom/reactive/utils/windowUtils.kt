package dev.toolkt.dom.reactive.utils

import dev.toolkt.dom.pure.PureSize
import dev.toolkt.reactive.Subscription
import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.event_stream.DependentEventStream
import dev.toolkt.reactive.event_stream.EventStream
import dev.toolkt.reactive.event_stream.fetch
import dev.toolkt.reactive.event_stream.getEventStream
import kotlinx.browser.window
import org.w3c.dom.Window
import org.w3c.dom.events.Event
import kotlin.time.Duration

typealias DOMHighResTimeStamp = Double

val Window.currentSize: PureSize
    get() = PureSize(
        width = this.innerWidth.toDouble(),
        height = this.innerHeight.toDouble(),
    )

fun Window.onResize(): EventStream<Event> = this.getEventStream("resize")

fun Window.trackSize(): Cell<PureSize> = onResize().fetch { this.currentSize }

fun Window.requestAnimationFrames(
    callback: (timestamp: DOMHighResTimeStamp) -> Unit,
): Subscription = object : Subscription {
    var handle: Int = requestNextFrame()

    private fun requestNextFrame(): Int = requestAnimationFrame { timestamp ->
        handle = requestNextFrame()

        callback(timestamp)
    }

    override fun cancel() {
        cancelAnimationFrame(handle)
    }
}

fun Window.setTimeouts(
    delay: Duration,
    callback: () -> Unit,
): Subscription = object : Subscription {
    var handle: Int = setNextTimeout()

    private fun setNextTimeout(): Int = setTimeout(
        handler = {
            handle = setNextTimeout()

            callback()
        },
        timeout = delay.inWholeMilliseconds.toInt(),
    )

    override fun cancel() {
        clearTimeout(handle)
    }
}

private class AnimationFrameEventStream() : DependentEventStream<Unit>() {
    override fun observe(): Subscription = window.requestAnimationFrames {
        notify(
            transaction = TODO(),
            event = Unit,
        )
    }
}

fun createAnimationFrameStream(): EventStream<Unit> = AnimationFrameEventStream()

fun createTimeoutStream(
    delay: Duration,
) = object : DependentEventStream<Unit>() {
    override fun observe(): Subscription = window.setTimeouts(
        delay = delay,
    ) {
        notify(
            transaction = TODO(),
            event = Unit
        )
    }
}
