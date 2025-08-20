package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.HybridSubscription
import dev.toolkt.reactive.Subscription

/**
 * TODO: Nuke
 */
interface BoundListener {
    fun listen(): Subscription

    fun listenWeak(): Subscription

    fun listenHybrid(): HybridSubscription
}
