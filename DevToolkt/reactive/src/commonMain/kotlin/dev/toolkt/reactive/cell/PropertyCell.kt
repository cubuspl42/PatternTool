package dev.toolkt.reactive.cell

import dev.toolkt.reactive.UnconditionalListener
import dev.toolkt.reactive.event_stream.EventStream
import dev.toolkt.reactive.future.Future
import dev.toolkt.reactive.effect.ActionContext
import dev.toolkt.reactive.effect.MomentContext
import dev.toolkt.reactive.effect.Transaction

class PropertyCell<ValueT>(
    initialValue: ValueT,
) : ProperCell<ValueT>() {
    sealed class State<out ValueT> {
        class Unbound<ValueT>(
            initialValue: ValueT,
        ) : State<ValueT>() {
            private val mutableValue = MutableCell(initialValue = initialValue)

            override val exposedValue: Cell<ValueT>
                get() = mutableValue

            context(actionContext: ActionContext) fun set(newValue: ValueT) {
                mutableValue.set(newValue = newValue)
            }
        }

        class Bound<ValueT>(
            private val boundValue: Cell<ValueT>,
        ) : State<ValueT>() {
            override val exposedValue: Cell<ValueT>
                get() = boundValue
        }

        abstract val exposedValue: Cell<ValueT>
    }

    private val mutableState = MutableCell<State<ValueT>>(
        State.Unbound(initialValue = initialValue),
    )

    val state: Cell<State<ValueT>>
        get() = mutableState

    val exposedValue = state.switchOf { it.exposedValue }

    context(actionContext: ActionContext) fun bindUntil(
        boundValue: Cell<ValueT>,
        until: Future<Unit>,
    ) {
        if (state.currentValueUnmanaged is State.Bound<*>) {
            throw IllegalStateException("The property is already bound")
        }

        val newBoundState = State.Bound(
            boundValue = boundValue,
        )

        until.onFulfilled.listen(
            listener = object : UnconditionalListener<Any?>() {
                override fun handleUnconditionally(
                    transaction: Transaction,
                    event: Any?,
                ) {
                    val finalUnboundState = State.Unbound(
                        initialValue = newBoundState.exposedValue.currentValueUnmanaged,
                    )

                    // FIXME: Can't we use some other primitives here?
                    with(transaction) {
                        mutableState.set(finalUnboundState)
                    }
                }
            },
        )

        mutableState.set(newBoundState)
    }

    override val newValues: EventStream<ValueT>
        get() = exposedValue.newValues

    context(momentContext: MomentContext) override fun sample(): ValueT = exposedValue.sample()

    override val currentValueUnmanaged: ValueT
        get() = exposedValue.currentValueUnmanaged
}
