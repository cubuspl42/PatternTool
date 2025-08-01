package dev.toolkt.reactive.cell

import dev.toolkt.reactive.Subscription
import dev.toolkt.reactive.event_stream.EventStream
import dev.toolkt.reactive.event_stream.hold
import dev.toolkt.reactive.event_stream.takeUntilNull
import dev.toolkt.reactive.reactive_list.LoopedCell

sealed class Cell<out V> {
    data class Change<out V>(
        val oldValue: V,
        val newValue: V,
    )

    companion object {
        fun <V, R> looped(
            placeholderValue: V,
            block: (Cell<V>) -> Pair<R, Cell<V>>,
        ): R {
            val (result, _) = loopedPair(
                placeholderValue = placeholderValue,
                block = block,
            )

            return result
        }

        fun <V, R> loopedPair(
            placeholderValue: V,
            block: (Cell<V>) -> Pair<R, Cell<V>>,
        ): Pair<R, Cell<V>> {
            val loopedCell = LoopedCell(placeholderValue = placeholderValue)

            val (result, cell) = block(loopedCell)

            loopedCell.loop(cell)

            return Pair(result, cell)
        }

        fun <V1, V2, Vr> map2(
            cell1: Cell<V1>,
            cell2: Cell<V2>,
            transform: (V1, V2) -> Vr,
        ): Cell<Vr> = Map2Cell(
            source1 = cell1,
            source2 = cell2,
            transform = transform,
        )

        fun <V1, V2, V3, Vr> map3(
            cell1: Cell<V1>,
            cell2: Cell<V2>,
            cell3: Cell<V3>,
            transform: (V1, V2, V3) -> Vr,
        ): Cell<Vr> = Map3Cell(
            source1 = cell1,
            source2 = cell2,
            source3 = cell3,
            transform = transform,
        )

        fun <V1, V2, V3, V4, Vr> map4(
            cell1: Cell<V1>,
            cell2: Cell<V2>,
            cell3: Cell<V3>,
            cell4: Cell<V4>,
            transform: (V1, V2, V3, V4) -> Vr,
        ): Cell<Vr> = Map4Cell(
            source1 = cell1,
            source2 = cell2,
            source3 = cell3,
            source4 = cell4,
            transform = transform,
        )

        fun <V> switch(
            nestedCell: Cell<Cell<V>>,
        ): Cell<V> = SwitchCell(
            nestedCell = nestedCell,
        )

        fun <Vr1, Vr2> zip2(
            cell1: Cell<Vr1>,
            cell2: Cell<Vr2>,
        ): Cell<Pair<Vr1, Vr2>> = cell1.switchOf { value1 ->
            cell2.map { value2 ->
                Pair(value1, value2)
            }
        }

        fun <V> of(
            value: V,
        ): Cell<V> = ConstCell(constValue = value)
    }

    abstract val newValues: EventStream<V>

    abstract val currentValue: V

    abstract val changes: EventStream<Change<V>>

    abstract fun <Vr> map(
        transform: (V) -> Vr,
    ): Cell<Vr>

    abstract fun calm(): Cell<V>

    abstract fun <T : Any> form(
        create: (V) -> T,
        update: (T, V) -> Unit,
    ): Pair<T, Subscription>

    fun <T : Any> formAndForget(
        create: (V) -> T,
        update: (T, V) -> Unit,
    ): T {
        val (target, _) = form(create, update)

        // Forget the subscription, relying purely on garbage collection
        return target
    }

    abstract fun <T : Any> bind(
        target: T,
        update: (T, V) -> Unit,
    ): Subscription

    fun <T : Any> bindAndForget(
        target: T,
        update: (T, V) -> Unit,
    ) {
        bind(
            target = target,
            update = update,
        )
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

fun <V : Any, Vr : Any> Cell<V?>.mapNotNull(
    transform: (V) -> Vr,
): Cell<Vr?> = map {
    when (it) {
        null -> null
        else -> transform(it)
    }
}

fun <V : Any> Cell<V?>.separateNonNull(): Cell<Cell<V>?> = this.map { value ->
    when (value) {
        null -> null
        else -> newValues.takeUntilNull().hold(value)
    }
}

fun <V, T : Any> Cell<V>.bindNested(
    target: T,
    bindInner: (T, V) -> Subscription,
): Subscription = object : Subscription {
    private var innerSubscription = bindInner(
        target,
        currentValue,
    )

    private val outerSubscription = bind(
        target = target,
        update = { it, newValue ->
            innerSubscription.cancel()
            innerSubscription = bindInner(it, newValue)
        },
    )

    override fun cancel() {
        outerSubscription.cancel()
        innerSubscription.cancel()
    }
}
