package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.Listener
import dev.toolkt.reactive.Subscription
import dev.toolkt.reactive.cell.MutableCell
import dev.toolkt.reactive.managed_io.ReactionContext
import dev.toolkt.reactive.managed_io.Reactions


// TODO: Add tests
class EventStreamSlot<EventT>(
    private val mutableEventStream: MutableCell<EventStream<EventT>>,
) : ProperEventStream<EventT>() {
    companion object {
        context(reactionContext: ReactionContext) fun <EventT> create(): EventStreamSlot<EventT> {
            val mutableEventStream = MutableCell.create<EventStream<EventT>>(
                initialValue = EventStream.Never,
            )

            return EventStreamSlot(
                mutableEventStream = mutableEventStream,
            )
        }
    }

    override fun listen(
        listener: Listener<EventT>,
    ): Subscription {
        TODO("Not yet implemented")
    }

    context(reactionContext: ReactionContext) fun bind(
        eventStream: EventStream<EventT>,
    ) = Reactions.defer {
        mutableEventStream.set(eventStream)
    }
}


