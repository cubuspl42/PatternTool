package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.Listener
import dev.toolkt.reactive.Subscription
import dev.toolkt.reactive.effect.ActionContext
import dev.toolkt.reactive.effect.Effect

context(actionContext: ActionContext) fun <EventT> EventStream<EventT>.watch(
    listener: Listener<EventT>,
): Effect.Handle = WatchEffectHandle.construct(
    source = this,
    listener = listener,
)

internal class WatchEffectHandle private constructor() : Effect.Handle {
    companion object {
        context(actionContext: ActionContext) fun <EventT> construct(
            source: EventStream<EventT>,
            listener: Listener<EventT>,
        ): WatchEffectHandle = WatchEffectHandle().apply {
            actionContext.enqueueMutation {
                this.initialize(
                    subscription = source.listen(
                        listener = listener,
                    ),
                )
            }
        }
    }

    private lateinit var subscription: Subscription

    private fun initialize(
        subscription: Subscription,
    ) {
        this.subscription = subscription
    }

    context(actionContext: ActionContext) override fun end() {
        if (this::subscription.isInitialized) {
            actionContext.enqueueMutation {
                subscription.cancel()
            }
        } else {
            throw IllegalStateException("Cannot end an effect that hasn't fully started")
        }
    }
}
