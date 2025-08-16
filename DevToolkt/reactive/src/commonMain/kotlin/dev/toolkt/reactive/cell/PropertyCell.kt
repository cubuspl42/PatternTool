package dev.toolkt.reactive.cell

import dev.toolkt.reactive.Listener
import dev.toolkt.reactive.event_stream.EventStream
import dev.toolkt.reactive.event_stream.hold
import dev.toolkt.reactive.future.Future

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
        if (state.currentValue is State.Bound<*>) {
            throw IllegalStateException("The property is already bound")
        }

        val newBoundState = State.Bound(
            boundValue = boundValue,
        )

        until.onFulfilled.listen(
            listener = object : Listener<Any?> {
                override fun handle(event: Any?) {
                    val finalUnboundState = State.Unbound(
                        initialValue = newBoundState.exposedValue.currentValue,
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
            boundValue = newValues.hold(currentValue),
            until = until,
        )
    }

    override val newValues: EventStream<ValueT>
        get() = exposedValue.newValues

    override val currentValue: ValueT
        get() = exposedValue.currentValue
}
