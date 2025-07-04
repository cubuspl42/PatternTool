package dev.toolkt.reactive

import dev.toolkt.core.platform.PlatformWeakReference
import dev.toolkt.reactive.event_stream.EventSource
import dev.toolkt.reactive.event_stream.TargetingListener

interface HybridSubscription : Subscription {
    /**
     * The result of a subscription strengthening operation.
     */
    enum class StrengthenResult {
        /**
         * The subscription was successfully strengthened.
         */
        Strengthened,

        /**
         * The subscription could not be strengthened because the target
         * was already collected. The subscription won't ever be able to
         * strengthen again and should be removed.
         */
        Collected,
    }

    companion object {
        fun <TargetT : Any, EventT> EventSource<EventT>.listenHybrid(
            target: TargetT,
            targetingListener: TargetingListener<TargetT, EventT>,
        ): HybridSubscription = object : HybridSubscription {
            private var currentMode: StrengthMode<TargetT> = StrengthMode.Weak(
                target = target,
            )

            private var innerSubscription: Subscription? = this@listenHybrid.listen(
                listener = object : Listener<EventT> {
                    override fun handle(event: EventT) {
                        // If the target was collected, we assume that this listener
                        // will soon be removed. For now, let's just ignore the event.
                        // TODO: Actually implement finalization registry listener removal
                        val target = currentMode.target ?: return

                        targetingListener.handle(
                            target = target,
                            event = event,
                        )
                    }
                },
            )

            override fun cancel() {
                val innerSubscription = this.innerSubscription ?: throw IllegalStateException(
                    "The hybrid subscription is already cancelled"
                )

                innerSubscription.cancel()
                this.innerSubscription = null
            }

            override fun strengthen(): StrengthenResult {
                val currentMode = this.currentMode as? StrengthMode.Weak<TargetT> ?: throw IllegalStateException(
                    "Cannot strengthen a subscription that is not weak."
                )

                val target = currentMode.weakTarget.get() ?: return StrengthenResult.Collected

                this.currentMode = StrengthMode.Strong(
                    target = target,
                )

                return StrengthenResult.Strengthened
            }

            override fun weaken() {
                val currentMode = this.currentMode as? StrengthMode.Strong<TargetT> ?: throw IllegalStateException(
                    "Cannot weaken a subscription that is not strong."
                )

                val target = currentMode.target

                this.currentMode = StrengthMode.Weak(
                    target = target,
                )
            }
        }
    }

    fun strengthen(): StrengthenResult

    fun weaken()
}

private sealed class StrengthMode<TargetT : Any> {
    /**
     * The subscription is weak, meaning that it will not prevent the
     * target from being collected by the garbage collector.
     */
    class Weak<TargetT : Any>(
        target: TargetT,
    ) : StrengthMode<TargetT>() {
        val weakTarget = PlatformWeakReference(target)

        override val target: TargetT?
            get() = weakTarget.get()
    }

    /**
     * The subscription is strong, meaning that it will ensure that the
     * target is not collected by the garbage collector as long as the
     * subscription is active.
     */
    class Strong<TargetT : Any>(
        override val target: TargetT,
    ) : StrengthMode<TargetT>()

    abstract val target: TargetT?
}
