package diy.lingerie.frp

abstract class EventStream<out E> : Notifier<E> {
    companion object {

        val Never: EventStream<Nothing> = NeverEventStream

        fun <V> divert(
            nestedEventStream: Cell<EventStream<V>>,
        ): EventStream<V> = DivertEventStream(
            nestedEventStream = nestedEventStream,
        )
    }

    fun <Er> map(
        transform: (E) -> Er,
    ): EventStream<Er> = MapEventStream(
        source = this,
        transform = transform,
    )

    fun filter(
        predicate: (E) -> Boolean,
    ): EventStream<E> = FilterEventStream(
        source = this,
        predicate = predicate,
    )
}


fun <E> EventStream<E>.hold(
    initialValue: E,
): Cell<E> = HoldCell(
    values = this,
    initialValue = initialValue,
)
