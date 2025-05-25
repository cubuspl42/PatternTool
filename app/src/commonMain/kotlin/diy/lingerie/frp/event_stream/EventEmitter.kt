package diy.lingerie.frp.event_stream

import diy.lingerie.frp.vertices.Vertex
import diy.lingerie.frp.vertices.event_stream.EmitterVertex

class EventEmitter<E> : ActiveEventStream<E>() {
    override val vertex: Vertex<E> = EmitterVertex()

    fun emit(event: E) {
        vertex.notify(event)
    }
}
