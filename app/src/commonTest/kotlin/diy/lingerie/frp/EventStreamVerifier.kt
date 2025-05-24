package diy.lingerie.frp

class EventStreamVerifier<E>(
    eventStream: EventStream<E>,
) {
    private val mutableReceivedEvents = mutableListOf<E>()

    init {
        eventStream.subscribeSemiBound(
            target = this,
            listener = object : Listener<E> {
                override fun handle(event: E) {
                    mutableReceivedEvents.add(event)
                }
            },
        )
    }

    fun removeReceivedEvents(): List<E> {
        val receivedEvents = mutableReceivedEvents.toList()

        mutableReceivedEvents.clear()

        return receivedEvents
    }
}
