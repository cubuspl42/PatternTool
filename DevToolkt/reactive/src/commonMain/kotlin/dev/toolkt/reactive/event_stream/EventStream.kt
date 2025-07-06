package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.Subscription
import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.cell.HoldCell
import dev.toolkt.reactive.future.Future

typealias TargetingListenerFn<TargetT, EventT> = (TargetT, EventT) -> Unit

interface TargetingListener<in TargetT : Any, in EventT> {
    object Noop : TargetingListener<Any, Any?> {
        override fun handle(
            target: Any,
            event: Any?,
        ) {
        }
    }

    companion object {
        fun <TargetT : Any, EventT> wrap(
            fn: TargetingListenerFn<TargetT, EventT>,
        ): TargetingListener<TargetT, EventT> = object : TargetingListener<TargetT, EventT> {
            override fun handle(
                target: TargetT,
                event: EventT,
            ) {
                fn(target, event)
            }
        }
    }

    /**
     * A function that accepts a target and an event.
     */
    fun handle(
        target: TargetT,
        event: EventT,
    )
}

fun <TargetT : Any, EventT> EventSource<EventT>.bind(
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

fun <TargetT : Any, EventT> EventSource<EventT>.bind(
    listener: TargetingListener<TargetT, EventT>,
): SourcedListener<TargetT, EventT> = SourcedListener(
    source = this,
    listener = listener,
)

abstract class EventStream<out E> : EventSource<E> {
    companion object {
        val Never: EventStream<Nothing> = NeverEventStream

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
    }

    abstract fun <Er> map(
        transform: (E) -> Er,
    ): EventStream<Er>

    abstract fun <Er : Any> mapNotNull(
        transform: (E) -> Er?,
    ): EventStream<Er>

    abstract fun filter(
        predicate: (E) -> Boolean,
    ): EventStream<E>

    abstract fun take(
        count: Int,
    ): EventStream<E>

    abstract fun single(): EventStream<E>

    abstract fun next(): Future<E>

    abstract fun forEach(
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
