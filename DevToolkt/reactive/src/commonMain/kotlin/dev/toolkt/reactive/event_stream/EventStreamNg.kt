package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.EventHandler
import dev.toolkt.reactive.EventStreamState
import dev.toolkt.reactive.Subscription

interface EventPropagationController<in EventT> {
    fun propagateEvent(event: EventT)

    fun stop()
}


abstract class EventStreamNg<out EventT> {

    data object Never : EventStreamNg<Nothing>()


    sealed interface SubscriptionResponse<out EventT>

    data object StreamDrainedResponse : SubscriptionResponse<Nothing>

    data class SubscriptionStartedResponse<out EventT>(
        val updatedEventStream: EventStreamNg<EventT>?,
        val subscription: Subscription,
    ): SubscriptionResponse<EventT>



    data class StateSubscriptionResponse<out EventT>(
        val outerResponse: SubscriptionStartedResponse<EventT>,
        val newState: EventStreamState<EventT>,
    )


    abstract fun subscribe(
        eventHandler: EventHandler<EventT>,
    ): SubscriptionResponse<EventT>


}

