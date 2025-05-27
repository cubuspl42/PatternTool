package diy.lingerie.reactive.vertices.event_stream

import diy.lingerie.reactive.vertices.Vertex

internal class MapEventStreamVertex<E, Er>(
    source: Vertex<E>,
    private val transform: (E) -> Er,
) : TransformingEventStreamVertex<E, Er>(
    source = source,
) {
    override val kind: String = "MapE"

    override fun handleSourceEvent(event: E) {
        notify(transform(event))
    }
}
