package diy.lingerie.frp

internal class FilterEventStream<E>(
    private val source: EventStream<E>,
    private val predicate: (E) -> Boolean,
) : TransformingEventStream<E, E>(
    source = source,
) {
    override fun transformEvent(event: E) {
        if (predicate(event)) {
            notify(event)
        }
    }
}
