package dev.toolkt.reactive.cell

import dev.toolkt.reactive.event_stream.BoundListener
import dev.toolkt.reactive.event_stream.EventStream
import dev.toolkt.reactive.event_stream.StatefulEventStream
import dev.toolkt.reactive.event_stream.TargetingListener
import dev.toolkt.reactive.event_stream.bind

/**
 * A cell with an inherent state, i.e. such that is not a pure function of its sources (that cannot be recomputed).
 */
abstract class StatefulCell<V>(
    initialValue: V,
    givenValues: EventStream<V>,
) : ProperCell<V>() {
    private var storedValue: V = initialValue

    // The `newValues` stream cannot store a strong reference to its outer cell, as that would keep it alive even
    // when maintaining state is not needed anymore (as no objects have a proper reference to that cell anymore).
    final override val newValues: EventStream<V> = object : StatefulEventStream<V>() {
        private val self = this // Kotlin doesn't ofer a label for `this@HybridStatefulEventStream` (why?)

        override fun bind(): BoundListener = givenValues.bind(
            listener = object : TargetingListener<StatefulCell<V>, V> {
                override fun handle(
                    target: StatefulCell<V>,
                    event: V,
                ) {
                    self.notify(event = event)

                    target.storedValue = event
                }
            },
            target = this@StatefulCell,
        )

        init {
            init()
        }
    }

    override val currentValue: V
        get() = storedValue
}
