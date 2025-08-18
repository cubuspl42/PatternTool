package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.Listener
import dev.toolkt.reactive.Subscription
import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.cell.HoldCell
import dev.toolkt.reactive.future.Future
import dev.toolkt.reactive.managed_io.ActionContext
import dev.toolkt.reactive.managed_io.Effect
import dev.toolkt.reactive.managed_io.Effective
import dev.toolkt.reactive.managed_io.Trigger
import dev.toolkt.reactive.managed_io.MomentContext
import dev.toolkt.reactive.managed_io.Transaction
import dev.toolkt.reactive.managed_io.TriggerBase

typealias TargetingListenerFn<TargetT, EventT> = (TargetT, EventT) -> Unit

interface TargetingListener<in TargetT : Any, in EventT> {
    object Noop : TargetingListener<Any, Any?> {
        override fun handle(
            transaction: Transaction,
            target: Any,
            event: Any?,
        ): Listener.Conclusion = Listener.Conclusion.KeepListening
    }

    companion object {
        fun <TargetT : Any, EventT> wrap(
            fn: TargetingListenerFn<TargetT, EventT>,
        ): TargetingListener<TargetT, EventT> = object : TargetingListener<TargetT, EventT> {
            override fun handle(
                transaction: Transaction,
                target: TargetT,
                event: EventT,
            ): Listener.Conclusion {
                fn(target, event)

                return Listener.Conclusion.KeepListening
            }
        }
    }

    /**
     * A function that accepts a target and an event.
     */
    fun handle(
        transaction: Transaction,
        target: TargetT,
        event: EventT,
    ): Listener.Conclusion
}

internal fun <TargetT : Any, EventT> EventSource<EventT>.bind(
    target: TargetT,
    listener: TargetingListener<TargetT, EventT>,
): BoundTargetedListener<TargetT, EventT> = listener.bindTarget(
    target = target,
).bindSource(
    source = this,
)

fun <TargetT : Any, EventT> TargetingListener<TargetT, EventT>.bindTarget(
    target: TargetT,
): TargetedListener<TargetT, EventT> = TargetedListener(
    target = target,
    listener = this,
)

internal fun <TargetT : Any, EventT> EventSource<EventT>.bind(
    listener: TargetingListener<TargetT, EventT>,
): SourcedListener<TargetT, EventT> = SourcedListener(
    source = this,
    listener = listener,
)

abstract class EventStream<out E> : EventSource<E> {
    companion object {
        val Never: EventStream<Nothing> = NeverEventStream

        context(momentContext: MomentContext) fun <EventT> spark(
            event: EventT,
        ): EventStream<EventT> {
            TODO()
        }

        fun <E, R> looped(
            block: (EventStream<E>) -> Pair<R, EventStream<E>>,
        ): R {
            val loopedEventStream = LoopedEventStream<E>()

            val (result, eventStream) = block(loopedEventStream)

            loopedEventStream.loop(eventStream)

            return result
        }

        fun <V> divert(
            nestedEventStream: Cell<EventStream<V>>,
        ): EventStream<V> = DivertEventStream(
            nestedEventStream = nestedEventStream,
        )

        fun <E> merge(
            source1: EventStream<E>,
            source2: EventStream<E>,
        ): EventStream<E> = when {
            source1 == NeverEventStream -> source2
            source2 == NeverEventStream -> source1
            else -> MergeEventStream(
                source1 = source1,
                source2 = source2,
            )
        }

        fun <E> mergeAll(
            vararg sources: EventStream<E>,
        ): EventStream<E> = mergeAll(
            sources = sources.toList(),
        )

        fun <E> mergeAll(
            sources: List<EventStream<E>>,
        ): EventStream<E> = StaticMergeAllEventStream(
            sources = sources,
        )
    }

    abstract fun <Er> map(
        transform: (E) -> Er,
    ): EventStream<Er>

    fun <Er> mapAt(
        transform: context(MomentContext) (E) -> Er,
    ): EventStream<Er> = MapAtEventStream.construct(
        source = this,
        transform = transform,
    )

    fun <Er> mapExecuting(
        transform: context(ActionContext) (E) -> Er,
    ): Effect<EventStream<Er>> = object : Effect<EventStream<Er>> {
        context(actionContext: ActionContext) override fun start(): Effective<EventStream<Er>> =
            MapExecutingEventStream.construct(
                source = this@EventStream,
                transform = transform,
            )
    }

    abstract fun <Er : Any> mapNotNull(
        transform: (E) -> Er?,
    ): EventStream<Er>

    abstract fun filter(
        predicate: (E) -> Boolean,
    ): EventStream<E>

    fun filterAt(
        predicate: context(MomentContext) (E) -> Boolean,
    ): EventStream<E> = FilterAtEventStream.construct(
        source = this,
        predicate = predicate,
    )

    abstract fun take(
        count: Int,
    ): EventStream<E>

    abstract fun singleUnmanaged(): EventStream<E>

    context(momentContext: MomentContext) fun single(): EventStream<E> = SingleEventStreamNg.construct(
        source = this,
    )

    abstract fun next(): Future<E>

    fun onNext(): Future<Unit> = next().unit()

    // This stinks
    abstract fun forEachUnmanaged(
        effect: (E) -> Unit,
    )

    abstract fun <T : Any> pipe(
        target: T,
        forward: (T, E) -> Unit,
    ): Subscription

    fun <T : Any> pipeAndForget(
        target: T,
        forward: (T, E) -> Unit,
    ) {
        // Forget the subscription, relying purely on garbage collection
        pipe(
            target = target,
            forward = forward,
        )
    }

    fun units(): EventStream<Unit> = map { }
}

fun <E> EventStream<E>.forEach(
    action: context(ActionContext) (E) -> Unit,
): Trigger = object : TriggerBase() {
    context(actionContext: ActionContext) override fun jumpStart(): Effect.Handle = ForEachEffectHandle.construct(
        source = this@forEach,
        action = action,
    )
}


context(actionContext: ActionContext) fun <E> EventStream<E>.forward(
    update: (E) -> Unit,
): Effect.Handle {
    TODO() // The other most low-level ReactionContext operation?
}

context(actionContext: ActionContext)


fun <E : Any> EventStream<E?>.filterNotNull(): EventStream<E> = mapNotNull { it }

fun <E : Any> EventStream<E?>.takeUntilNull(): EventStream<E> = TakeUntilNullStream(
    source = this,
)

fun <E> EventStream<E>.mergeWith(
    other: EventStream<E>,
): EventStream<E> = EventStream.merge(
    source1 = this,
    source2 = other,
)

fun <E> EventStream<*>.cast(): EventStream<E> {
    @Suppress("UNCHECKED_CAST") return this as EventStream<E>
}

fun <E> EventStream<E>.hold(
    initialValue: E,
): Cell<E> = HoldCell(
    initialValue = initialValue,
    newValues = this,
)

fun <V, R> EventStream<V>.accum(
    initialValue: R,
    transform: (previousValue: R, newEvent: V) -> R,
): Cell<R> = Cell.selfLooped(
    placeholderValue = initialValue,
) { loopedCell ->
    map { newEvent ->
        transform(
            loopedCell.currentValueUnmanaged,
            newEvent,
        )
    }.hold(initialValue = initialValue)
}

fun <E> EventStream<Any?>.fetch(
    getValue: () -> E,
): Cell<E> = HoldCell(
    initialValue = getValue(),
    newValues = map { getValue() },
)

fun <ValueT> EventStream<ValueT>.newest(): Future<Cell<ValueT>> = next().map { firstValue ->
    hold(initialValue = firstValue)
}
