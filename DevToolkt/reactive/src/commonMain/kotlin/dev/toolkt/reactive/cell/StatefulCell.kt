package dev.toolkt.reactive.cell

import dev.toolkt.core.platform.PlatformWeakReference
import dev.toolkt.reactive.Listener
import dev.toolkt.reactive.ListenerFn
import dev.toolkt.reactive.Subscription
import dev.toolkt.reactive.event_stream.DependentEventStream
import dev.toolkt.reactive.event_stream.EventStream
import dev.toolkt.reactive.event_stream.pinWeak
import dev.toolkt.reactive.managed_io.MomentContext
import dev.toolkt.reactive.managed_io.Transaction

/**
 * A cell with an inherent state, i.e. such that is not a pure function of its sources (that cannot be recomputed).
 */
abstract class StatefulCell<V>(
    initialValue: V,
    givenValues: EventStream<V>,
) : ProperCell<V>() {
    private var storedValue: V = initialValue

    final override val newValues: EventStream<V> = object : DependentEventStream<V>() {
        // The `newValues` stream cannot store a strong reference to its outer cell, as that would keep it alive even
        // when maintaining state is not needed anymore (as no objects have a proper reference to that cell anymore).
        private val weakCell = PlatformWeakReference(this@StatefulCell)

        private val self = this // Kotlin doesn't ofer a label for `this@DependentEventStream` (why?)

        override fun observe() = givenValues.listen(
            listener = object : Listener<V> {
                override fun handle(
                    transaction: Transaction,
                    event: V,
                ) {
                    val newValue = event

                    // Notify the listeners about the new value (if there are any)
                    self.notify(
                        transaction = transaction,
                        event = event,
                    )

                    // ...and store it (only if needed)
                    val cell = weakCell.get() ?: return

                    transaction.enqueueMutation {
                        cell.storedValue = newValue
                    }
                }
            }
        )

        init {
            // Pin the event stream to keep the stored value up-to-date even when there are no listeners
            self.pinWeak(target = this@StatefulCell)
        }
    }

    final override val currentValueUnmanaged: V
        get() = storedValue

    context(momentContext: MomentContext) final override fun sample(): V = storedValue
}
