package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.HybridSubscription

/**
 * An event stream that maintains some internal state which might affect future event occurrences.
 *
 * When this stream has listeners, we need a subscription (which is clear), but it has to be strong one. In a corner case
 * all its listeners might be weak. In such a case, there's no down-to-up chain of strong references from down. To keep
 * the system alive, the strong reference chain must start at the upstream.
 *
 * When this stream doesn't have listeners, we still need an upstream subscription, as some object might have a reference
 * to this stream, but haven't installed a listener (yet?). As the inherent internal stream state might affect all future
 * event occurrences, it must be kept up-to-date. At the same time, that subscription _cannot_ be strong, as it would keep
 * this stream alive indefinitely, even when no other objects have a proper reference to it (and, in consequence, can't
 * start listening).
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

    abstract fun bind(): SourcedListener<TargetT, EventT>
}
