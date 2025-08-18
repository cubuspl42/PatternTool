package dev.toolkt.reactive.future

import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.cell.switch
import dev.toolkt.reactive.event_stream.EventStream
import dev.toolkt.reactive.event_stream.NeverEventStream
import dev.toolkt.reactive.event_stream.hold
import dev.toolkt.reactive.managed_io.Effect
import dev.toolkt.reactive.managed_io.MomentContext
import dev.toolkt.reactive.managed_io.Program
import dev.toolkt.reactive.managed_io.ActionContext
import dev.toolkt.reactive.managed_io.Schedule
import dev.toolkt.reactive.managed_io.executeCurrent
import dev.toolkt.reactive.managed_io.map

abstract class Future<out V> {
    sealed class State<out V>

    data object Pending : State<Nothing>()

    data class Fulfilled<out V>(
        val result: V,
    ) : State<V>()

    object Hang : Future<Nothing>() {
        override val state: Cell<State<Nothing>> = Cell.Companion.of(Pending)

        override val currentStateUnmanaged: State<Nothing> = Pending

        override val onFulfilled: EventStream<Fulfilled<Nothing>> = EventStream.Never

        override val onResult: EventStream<Nothing> = NeverEventStream

        override fun <Vr> map(transform: (Nothing) -> Vr): Future<Vr> = Hang
    }

    data class Prefilled<V>(
        val constResult: V,
    ) : Future<V>() {
        private val fulfilledState: Fulfilled<V>
            get() = Fulfilled(result = constResult)

        override val state: Cell<State<V>> = Cell.of(fulfilledState)

        override val currentStateUnmanaged: State<V> = fulfilledState

        override val onFulfilled: EventStream<Fulfilled<V>> = NeverEventStream

        override val onResult: EventStream<V> = NeverEventStream

        override fun <Vr> map(transform: (V) -> Vr): Future<Vr> = Prefilled(
            constResult = transform(constResult),
        )
    }

    companion object {
        fun <V> of(
            constResult: V,
        ): Future<V> = Prefilled(constResult)

        fun <V> join(
            future: Future<Future<V>>,
        ): Future<V> = JoinFuture(future)

        context(momentContext: MomentContext) fun <V, V1 : V, V2 : V> oscillate2(
            initialValue: V1,
            selectNextValueFuture1: context(MomentContext) (V1) -> Future<V2>,
            selectNextValueFuture2: context(MomentContext) (V2) -> Future<V1>,
        ): Cell<V> = object {
            context(momentContext: MomentContext) fun enterPhase1(
                value1: V1,
            ): Cell<V> = enterPhaseX(
                valueX = value1,
                selectNextValueFutureX = selectNextValueFuture1,
                enterPhaseY = { enterPhase2(it) },
            )

            context(momentContext: MomentContext) fun enterPhase2(
                value2: V2,
            ): Cell<V> = enterPhaseX(
                valueX = value2,
                selectNextValueFutureX = selectNextValueFuture2,
                enterPhaseY = { enterPhase1(it) },
            )

            private inline fun <V, Vx : V, Vy> enterPhaseX(
                valueX: Vx,
                selectNextValueFutureX: context(MomentContext) (Vx) -> Future<Vy>,
                crossinline enterPhaseY: context(MomentContext) (Vy) -> Cell<V>,
            ): Cell<V> = selectNextValueFutureX(valueX).mapAt { valueY ->
                enterPhaseY(valueY)
            }.placeholdStatic(
                placeholderValue = valueX,
            )
        }.enterPhase1(
            value1 = initialValue,
        )

        fun <V, V1 : V, V2 : V> oscillateUnmanaged2(
            initialValue: V1,
            switchPhase1: (V1) -> Future<V2>,
            switchPhase2: (V2) -> Future<V1>,
        ): Cell<V> = object {
            fun enterPhase1(
                value1: V1,
            ): Cell<V> = deflectJumpUnmanaged(
                initialValue = value1,
                jump = switchPhase1,
                recurse = ::enterPhase2,
            )

            fun enterPhase2(
                value2: V2,
            ): Cell<V> = deflectJumpUnmanaged(
                initialValue = value2,
                jump = switchPhase2,
                recurse = ::enterPhase1,
            )


            val result = enterPhase1(
                value1 = initialValue,
            )
        }.result

        context(momentContext: MomentContext) fun <V> deflect(
            initialValue: V,
            selectNextValueFuture: context(MomentContext) (value: V) -> Future<V>,
        ): Cell<V> {
            context(momentContext: MomentContext) fun recurse(
                firstValue: V,
            ): Cell<V> = selectNextValueFuture(firstValue).mapAt { nextValue ->
                recurse(nextValue)
            }.placeholdStatic(
                placeholderValue = initialValue,
            )

            return recurse(
                firstValue = initialValue,
            )
        }

        fun <V, V1 : V, V2> deflectJumpUnmanaged(
            initialValue: V1,
            jump: (V1) -> Future<V2>,
            recurse: (V2) -> Cell<V>,
        ): Cell<V> = jump(initialValue).map { value2 ->
            recurse(value2)
        }.switchHold(
            initialValue = initialValue,
        )
    }

