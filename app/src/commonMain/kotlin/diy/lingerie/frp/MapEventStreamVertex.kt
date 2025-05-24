package diy.lingerie.frp

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
