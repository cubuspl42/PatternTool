package dev.toolkt.reactive

import dev.toolkt.reactive.effect.ActionContext
import dev.toolkt.reactive.effect.Effect

interface Subscription {
    object Noop : Subscription {
        override fun cancel() {
        }
    }

    fun cancel()
}

fun Subscription.toEffectHandle(): Effect.Handle = object : Effect.Handle {
    context(actionContext: ActionContext) override fun end() {
        actionContext.transaction.enqueueMutation {
            this@toEffectHandle.cancel()
        }
    }
}
