package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.Listener
import dev.toolkt.reactive.Subscription
import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.cell.HoldCell
import dev.toolkt.reactive.future.Future
import dev.toolkt.reactive.effect.ActionContext
import dev.toolkt.reactive.effect.Effect
import dev.toolkt.reactive.effect.Effective
import dev.toolkt.reactive.effect.MomentContext
import dev.toolkt.reactive.effect.Transaction
import dev.toolkt.reactive.effect.Trigger
import dev.toolkt.reactive.effect.TriggerBase

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

abstract class EventStream<out E> {
    companion object {
        val Never: EventStream<Nothing> = NeverEventStream

        context(momentContext: MomentContext) fun <EventT> spark(
            event: EventT,
        ): EventStream<EventT> = SparkEventStream.construct(
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

        /**
         * Creates an [EventStream] that is driven by an external source, assuming
         * that the external events happen independently (the fact that we subscribe
         * to the external source does not affect the events that it emits).
         *
         * @return An [EventStream] that emits events from the external source.
         */
        // TODO: Add tests
        fun <EventT> subscribeExternal(
            subscribe: (Controller<EventT>) -> Subscription,
        ): EventStream<EventT> = PassiveExternalEventStream.construct(
            subscribe = subscribe,
        )

        /**
         * Creates an [EventStream] that is driven by an external source activated
         * by this call.
         *
         * @return An [Effect] that, once started, gives an [EventStream] driven
         * by an external source. Once the effect is cancelled, the external
         * source is deactivated.
         */
        // TODO: Add tests
        context(actionContext: ActionContext) fun <EventT> activateExternal(
            activate: (Controller<EventT>) -> Subscription,
        ): Effect<EventStream<EventT>> = object : Effect<EventStream<EventT>> {
            context(actionContext: ActionContext) override fun start(): Effective<EventStream<EventT>> =
                ActiveExternalEventStream.construct(
                    activate = activate,
                )
        }
    }

    interface Controller<EventT> {
        fun accept(
            event: EventT,
        )
    }

    abstract fun listen(
        listener: Listener<E>,
    ): Subscription

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

    context(momentContext: MomentContext) abstract fun take(
        count: Int,
    ): EventStream<E>

    context(momentContext: MomentContext) fun single(): EventStream<E> = SingleEventStream.construct(
        source = this,
    )

    context(momentContext: MomentContext) abstract fun next(): Future<E>

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
