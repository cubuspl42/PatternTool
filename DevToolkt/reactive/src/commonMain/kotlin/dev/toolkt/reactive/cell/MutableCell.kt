package dev.toolkt.reactive.cell

import dev.toolkt.reactive.event_stream.EventEmitter
import dev.toolkt.reactive.event_stream.EventStream
import dev.toolkt.reactive.managed_io.ProactionContext
import dev.toolkt.reactive.managed_io.MomentContext
import dev.toolkt.reactive.managed_io.ReactionContext
import dev.toolkt.reactive.managed_io.Reactions

// TODO: Make the constructor private
class MutableCell<V>(
    initialValue: V,
) : ProperCell<V>() {
    companion object {
        /**
         * Creates a new [MutableCell] with the given [initialValue].
         *
         * [MomentContext] is needed only to give the mutable cell its identity.
         */
        context(momentContext: MomentContext) fun <V> create(
            initialValue: V,
        ): MutableCell<V> = MutableCell(
            initialValue = initialValue,
        )
    }

    private val newValueEmitter = EventEmitter<V>()

    private var mutableValue: V = initialValue

    val hasListeners: Boolean
        get() = newValueEmitter.hasListeners

    override val newValues: EventStream<V>
        get() = newValueEmitter

    override val currentValue: V
        get() = mutableValue

    context(proactionContext: ProactionContext) fun set(
        newValue: V,
    ) {

//        reactionContext.enqueueMutation {
//            mutableValue = newValue
//        }

        newValueEmitter.emit(newValue)
    }

    fun setUnmanaged(
        newValue: V,
    ) {
        newValueEmitter.emitUnmanaged(newValue)
        mutableValue = newValue
    }
}

context(proactionContext: ReactionContext) fun <V> MutableCell<V>.setLater(
    newValue: V,
) {
    // Thought: Does it really make any difference? Shouldn't reaction and proaction context be merged?
    Reactions.defer {
        set(newValue)
    }
}
