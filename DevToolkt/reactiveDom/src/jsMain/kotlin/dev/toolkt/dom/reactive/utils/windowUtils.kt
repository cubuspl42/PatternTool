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
    var handle = requestSingleFrame()

    private fun requestSingleFrame(): Int = requestAnimationFrame { timestamp ->
        requestAnimationFrame {
            handle = requestSingleFrame()
        }

        callback(timestamp)
    }

    override fun cancel() {
        cancelAnimationFrame(handle)
    }
}

private class AnimationFrameEventStream() : DependentEventStream<Unit>() {
    override fun observe(): Subscription = window.requestAnimationFrames { notify(Unit) }
}

fun createAnimationFrameStream(): EventStream<Unit> = AnimationFrameEventStream()
