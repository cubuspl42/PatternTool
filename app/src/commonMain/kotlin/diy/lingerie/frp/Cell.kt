package diy.lingerie.frp

abstract class Cell<out V> : Notifier<Cell.Change<V>> {
    data class Change<out V>(
        val oldValue: V,
        val newValue: V,
    )

    companion object {
        fun <V> switch(
            nestedCell: Cell<Cell<V>>,
        ): Cell<V> = SwitchCell(
            nestedCell = nestedCell,
        )

        fun <V> of(value: V): Cell<V> = ConstCell(
            constValue = value,
        )
    }

    val newValues: EventStream<V>
        get() = changes.map { it.newValue }

    abstract val currentValue: V

    abstract val changes: EventStream<Change<V>>

    fun <Vr> map(
        transform: (V) -> Vr,
    ): Cell<Vr> = MapCell(
        source = this,
        transform = transform,
    )

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
//
//internal fun <V> Cell<V>.subscribeToNewValues(
//    handle: (V) -> Unit,
//): Subscription = this.newValues.subscribe(
//    listener = object : Listener<V> {
//        override fun handle(event: V) {
//            handle(event)
//        }
//    },
//)
//
//internal fun <V> Cell<V>.subscribeToNewValuesBound(
//    target: Any,
//    handle: (V) -> Unit,
//) {
//    this.subscribeToChangesBound(
//        target = target,
//    ) { change ->
//        handle(change.newValue)
//    }
//}
//
//internal fun <V> Cell<V>.subscribeToChanges(
//    handle: (Cell.Change<V>) -> Unit,
//): Subscription {
//    return changes.subscribe(
//        listener = object : Listener<Cell.Change<V>> {
//            override fun handle(event: Cell.Change<V>) {
//                handle(event)
//            }
//        },
//    )
//}
//
//internal fun <V> Cell<V>.subscribeToChangesBound(
//    target: Any,
//    handle: (Cell.Change<V>) -> Unit,
//): Subscription = changes.subscribeBound(
//    target = target,
//    listener = object : Listener<Cell.Change<V>> {
//        override fun handle(event: Cell.Change<V>) {
//            handle(event)
//        }
//    },
//)
