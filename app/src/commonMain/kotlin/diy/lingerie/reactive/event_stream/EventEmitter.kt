package diy.lingerie.reactive.event_stream

import diy.lingerie.reactive.vertices.Vertex
import diy.lingerie.reactive.vertices.event_stream.EmitterVertex

class EventEmitter<E> : ActiveEventStream<E>() {
    override val vertex: Vertex<E> = EmitterVertex()

    fun emit(event: E) {
        vertex.notify(event)
    }
}
