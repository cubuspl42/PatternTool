package diy.lingerie.frp

internal class FilterEventStream<E>(
    source: EventStream<E>,
    private val predicate: (E) -> Boolean,
) : TransformingEventStream<E, E>(
    source = source,
) {
    override fun handleSourceEvent(event: E) {
        if (predicate(event)) {
            notify(event)
        }
    }
}
