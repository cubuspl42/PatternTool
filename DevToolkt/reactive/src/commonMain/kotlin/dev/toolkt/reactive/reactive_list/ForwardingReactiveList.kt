package dev.toolkt.reactive.reactive_list

import dev.toolkt.reactive.event_stream.EventStream

class ForwardingReactiveList<E>(
    private val operator: ReactiveListPureOperator<E>,
) : ActiveReactiveList<E>() {
    override val currentElements: List<E>
        get() = operator.getCurrentContent()

    override val changes: EventStream<Change<E>> = operator.getChanges()
}
