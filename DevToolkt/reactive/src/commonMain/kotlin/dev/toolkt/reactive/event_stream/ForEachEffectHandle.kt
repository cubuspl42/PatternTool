package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.Subscription
import dev.toolkt.reactive.UnconditionalListener
import dev.toolkt.reactive.managed_io.ActionContext
import dev.toolkt.reactive.managed_io.Effect
import dev.toolkt.reactive.managed_io.Transaction

internal class ForEachEffectHandle private constructor() : Effect.Handle {
    companion object {
        context(actionContext: ActionContext) fun <EventT> construct(
            source: EventStream<EventT>,
            action: context(ActionContext) (EventT) -> Unit,
        ): ForEachEffectHandle = ForEachEffectHandle().apply {
            actionContext.enqueueMutation {
                this.initialize(
                    subscription = source.listen(
                        listener = object : UnconditionalListener<EventT>() {
                            override fun handleUnconditionally(
                                transaction: Transaction,
                                event: EventT,
                            ) {
                                with(transaction) {
                                    action(event)
                                }
                            }
                        },
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
