package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.Subscription

interface BoundListener {
    fun listen(): Subscription

    fun listenWeak(): Subscription
}
