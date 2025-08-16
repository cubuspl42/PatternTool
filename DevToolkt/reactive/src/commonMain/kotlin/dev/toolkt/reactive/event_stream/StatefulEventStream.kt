package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.HybridSubscription

/**
 * An event stream that maintains some internal state which might affect future event occurrences.
 *
 * When this stream has listeners, we need a strong subscription to the upstream, because having listeners doesn't
 * actually guarantee that any object in the whole system has a reference to this object. All the listeners of this
 * stream (potentially even one listener) might be weak, i.e. update the state of an external target object based on the
 * reachability of that object, without storing a reference to this stream. To keep the external target objects up-to-date
 * and keep the reactive system alive, we need to store a chain of references from the upstream size.
 *
 * FIXME: IS THIS TRUE? Shouldn't we expect a reference in the finalization registry?
 *
 * When this stream doesn't have listeners, we still need an upstream subscription, as some object might have a reference
 * to this stream, but haven't installed a listener (yet?). As the inherent internal stream state might affect all future
 * event occurrences, it must be kept up-to-date. At the same time, that subscription _cannot_ be strong, as it would keep
 * this stream alive indefinitely, even when the upstream never emits any event and no other objects have a proper
 * reference to this stream (and, in consequence, can't start listening).
 */
abstract class StatefulEventStream<TargetT : Any, EventT>() : ManagedEventStream<EventT>() {
    private lateinit var hybridSubscription: HybridSubscription

    final override fun onResumed() {
        when (hybridSubscription.strengthen()) {
            HybridSubscription.StrengthenResult.Strengthened -> {
                // All good
            }

            HybridSubscription.StrengthenResult.Collected -> {
                // FIXME: Aborting hold/newValues is not right; is hold/newValues a proper stateful stream?>
                abort()
            }
        }
    }

    final override fun onPaused() {
        hybridSubscription.weaken()
    }

    final override fun onAborted() {
        hybridSubscription.cancel()
    }

    protected fun init(
        target: TargetT,
    ) {
        if (this::hybridSubscription.isInitialized) {
            throw AssertionError("The hybrid subscription is already initialized")
        }

        hybridSubscription = bind().bindTarget(
            target = target,
        ).listenHybrid()
    }

    abstract fun bind(): ISourcedListener<TargetT>
}
