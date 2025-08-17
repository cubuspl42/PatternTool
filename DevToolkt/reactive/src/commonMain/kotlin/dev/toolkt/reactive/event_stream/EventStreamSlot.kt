package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.Listener
import dev.toolkt.reactive.Subscription
import dev.toolkt.reactive.cell.MutableCell
import dev.toolkt.reactive.cell.setLater
import dev.toolkt.reactive.managed_io.ReactionContext

class EventStreamSlot<EventT>(
    private val mutableEventStream: MutableCell<EventStream<EventT>>,
    private val divertedEventStream: EventStream<EventT>,
) : ProperEventStream<EventT>() {
    companion object {
        context(reactionContext: ReactionContext) fun <EventT> create(): EventStreamSlot<EventT> {
            val mutableEventStream = MutableCell.create<EventStream<EventT>>(
                initialValue = EventStream.Never,
            )

            val divertedEventStream = EventStream.divert(mutableEventStream)

            return EventStreamSlot(
                mutableEventStream = mutableEventStream,
                divertedEventStream = divertedEventStream,
            )
        }
    }

    override fun listen(
        listener: Listener<EventT>,
    ): Subscription = divertedEventStream.listen(
        listener = listener,
    )

    context(reactionContext: ReactionContext) fun bind(
        eventStream: EventStream<EventT>,
    ) {
        mutableEventStream.setLater(eventStream)
    }
}
