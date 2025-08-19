package dev.toolkt.dom.reactive.utils

import dev.toolkt.dom.pure.PureSize
import dev.toolkt.reactive.Subscription
import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.event_stream.EventStream
import dev.toolkt.reactive.event_stream.fetch
import dev.toolkt.reactive.event_stream.getEventStream
import dev.toolkt.reactive.managed_io.ActionContext
import dev.toolkt.reactive.managed_io.Effect
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

context(actionContext: ActionContext) fun Window.createIntervalAnimationFrameStream(): Effect<EventStream<Double>> =
    EventStream.activateExternal { controller ->
        object : Subscription {
            var requestId: Int = requestNextFrame()

            private fun requestNextFrame(): Int = requestAnimationFrame { timestamp ->
                requestId = requestNextFrame()

                controller.accept(timestamp)
            }

            override fun cancel() {
                cancelAnimationFrame(requestId)
            }
        }
    }

context(actionContext: ActionContext) fun Window.createIntervalTimeoutStream(
    delay: Duration,
): Effect<EventStream<Unit>> = EventStream.activateExternal { controller ->
    object : Subscription {
        var handle: Int = setNextTimeout()

        private fun setNextTimeout(): Int = setTimeout(
            handler = {
                handle = setNextTimeout()

                controller.accept(Unit)
            },
            timeout = delay.inWholeMilliseconds.toInt(),
        )

        override fun cancel() {
            clearTimeout(handle)
        }
    }
}
