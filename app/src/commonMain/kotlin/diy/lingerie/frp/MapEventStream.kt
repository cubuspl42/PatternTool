package diy.lingerie.frp

internal class MapEventStream<E, Er>(
    source: EventStream<E>,
    private val transform: (E) -> Er,
) : TransformingEventStream<E, Er>(
    source = source,
) {
    override fun transformEvent(event: E) {
        notify(transform(event))
    }
}
