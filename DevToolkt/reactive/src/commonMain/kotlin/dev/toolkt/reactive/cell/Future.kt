package dev.toolkt.reactive.cell

import dev.toolkt.reactive.event_stream.EventStream
import dev.toolkt.reactive.event_stream.hold

sealed class Future<out V> {
    sealed class State<out V>

    data object Pending : State<Nothing>()

    data class Fulfilled<V>(
        val value: V,
    ) : State<V>()

    object Hang : Future<Nothing>() {
        override val state: Cell<State<Nothing>> = Cell.of(Pending)

        override val currentState: State<Nothing> = Pending

        override val onFulfilled: EventStream<Nothing> = EventStream.Never

        override fun <Vr> map(transform: (Nothing) -> Vr): Future<Vr> = Hang
    }

    companion object {
        fun <V, V1 : V, V2 : V> oscillate(
            initialValue: V1,
            switchPhase1: (V1) -> Future<V2>,
            switchPhase2: (V2) -> Future<V1>,
        ): Cell<V> = object {
            fun enterPhase1(
                value1: V1,
            ): Cell<V> = Future.deflect(
                initialValue = value1,
                jump = switchPhase1,
                recurse = ::enterPhase2,
            )

            fun enterPhase2(
                value2: V2,
            ): Cell<V> = Future.deflect(
                initialValue = value2,
                jump = switchPhase2,
                recurse = ::enterPhase1,
            )

            val result = enterPhase1(
                value1 = initialValue,
            )
        }.result

        fun <V, V1 : V, V2> deflect(
            initialValue: V1,
            jump: (V1) -> Future<V2>,
            recurse: (V2) -> Cell<V>,
        ): Cell<V> = jump(initialValue).map { value2 ->
            recurse(value2)
        }.switchHold(
            initialValue = initialValue,
        )
    }

    fun unit(): Future<Unit> {
        TODO()
    }

    abstract val state: Cell<State<V>>

    abstract val currentState: State<V>

    abstract val onFulfilled: EventStream<V>

    abstract fun <Vr> map(
        transform: (V) -> Vr,
    ): Future<Vr>
}

fun <V> Future<V>.hold(
    initialValue: V,
): Cell<V> = when (val state = currentState) {
    is Future.Fulfilled<V> -> Cell.of(state.value)

    Future.Pending -> onFulfilled.hold(initialValue)
}

fun <V> Future<Cell<V>>.switchHold(
    initialCell: Cell<V>,
): Cell<V> = when (val state = currentState) {
    is Future.Fulfilled<Cell<V>> -> state.value

    Future.Pending -> Cell.switch(
        onFulfilled.hold(initialCell),
    )
}

fun <V> Future<Cell<V>>.switchHold(
    initialValue: V,
): Cell<V> = switchHold(
    initialCell = Cell.of(initialValue),
)

fun <V> Future<EventStream<V>>.divertHold(
    initialEventStream: EventStream<V>,
): EventStream<V> = when (val state = currentState) {
    is Future.Fulfilled<EventStream<V>> -> state.value

    Future.Pending -> EventStream.divert(
        onFulfilled.hold(initialEventStream),
    )
}