    fun unit(): Future<Unit> = map { }

    @Suppress("FunctionName")
    fun null_(): Future<Nothing?> = map { null }

    fun thenExecute(
        action: (V) -> Schedule,
    ): Schedule = onResult.singleUnmanaged().map {
        action(it)
    }.hold(Program.Noop).executeCurrent()

    abstract val onResult: EventStream<V>

    abstract val state: Cell<State<V>>

    abstract val currentStateUnmanaged: State<V>

    abstract val onFulfilled: EventStream<Fulfilled<V>>

    abstract fun <Vr> map(
        transform: (V) -> Vr,
    ): Future<Vr>

    context(momentContext: MomentContext) fun <Vr> mapAt(
        transform: context(MomentContext) (V) -> Vr,
    ): Future<Vr> = PlainFuture(
        state = state.mapAt {
            when (it) {
                is Fulfilled<V> -> Fulfilled(
                    result = transform(it.result),
                )

                Pending -> Pending
            }
        },
    )

    fun <Vr> mapExecuting(
        transform: context(ActionContext) (V) -> Vr,
    ): Effect<Future<Vr>> = state.mapExecuting { stateNow ->

        when (stateNow) {
            is Fulfilled<V> -> Fulfilled(
                result = transform(stateNow.result),
            )

            Pending -> Pending
        }
    }.map { stateCell ->
        PlainFuture(state = stateCell)
    }
}

val <V> Future<V>.resultOrNull: Cell<V?>
    get() = state.map {
        when (it) {
            is Future.Fulfilled<V> -> it.result
            Future.Pending -> null
        }
    }

fun <V, R> Future<V>.joinOf(
    transform: (V) -> Future<R>,
): Future<R> = Future.join(map(transform))

fun <V> Future<V>.hold(
    initialValue: V,
): Cell<V> = when (val foundState = currentStateUnmanaged) {
    is Future.Fulfilled<V> -> Cell.of(foundState.result)

    Future.Pending -> onResult.hold(initialValue)
}

fun <V> Future<Cell<V>>.switchHold(
    initialCell: Cell<V>,
): Cell<V> = when (val state = currentStateUnmanaged) {
    is Future.Fulfilled<Cell<V>> -> state.result

    Future.Pending -> Cell.switch(
        onResult.hold(initialCell),
    )
}

fun <V> Future<V>.placehold(placeholderValue: V): Cell<V> = resultOrNull.map {
    it ?: placeholderValue
}

fun <V> Future<Cell<V>>.switchHold(
    initialValue: V,
): Cell<V> = switchHold(
    initialCell = Cell.of(initialValue),
)

fun <V> Future<Cell<V>>.placeholdStatic(
    placeholderValue: V,
): Cell<V> = placehold(
    Cell.of(placeholderValue),
).switch()

fun <E> Future<EventStream<E>>.divertHold(
    initialEventStream: EventStream<E>,
): EventStream<E> = when (val foundState = currentStateUnmanaged) {
    is Future.Fulfilled<EventStream<E>> -> foundState.result

    Future.Pending -> EventStream.divert(
        onResult.hold(initialEventStream),
    )
}
