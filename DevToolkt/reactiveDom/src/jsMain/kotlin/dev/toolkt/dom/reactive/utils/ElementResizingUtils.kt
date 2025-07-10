package dev.toolkt.dom.reactive.utils

import dev.toolkt.reactive.Subscription
import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.event_stream.DependentEventStream
import dev.toolkt.reactive.event_stream.EventStream
import dev.toolkt.reactive.event_stream.newest
import dev.toolkt.reactive.future.Future
import org.w3c.dom.DOMRectReadOnly
import org.w3c.dom.Element
import org.w3c.dom.extra.ResizeObserver
import org.w3c.dom.extra.ResizeObserverEntry

fun Element.getResizeEventStream(): EventStream<ResizeObserverEntry> = ResizeObserverContentRectEventStream(
    element = this,
)

fun Element.getNewestContentRect(): Future<Cell<DOMRectReadOnly>> =
    getResizeEventStream().map { it.contentRect }.newest()

private class ResizeObserverContentRectEventStream(
    private val element: Element,
) : DependentEventStream<ResizeObserverEntry>() {
    override fun observe(): Subscription {
        // It's not clear if it's highly non-optimal to create a separate
        // resize observer for each stream (it would be good to verify it)
        val resizeObserver = ResizeObserver { entries, _ ->
            entries.forEach { entry ->
                notify(event = entry)
            }
        }.apply {
            observe(element)
        }

        return object : Subscription {
            override fun cancel() {
                resizeObserver.disconnect()
            }
        }
    }
}

