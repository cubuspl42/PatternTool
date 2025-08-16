package dev.toolkt.reactive.managed_io

import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.event_stream.EventStream
import dev.toolkt.reactive.event_stream.subscribe
import dev.toolkt.reactive.future.Future
import dev.toolkt.reactive.future.resultOrNull
import dev.toolkt.reactive.future.withPlaceholderSwitched

interface Effect<out ResultT> {
    companion object {
        val Null: Effect<Nothing?> = pure(null)

        fun <ResultT> pure(
            effective: Effective<ResultT>,
        ): Effect<ResultT> = object : Effect<ResultT> {
            context(reactionContext: ReactionContext) override fun start(): Effective<ResultT> = effective
        }

        fun <ResultT> pure(
            result: ResultT,
        ): Effect<ResultT> = pure(
            effective = Effective.pure(result),
        )

        fun <ResultT> pureTriggering(
            result: ResultT,
            trigger: Trigger,
        ): Effect<ResultT> = object : Effect<ResultT> {
            context(reactionContext: ReactionContext) override fun start(): Effective<ResultT> = Effective(
                result = result,
                handle = trigger.startExternally(),
            )
        }

        fun <ResultT> pureTriggering(
            result: ResultT,
            triggers: Iterable<Trigger>,
        ): Effect<ResultT> = object : Effect<ResultT> {
            context(reactionContext: ReactionContext) override fun start(): Effective<ResultT> = Effective(
                result = result,
                handle = Triggers.startAll(triggers = triggers),
            )
        }

        fun <ResultT> prepared(
            prepare: context(ReactionContext) () -> Effect<ResultT>,
        ): Effect<ResultT> = object : Effect<ResultT> {
            context(reactionContext: ReactionContext) override fun start(): Effective<ResultT> {
                val effect = prepare()
                return effect.start()
            }
        }

        fun <ResultT> preparedPure(
            prepare: context(ReactionContext) () -> ResultT,
        ): Effect<ResultT> = prepared {
            pure(prepare())
        }

        fun <ResultT> prefaced(
            preface: context(ReactionContext) () -> Unit,
            effect: Effect<ResultT>,
        ): Effect<ResultT> = object : Effect<ResultT> {
            context(reactionContext: ReactionContext) override fun start(): Effective<ResultT> {
                preface()
                return effect.start()
            }
        }

        fun <ResultT> initialized(
            init: context(ProactionContext) () -> Unit,
            effect: Effect<ResultT>,
        ): Effect<ResultT> = prefaced(
            preface = {
                Reactions.defer(init)
            },
            effect = effect,
        )

        fun <V> deflectEffectively(
            initialValueEffect: Effect<V>,
            selectNextValueEffectFuture: context(MomentContext) (value: V) -> Future<Effect<V>>,
        ): Effect<Cell<V>> {
            fun recurse(
                firstValueEffect: Effect<V>,
            ): Effect<Cell<V>> = firstValueEffect.joinOf { firstValue: V ->
                selectNextValueEffectFuture(firstValue).mapAt { nextValueEffect: Effect<V> ->
                    recurse(firstValueEffect = nextValueEffect)
                }.waitEffectively().map { successorCellFuture: Future<Cell<V>> ->
                    successorCellFuture.withPlaceholderSwitched(placeholderValue = firstValue)
                }
            }

            return recurse(
                firstValueEffect = initialValueEffect,
            )
        }
    }

    interface Handle {
        data object Noop : Handle {
            context(reactionContext: ReactionContext) override fun end() {
            }
        }

        companion object {
            fun combine(
                handles: Iterable<Handle>,
            ): Handle = object : Handle {
                context(reactionContext: ReactionContext) override fun end() {
                    handles.forEach { it.end() }
                }
            }

            fun combine(
                vararg handles: Handle,
            ): Handle = combine(handles = handles.asIterable())

            fun current(
                handle: Cell<Handle>,
            ): Handle = object : Handle {
                context(reactionContext: ReactionContext) override fun end() {
                    handle.sample().end()
                }
            }
        }

        context(reactionContext: ReactionContext) fun end()
    }

    context(reactionContext: ReactionContext) fun start(): Effective<ResultT>
}

fun <ResultT, TransformedResultT> Effect<ResultT>.map(
    transform: context(ReactionContext) (ResultT) -> TransformedResultT,
): Effect<TransformedResultT> = object : Effect<TransformedResultT> {
    context(reactionContext: ReactionContext) override fun start(): Effective<TransformedResultT> {
        val (outerValue, outerHandle) = this@map.start()

        val transformedValue = transform(outerValue)

        return Effective(
            result = transformedValue,
            handle = outerHandle,
        )
    }
}

fun <ResultT, TransformedResultT> Effect<ResultT>.joinOf(
    transform: context(ReactionContext) (ResultT) -> Effect<TransformedResultT>,
): Effect<TransformedResultT> = object : Effect<TransformedResultT> {
    context(reactionContext: ReactionContext) override fun start(): Effective<TransformedResultT> {
        val (outerValue, outerHandle) = this@joinOf.start()

        val innerEffect = transform(outerValue)

        val (innerValue, innerHandle) = innerEffect.start()

        return Effective(
            result = innerValue,
            handle = Effect.Handle.combine(
                outerHandle,
                innerHandle,
            ),
        )
    }
}

fun <ResultT> Effect<ResultT>.interrupted(
    doInterrupt: EventStream<Unit>,
): Effect<ResultT> = object : Effect<ResultT> {
    context(reactionContext: ReactionContext) override fun start(): Effective<ResultT> {
        val (outerValue, outerHandle) = this@interrupted.start()

        val interruptHandle = doInterrupt.subscribe {
            outerHandle.end()
        }

        return Effective(
            result = outerValue,
            handle = Effect.Handle.combine(
                outerHandle,
                interruptHandle,
            ),
        )
    }
}

// TODO: Add tests
fun <V> Cell<Effect<V>>.actuate(): Effect<Cell<V>> {
    TODO()
}

// TODO: Add tests
fun Cell<Trigger>.activate(): Trigger {
    TODO()
}

fun <V, R> Cell<V>.actuateOf(
    transform: (V) -> Effect<R>,
): Effect<Cell<R>> = map(transform).actuate()

fun <V> Cell<V>.activateOf(
    transform: (V) -> Trigger,
): Trigger = map(transform).activate()

// TODO: Add tests
fun <ResultT> Effect<ResultT>.startBound(
    target: Any,
): ResultT {
    TODO()
}

fun <V> Future<Effect<V>>.waitEffectively(): Effect<Future<V>> = mapRe { valueEffect: Effect<V> ->
    valueEffect.start()
}.joinOf { effectiveFuture: Future<Effective<V>> ->
    Effect.pure(
        effective = Effective(
            result = effectiveFuture.map { it.result }, handle = Effect.Handle.current(
                handle = effectiveFuture.resultOrNull.map {
                    it?.handle ?: Effect.Handle.Noop
                },
            )
        )
    )
}
