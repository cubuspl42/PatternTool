package diy.lingerie.frp


interface Subscription {
    fun cancel()
}

interface Listener<in E> {
    fun handle(event: E)
}

abstract class Cell<out V> {
    companion object {
        fun <V> switch(
            nestedCell: Cell<Cell<V>>,
        ): Cell<V> {
            TODO()
        }

        fun <V> of(value: V): Cell<V> {
            TODO()
        }
    }

    abstract val currentValue: V

    fun <Vr> map(
        transform: (V) -> Vr,
    ): Cell<Vr> {
        TODO()
    }

    fun <Vr> switchOf(
        transform: (V) -> Cell<Vr>,
    ): Cell<Vr> = switch(
        nestedCell = map(transform),
    )

    fun <Er> divertOf(
        transform: (V) -> EventStream<Er>,
    ): EventStream<Er> = EventStream.divert(
        nestedEventStream = map(transform),
    )
}
