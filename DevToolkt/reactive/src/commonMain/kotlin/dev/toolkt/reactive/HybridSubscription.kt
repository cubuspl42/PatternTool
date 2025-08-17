package dev.toolkt.reactive

import dev.toolkt.core.platform.PlatformCleanable
import dev.toolkt.core.platform.PlatformFinalizationRegistry
import dev.toolkt.core.platform.PlatformWeakReference
import dev.toolkt.reactive.event_stream.EventSource
import dev.toolkt.reactive.event_stream.TargetingListener
import dev.toolkt.reactive.managed_io.Transaction

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
        private val finalizationRegistry = PlatformFinalizationRegistry()

        internal fun <TargetT : Any, EventT> EventSource<EventT>.listenHybrid(
            target: TargetT,
            targetingListener: TargetingListener<TargetT, EventT>,
        ): HybridSubscription = object : HybridSubscription {
            private var currentMode: StrengthMode<TargetT> = enterWeakMode(
                target = target,
            )

            private var innerSubscription: Subscription? = this@listenHybrid.listen(
                listener = object : Listener<EventT> {
                    override fun handle(
                        transaction: Transaction,
                        event: EventT,
                    ) {
                        // If the target was collected, we assume that this listener
                        // will soon be removed. For now, let's just ignore the event.
                        val target = currentMode.getReachableTarget() ?: return

                        targetingListener.handle(
                            transaction = transaction,
                            target = target,
                            event = event,
                        )
                    }
                },
            )

            override fun cancel() {
                val weakMode = currentMode as? StrengthMode.Weak<TargetT>
                weakMode?.cleanable?.unregister()

                innerSubscription?.cancel()
                this.innerSubscription = null
            }

            override fun strengthen(): StrengthenResult {
                val weakMode = this.currentMode as? StrengthMode.Weak<TargetT> ?: throw IllegalStateException(
                    "Cannot strengthen a subscription that is not weak."
                )

                val target = weakMode.weakTarget.get() ?: return StrengthenResult.Collected
                val cleanable = weakMode.cleanable

                // Unregister the cleanable, removing stress from the finalization register. It should be impossible
                // to observe actual finalization when the hybrid subscription is in the strong mode, though, as we
                // hold a strong reference to the target object then.
                cleanable.unregister()

                this.currentMode = StrengthMode.Strong(
                    target = target,
                )

                return StrengthenResult.Strengthened
            }

            override fun weaken() {
                val strongMode = this.currentMode as? StrengthMode.Strong<TargetT> ?: throw IllegalStateException(
                    "Cannot weaken a subscription that is not strong."
                )

                this.currentMode = enterWeakMode(
                    target = strongMode.target,
                )
            }

            private fun enterWeakMode(
                target: TargetT,
            ): StrengthMode.Weak<TargetT> {
                val cleanable = finalizationRegistry.register(
                    target = target,
                    cleanup = {
                        val subscription = innerSubscription
                            ?: throw IllegalStateException("The subscription is already cancelled at the time of finalization")

                        subscription.cancel()
                    },
                )

                return StrengthMode.Weak(
                    target = target,
                    cleanable = cleanable,
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
        val cleanable: PlatformCleanable,
    ) : StrengthMode<TargetT>() {
        val weakTarget = PlatformWeakReference(target)

        override fun getReachableTarget(): TargetT? = weakTarget.get()
    }

    /**
     * The subscription is strong, meaning that it will ensure that the
     * target is not collected by the garbage collector as long as the
     * subscription is active.
     */
    data class Strong<TargetT : Any>(
        val target: TargetT,
    ) : StrengthMode<TargetT>() {
        override fun getReachableTarget(): TargetT? = target
    }

    abstract fun getReachableTarget(): TargetT?
}
