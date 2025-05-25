package diy.lingerie.frp.vertices.event_stream

import diy.lingerie.frp.vertices.Vertex

internal class FilterEventStreamVertex<E>(
    source: Vertex<E>,
    private val predicate: (E) -> Boolean,
) : TransformingEventStreamVertex<E, E>(
    source = source,
) {
    override fun handleSourceEvent(event: E) {
        if (predicate(event)) {
            notify(event)
        }
    }
}
