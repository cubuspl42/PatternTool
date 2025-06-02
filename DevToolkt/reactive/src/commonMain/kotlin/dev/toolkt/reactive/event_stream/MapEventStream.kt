package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.DrainedEventStreamState
import dev.toolkt.reactive.EventHandler
import dev.toolkt.reactive.FreshEventStreamState
import dev.toolkt.reactive.SucceededEventStreamState
import dev.toolkt.reactive.SuspendedEventStreamState

class MapEventStream<E, Er>(
    private var source: EventStream<E>,
    private val transform: (E) -> Er,
) : TransformingEventStream<E, Er>(
    source = source,
) {
    override fun transformEvent(event: E) {
        notify(transform(event))
    }
}


class MapEventStreamNg<SourceEventT, TransformedEventT>(
    private val transform: (SourceEventT) -> TransformedEventT,
    private var source: EventStreamNg<SourceEventT>,
) : EventStreamNgImpl<
        MapEventStreamOperator.MapParams<SourceEventT, TransformedEventT>,
        MapEventStreamOperator.MapDeps<SourceEventT>,
        TransformedEventT,
        >(
    params = MapEventStreamOperator.MapParams(transform = transform),
    deps = MapEventStreamOperator.MapDeps(source = source),
) {
    override fun buildFreshState(): FreshEventStreamState<TransformedEventT> {
        val (newSource, freshState) = buildFreshStateForSource(source = source)

        source = newSource

        return freshState
    }

    override val operator = MapEventStreamOperator<SourceEventT, TransformedEventT>()

    private fun buildFreshStateForSource(
        source: EventStreamNg<SourceEventT>,
    ): Pair<EventStreamNg<SourceEventT>, FreshEventStreamState<TransformedEventT>> {
        val sourceState = source.getFreshState()

        when (sourceState) {
            is SucceededEventStreamState<SourceEventT> -> buildFreshStateForSource(
                source = sourceState.successorEventStream,
            )

            DrainedEventStreamState -> Pair(Never, DrainedEventStreamState)

            else -> Pair(source, SuspendedEventStreamState)
        }
    }
}

class MapEventStreamOperator<
        SourceEventT,
        TransformedEventT,
        >() : EventStreamOperator<
        MapEventStreamOperator.MapParams<SourceEventT, TransformedEventT>,
        MapEventStreamOperator.MapDeps<SourceEventT>,
        TransformedEventT,
        >() {
    override fun operate(
        params: MapParams<SourceEventT, TransformedEventT>,
        deps: MapDeps<SourceEventT>,
        propagationController: EventPropagationController<TransformedEventT>
    ): OperateResponse<MapDeps<SourceEventT>> {
        val sourceSubscriptionResponse = deps.source.subscribe(
            eventHandler = object : EventHandler<SourceEventT> {
                override fun handleEvent(event: SourceEventT) {
                    propagationController.propagateEvent(
                        event = params.transform(event),
                    )
                }

                override fun handleStop() {
                    propagationController.stop()
                }
            },
        )

        val sourceSubscriptionStartedResponse = when (sourceSubscriptionResponse) {
            EventStreamNg.StreamDrainedResponse -> return DepsDrainedResponse

            is EventStreamNg.SubscriptionStartedResponse<SourceEventT> -> sourceSubscriptionResponse
        }

        val updatedDeps = sourceSubscriptionResponse.updatedEventStream?.let { updatedSource ->
            MapDeps(
                source = updatedSource,
            )
        }

        return OperationStartedResponse(
            updatedDeps = updatedDeps,
            upstreamSubscription = sourceSubscriptionStartedResponse.subscription,
        )
    }

    data class MapParams<in SourceEventT, out TransformedEventT>(
        val transform: (SourceEventT) -> TransformedEventT,
    ) : EventStreamNgImpl.Params

    data class MapDeps<out SourceEventT>(
        val source: EventStreamNg<SourceEventT>,
    ) : EventStreamNgImpl.Deps

}
