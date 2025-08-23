package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.Listener
import dev.toolkt.reactive.Subscription
import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.cell.HoldCell
import dev.toolkt.reactive.effect.ActionContext
import dev.toolkt.reactive.effect.Effect
import dev.toolkt.reactive.effect.Effective
import dev.toolkt.reactive.effect.MomentContext
import dev.toolkt.reactive.effect.Transaction
import dev.toolkt.reactive.effect.Trigger
import dev.toolkt.reactive.effect.TriggerBase
import dev.toolkt.reactive.future.Future

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

interface EventStream<out EventT> {
    companion object {
        val Never: EventStream<Nothing> = NeverEventStream

        context(momentContext: MomentContext) fun <EventT> spark(
            event: EventT,
        ): EventStream<EventT> = SparkEventStream.cause(
            event = event,
        )

        fun <E, R> looped(
            block: (EventStream<E>) -> Pair<R, EventStream<E>>,
        ): R {
            val loopedEventStream = LoopedEventStream<E>()

            val (result, eventStream) = block(loopedEventStream)

            loopedEventStream.loop(eventStream)

            return result
        }

        fun <EventT> lazy(
            lazyEventStream: Lazy<EventStream<EventT>>,
        ): EventStream<EventT> {
            TODO()
        }

        fun <EventT, ResultT> loopedInEffect(
            block: (EventStream<EventT>) -> Effect<Pair<ResultT, EventStream<EventT>>>,
        ): Effect<ResultT> = Effect.looped<ResultT, EventStream<EventT>> { lazyEventStream: Lazy<EventStream<EventT>> ->
            block(
                lazy(lazyEventStream = lazyEventStream),
            )
        }

        fun <V> divert(
            nestedEventStream: Cell<EventStream<V>>,
        ): EventStream<V> = DivertEventStream(
            source = nestedEventStream,
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

        /**
         * Creates an [EventStream] that is driven by an external source, assuming
         * that the external events happen independently (the fact that we subscribe
         * to the external source does not affect the events that it emits).
         *
         * @return An [EventStream] that emits events from the external source.
         */
        fun <EventT> subscribeExternal(
            subscribe: (Controller<EventT>) -> Subscription,
        ): EventStream<EventT> = PassiveExternalEventStream(
            subscribe = subscribe,
        )

        /**
         * Creates an [EventStream] that is driven by an external source activated
         * by starting the returned effect.
         *
         * @return An [Effect] that, once started, activates the external source
         * and gives an [EventStream] driven by an external source. Once the effect
         * is cancelled, the external source is deactivated.
         */
        context(actionContext: ActionContext) fun <EventT> activateExternal(
            start: (Controller<EventT>) -> Subscription,
        ): Effect<EventStream<EventT>> = object : Effect<EventStream<EventT>> {
            context(actionContext: ActionContext) override fun start(): Effective<EventStream<EventT>> =
                ActiveExternalEventStream.start(
                    start = start,
                )
        }
    }

    interface Controller<EventT> {
        fun accept(
            event: EventT,
        )
    }

    fun listen(
        listener: Listener<EventT>,
    ): Subscription

    fun <Er> map(
        transform: (EventT) -> Er,
    ): EventStream<Er>

    fun <TransformedEventT : Any> mapNotNull(
        transform: (EventT) -> TransformedEventT?,
    ): EventStream<TransformedEventT>

    fun filter(
        predicate: (EventT) -> Boolean,
    ): EventStream<EventT>

    context(momentContext: MomentContext) fun take(
        count: Int,
    ): EventStream<EventT>

    context(momentContext: MomentContext) fun next(): Future<EventT>

    // This stinks
    fun forEachUnmanaged(
        effect: (EventT) -> Unit,
    )

    fun <T : Any> pipe(
        target: T,
        forward: (T, EventT) -> Unit,
    ): Subscription
}

fun <E> EventStream<E>.filterAt(
    predicate: context(MomentContext) (E) -> Boolean,
): EventStream<E> = FilterEventStream(
    source = this,
    predicate = predicate,
)

context(momentContext: MomentContext) fun <E> EventStream<E>.single(): EventStream<E> = SingleEventStream.construct(
    source = this,
)

fun <E> EventStream<E>.units(): EventStream<Unit> = map { }

fun <E, Er> EventStream<E>.mapExecuting(
    transform: context(ActionContext) (E) -> Er,
): Effect<EventStream<Er>> = object : Effect<EventStream<Er>> {
    context(actionContext: ActionContext) override fun start(): Effective<EventStream<Er>> =
        MapExecutingEventStream.start(
            source = this@mapExecuting,
            transform = transform,
        )
}

fun <EventT, TransformedEventT> EventStream<EventT>.mapAt(
    transform: context(MomentContext) (EventT) -> TransformedEventT,
): EventStream<TransformedEventT> = MapEventStream(
    source = this,
    transform = transform,
)

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

fun <E : Any> EventStream<E?>.filterNotNull(): EventStream<E> = mapNotNull { it }

context(momentContext: MomentContext) fun <E : Any> EventStream<E?>.takeUntilNull(): EventStream<E> =
    TakeUntilNullStream.construct(
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

fun <E> EventStream<E>.holdUnmanaged(
    initialValue: E,
): Cell<E> = HoldCell(
    initialValue = initialValue,
    newValues = this,
)

context(momentContext: MomentContext) fun <E> EventStream<E>.hold(
    initialValue: E,
): Cell<E> = HoldCell(
    initialValue = initialValue,
    newValues = this,
)

context(momentContext: MomentContext) fun <V, R> EventStream<V>.accum(
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

fun <V, R> EventStream<V>.accumUnmanaged(
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
    }.holdUnmanaged(initialValue = initialValue)
}

fun <E> EventStream<Any?>.fetch(
    getValue: () -> E,
): Cell<E> = HoldCell(
    initialValue = getValue(),
    newValues = map { getValue() },
)

context(momentContext: MomentContext) fun <ValueT> EventStream<ValueT>.newest(): Future<Cell<ValueT>> =
    next().mapAt { firstValue ->
        hold(initialValue = firstValue)
    }
