package diy.lingerie.frp.vertices.event_stream

import diy.lingerie.frp.vertices.Vertex

internal class MapEventStreamVertex<E, Er>(
    source: Vertex<E>,
    private val transform: (E) -> Er,
) : TransformingEventStreamVertex<E, Er>(
    source = source,
) {
    override fun handleSourceEvent(event: E) {
        notify(transform(event))
    }
}
