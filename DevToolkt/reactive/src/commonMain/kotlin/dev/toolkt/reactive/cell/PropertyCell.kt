package dev.toolkt.reactive.cell

import dev.toolkt.reactive.UnconditionalListener
import dev.toolkt.reactive.cell.PropertyCell.State.Unbound
import dev.toolkt.reactive.effect.ActionContext
import dev.toolkt.reactive.effect.MomentContext
import dev.toolkt.reactive.effect.Transaction
import dev.toolkt.reactive.event_stream.EventStream
import dev.toolkt.reactive.future.Future

class PropertyCell<ValueT> private constructor(
    private val mutableState: MutableCell<PropertyCell.State<ValueT>>,
) : ProperCell<ValueT>() {
    companion object {
        context(momentContext: MomentContext) fun <ValueT> create(
            initialValue: ValueT,
        ): PropertyCell<ValueT> = PropertyCell(
            mutableState = MutableCell.create(
                State.Unbound.enter(initialValue = initialValue),
            ),
        )
    }

    sealed class State<out ValueT> {
        class Unbound<ValueT> private constructor(
            private val mutableValue: MutableCell<ValueT>,
        ) : State<ValueT>() {
            companion object {
                context(momentContext: MomentContext) fun <ValueT> enter(
                    initialValue: ValueT,
                ): Unbound<ValueT> = Unbound(
                    mutableValue = MutableCell.create(initialValue = initialValue),
                )
            }

            override val exposedValue: Cell<ValueT>
                get() = mutableValue

            context(actionContext: ActionContext) fun set(newValue: ValueT) {
                mutableValue.set(newValue = newValue)
            }
        }

        class Bound<ValueT> private constructor(
            private val boundValue: Cell<ValueT>,
        ) : State<ValueT>() {
            companion object {
                fun <ValueT> enter(
                    boundValue: Cell<ValueT>,
                ): Bound<ValueT> = Bound(
                    boundValue = boundValue,
                )
            }

            override val exposedValue: Cell<ValueT>
                get() = boundValue
        }

        abstract val exposedValue: Cell<ValueT>
    }

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

        val newBoundState = State.Bound.enter(
            boundValue = boundValue,
        )

        until.onFulfilled.listen(
            listener = object : UnconditionalListener<Any?>() {
                override fun handleUnconditionally(
                    transaction: Transaction,
                    event: Any?,
                ) {
                    val finalUnboundState = State.Unbound.enter(
                        initialValue = newBoundState.exposedValue.sample(),
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
