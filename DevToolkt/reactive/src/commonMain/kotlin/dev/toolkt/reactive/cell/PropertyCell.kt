package dev.toolkt.reactive.cell

import dev.toolkt.reactive.Listener
import dev.toolkt.reactive.event_stream.EventStream
import dev.toolkt.reactive.event_stream.hold
import dev.toolkt.reactive.future.Future
import dev.toolkt.reactive.managed_io.MomentContext
import dev.toolkt.reactive.managed_io.Transaction

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

            fun set(newValue: ValueT) {
                mutableValue.setUnmanaged(newValue = newValue)
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

    fun bindUntil(
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
            listener = object : Listener<Any?> {
                override fun handle(
                    transaction: Transaction,
                    event: Any?,
                ) {
                    val finalUnboundState = State.Unbound(
                        initialValue = newBoundState.exposedValue.currentValueUnmanaged,
                    )

                    mutableState.setUnmanaged(finalUnboundState)
                }
            },
        )

        mutableState.setUnmanaged(newBoundState)
    }

    fun bindUntil(
        newValues: EventStream<ValueT>,
        until: Future<Unit>,
    ) {
        bindUntil(
            boundValue = newValues.hold(currentValueUnmanaged),
            until = until,
        )
    }

    override val newValues: EventStream<ValueT>
        get() = exposedValue.newValues

    context(momentContext: MomentContext) override fun sample(): ValueT = exposedValue.sample()

    override val currentValueUnmanaged: ValueT
        get() = exposedValue.currentValueUnmanaged
}
