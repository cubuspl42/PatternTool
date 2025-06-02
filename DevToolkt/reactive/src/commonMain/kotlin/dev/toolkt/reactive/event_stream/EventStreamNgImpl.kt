package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.ActiveEventStreamState
import dev.toolkt.reactive.DrainedEventStreamState
import dev.toolkt.reactive.EventHandler
import dev.toolkt.reactive.EventStreamState
import dev.toolkt.reactive.SucceededEventStreamState
import dev.toolkt.reactive.SuspendedEventStreamState
import dev.toolkt.reactive.TerminalEventStreamState

abstract class EventStreamNgImpl<
        ParamsT : EventStreamNgImpl.Params,
        DepsT : EventStreamNgImpl.Deps,
        out EventT,
        >(
    params: ParamsT,
    deps: DepsT,
) : EventStreamNg<EventT>() {

    interface Deps

    interface Params

    private var state: EventStreamState<EventT> = SuspendedEventStreamState

    override fun subscribe(
        eventHandler: EventHandler<EventT>,
    ): SubscriptionResponse<EventT> = when (val foundState = this.state) {
        SuspendedEventStreamState -> subscribeWhenSuspended(
            eventHandler = eventHandler,
        )

        is ActiveEventStreamState<EventT> -> subscribeWhenActive(
            activeState = foundState,
            eventHandler = eventHandler,
        )

        is SucceededEventStreamState<EventT> -> subscribeWhenSucceeded(
            succeededEventStreamState = foundState,
            eventHandler = eventHandler,
        )

        DrainedEventStreamState -> subscribeWhenDrained()
    }

    private fun subscribeWhenSuspended(
        eventHandler: EventHandler<EventT>,
    ): SubscriptionStartedResponse<EventT> = activate(
        eventHandler = eventHandler,
    )

    private fun activate(
        eventHandler: EventHandler<EventT>,
    ): SubscriptionStartedResponse<EventT> {
        val operateResponse = operator.operate(
            params = null,
            deps = null,
            propagationController = object : EventPropagationController<@UnsafeVariance EventT> {
                override fun propagateEvent(event: @UnsafeVariance EventT) {
                    TODO("Not yet implemented")
                }

                override fun stop() {
                    TODO("Not yet implemented")
                }
            },
        )

        val (activeState, downstreamSubscription) = ActiveEventStreamState.Companion.build(
            upstreamSubscription = null,
            firstEventHandler = eventHandler,
            onDeactivated = {
                changeState(newState = SuspendedEventStreamState)
            },
        )

        changeState(newState = activeState)

        return SubscriptionStartedResponse(
            updatedEventStream = this,
            subscription = downstreamSubscription,
        )
    }

    private fun subscribeWhenActive(
        activeState: ActiveEventStreamState<EventT>,
        eventHandler: EventHandler<EventT>,
    ): SubscriptionStartedResponse<EventT> {
        val propagationVertex = activeState.propagationVertex

        propagationVertex.register(
            eventHandler = eventHandler,
        )

        return SubscriptionStartedResponse<EventT>(
            updatedEventStream = Never,
            subscription = null,
        )
    }

    private fun subscribeWhenSucceeded(
        succeededEventStreamState: SucceededEventStreamState<EventT>,
        eventHandler: EventHandler<EventT>,
    ): SubscriptionResponse<EventT> {
        val successorEventStream = succeededEventStreamState.successorEventStream

        return successorEventStream.subscribe(eventHandler = eventHandler)
    }

    private fun subscribeWhenDrained(): StreamDrainedResponse = StreamDrainedResponse

    private fun changeState(
        newState: EventStreamState<EventT>,
    ) {
        when (state) {
            is TerminalEventStreamState<EventT> -> {
                throw IllegalStateException()
            }

            else -> {
                state = newState
            }
        }
    }

    protected abstract val operator: EventStreamOperator<ParamsT, DepsT, EventT>
}