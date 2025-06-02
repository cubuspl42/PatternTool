package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.Subscription

abstract class EventStreamOperator<
        ParamsT : EventStreamNgImpl.Params,
        DepsT : EventStreamNgImpl.Deps,
        out OutEventT,
        > {
    sealed interface OperateResponse<out DepsT : EventStreamNgImpl.Deps>

    data class OperationStartedResponse<out DepsT : EventStreamNgImpl.Deps>(
        val updatedDeps: DepsT?,
        val upstreamSubscription: Subscription,
    ) : OperateResponse<DepsT>

    data object DepsDrainedResponse : OperateResponse<Nothing>

    abstract fun operate(
        params: ParamsT,
        deps: DepsT,
        propagationController: EventPropagationController<OutEventT>,
    ): OperateResponse<DepsT>
}
